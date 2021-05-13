package fr.naruse.dbapi.sql;

import fr.naruse.dbapi.database.Database;

import java.util.concurrent.*;

public class SQLThread {
    private final ConcurrentLinkedQueue<SQLTask> tasks = new ConcurrentLinkedQueue<>();

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final ExecutorService issueTracker = Executors.newSingleThreadExecutor();
    private static final ExecutorService RUNNABLE_ADDER = Executors.newSingleThreadExecutor();

    private final SQLConnection sqlConnection;
    private boolean isStopping = false;
    private boolean isRequestCancelled = false;
    private int requestSeparator = 50;
    private int size = 0;

    public SQLThread(SQLConnection sqlConnection) {
        this.sqlConnection = sqlConnection;
        runner();
    }

    public void init() {
        this.requestSeparator = this.sqlConnection.getCore().getRequestSeparator();
    }

    private void runner() {
        ScheduledFuture<?> future = this.executor.scheduleAtFixedRate(() -> {
            SQLTask sqlTask;

            while ((sqlTask = this.tasks.poll()) != null) {
                if (this.isStopping || this.isRequestCancelled) {
                    return;
                }

                try {
                    size--;
                    sqlTask.run();
                } catch (Exception e) {
                    sqlConnection.getCore().getSecurityManager().activate(e);
                    e.printStackTrace();
                }
            }
        }, 0, this.requestSeparator, TimeUnit.MILLISECONDS);
        issueTracker.submit(() -> {
            try {
                future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    public void addToQueue(SQLTask sqlTask, Database database) {
        addToQueue(sqlTask, false, database);
    }

    public void addToQueue(SQLTask sqlTask, boolean force, Database database) {
        if (this.isRequestCancelled) {
            return;
        }

        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

        if (force) {
            runThread(sqlTask);
            return;
        }

        if ((database == null || database.isLoaded()) && !this.sqlConnection.isClosed()) {
            RUNNABLE_ADDER.submit(() -> {
                if(database != null){
                    database.incrementCallCount();
                    StringBuilder builder = new StringBuilder();
                    for (StackTraceElement stackTraceElement : stackTraceElements) {
                        builder.append(stackTraceElement.toString()+"\n");
                    }
                    String s = builder.toString();
                    Integer integer = database.getCodeCallCount().get(s);
                    if(integer == null){
                        database.getCodeCallCount().put(s, 1);
                    }else{
                        database.getCodeCallCount().put(s, integer+1);
                    }
                }
                SQLThread.this.tasks.add(sqlTask);
                size++;
            });
        } else {
            runScheduledQueue(sqlTask, database);
        }

        if (this.executor.isShutdown()) {
            runner();
        }
    }

    private void runThread(SQLTask sqlTask) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    sqlTask.run();
                } catch (Exception e) {
                    sqlConnection.getCore().getSecurityManager().activate(e);
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void runScheduledQueue(SQLTask sqlTask, Database database) {
        if (this.isStopping) {
            return;
        }

        this.sqlConnection.getCore().getPlugin().scheduleTask(() -> addToQueue(sqlTask, database), 5);

        if (this.executor.isShutdown()) {
            runner();
        }
    }

    public boolean isRunning() {
        return size != 0;
    }

    public int getQueueSize() {
        return size;
    }

    public void shutdown() {
        this.isStopping = true;
        executor.shutdown();
        issueTracker.shutdown();
    }

    public void preventRequests() {
        this.isRequestCancelled = true;
    }
}

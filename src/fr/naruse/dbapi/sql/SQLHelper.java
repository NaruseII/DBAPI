package fr.naruse.dbapi.sql;

public class SQLHelper {
    private static final String SELECT = "SELECT ";
    private static final String FROM = " FROM ";
    private static final String WHERE = " WHERE ";
    private static final String VALUE = " = ?";

    private SQLHelper() {
        throw new IllegalStateException("Utility class");
    }

    private static String getWhere(String[] whereColumnNames) {
        StringBuilder whereColumns = new StringBuilder(whereColumnNames[0]);
        int length = whereColumnNames.length;

        if (length > 1) {
            for (int i = 1; i < length; i++) {
                whereColumns.append(VALUE).append(" AND ").append(whereColumnNames[i]);
            }
        }

        return whereColumns.toString();
    }

    private static String getSelect(String[] selectedColumnNames) {
        StringBuilder selectedColumns = new StringBuilder(selectedColumnNames[0]);
        int length = selectedColumnNames.length;

        if (length > 1) {
            for (int i = 1; i < length; i++) {
                selectedColumns.append(", ").append(selectedColumnNames[i]);
            }
        }

        return selectedColumns.toString();
    }

    private static String getSet(String[] settedColumnNames) {
        StringBuilder settedColumns = new StringBuilder(settedColumnNames[0]);
        int settedLength = settedColumnNames.length;

        if (settedLength > 1) {
            for (int i = 1; i < settedLength; i++) {
                settedColumns.append(VALUE).append(", ").append(settedColumnNames[i]);
            }
        }

        return settedColumns.toString();
    }

    private static String[] getInsert(String[] insertedColumnNames) {
        StringBuilder columnToCreate = new StringBuilder(insertedColumnNames[0]);
        StringBuilder interrogationCount = new StringBuilder("?");
        int insertedLength = insertedColumnNames.length;

        if (insertedLength > 1) {
            for (int i = 1; i < insertedLength; i++) {
                columnToCreate.append(",").append(insertedColumnNames[i]);
                interrogationCount.append(",?");
            }
        }

        return new String[]{columnToCreate.toString(), interrogationCount.toString()};
    }

    public static String getInsertRequest(String tableName, String[] insertedColumns, String duplicateKey) {
        String[] inserts = getInsert(insertedColumns);

        return "INSERT INTO " + tableName + " (" + inserts[0] + ") VALUES (" + inserts[1] + ")" + duplicateKey + ";";
    }

    public static String getInsertRequest(String tableName, String[] insertedColumns) {
        return getInsertRequest(tableName, insertedColumns, "");
    }

    public static String getInsertRequest(String tableName, String[] insertedColumns, String duplicateKey, int value) {
        return getInsertRequest(tableName, insertedColumns, " ON DUPLICATE KEY UPDATE " + duplicateKey + " = " + value);
    }

    public static String getInsertRequest(String tableName, String[] insertedColumns, String duplicateKey, String operation, int value) {
        return getInsertRequest(tableName, insertedColumns, " ON DUPLICATE KEY UPDATE " + duplicateKey + " = " + duplicateKey + " " + operation + " " + value);
    }

    public static String getSelectRequest(String tableName, String selectedColumn) {
        return SELECT + selectedColumn + FROM + tableName + ";";
    }

    public static String getSelectRequest(String tableName, String[] selectedColumnNames) {
        return getSelectRequest(tableName, getSelect(selectedColumnNames));
    }

    public static String getSelectRequest(String tableName, String selectedColumn, String whereColumn) {
        return SELECT + selectedColumn + FROM + tableName + WHERE + whereColumn + VALUE + ";";
    }

    public static String getSelectRequest(String tableName, String selectedColumn, String[] whereColumnNames) {
        return getSelectRequest(tableName, selectedColumn, getWhere(whereColumnNames));
    }

    public static String getSelectRequest(String tableName, String[] selectedColumnNames, String whereColumn) {
        return  getSelectRequest(tableName, getSelect(selectedColumnNames), whereColumn);
    }

    public static String getSelectRequest(String tableName, String[] selectedColumnNames, String[] whereColumnNames) {
        return getSelectRequest(tableName, getSelect(selectedColumnNames), getWhere(whereColumnNames));
    }

    public static String getUpdateRequest(String tableName, String settedColumn, String whereColumn) {
        return "UPDATE " + tableName + " SET " + settedColumn + VALUE + WHERE + whereColumn + VALUE + ";";
    }

    public static String getUpdateRequest(String tableName, String[] settedColumnNames, String whereColumn) {
        return getUpdateRequest(tableName, getSet(settedColumnNames), whereColumn);
    }

    public static String getUpdateRequest(String tableName, String settedColumn, String[] whereColumnNames) {
        return getUpdateRequest(tableName, settedColumn, getWhere(whereColumnNames));
    }

    public static String getUpdateRequest(String tableName, String[] settedColumnNames, String[] whereColumnNames) {
        return getUpdateRequest(tableName, getSet(settedColumnNames), getWhere(whereColumnNames));
    }

    public static String getDeleteRequest(String tableName, String whereColumn) {
        return "DELETE FROM " + tableName + WHERE + whereColumn + VALUE + ";";
    }

    public static String getDeleteRequest(String tableName, String[] whereColumnNames) {
        return getDeleteRequest(tableName, getWhere(whereColumnNames));
    }

    public static String getTruncateRequest(String tableName) {
        return "TRUNCATE TABLE " + tableName + ";";
    }

    public static String getReplaceRequest(String tableName, String[] replacedColumns, String replacedValue) {
        return "REPLACE INTO " + tableName + "(" + getSelect(replacedColumns) + ") VALUES " + replacedValue + ";";
    }

    public static String getDropRequest(String tableName) {
        return "DROP TABLE " + tableName + ";";
    }
}

package fr.naruse.dbapi.util;

public enum Message {
    B("§5§l[§6DB API§5§l]");

    private final String message;

    Message(String s) {
        this.message = s;
    }

    public String getMessage() {
        return this.message;
    }
}


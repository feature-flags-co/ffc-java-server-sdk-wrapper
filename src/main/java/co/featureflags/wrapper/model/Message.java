package co.featureflags.wrapper.model;

public final class Message {
    private final String status;
    private final String reason;

    private Message(String status, String reason) {
        this.status = status;
        this.reason = reason;
    }

    public static Message OK(String reason) {
        return new Message("OK", reason);
    }

    public static Message ERROR(String reason) {
        return new Message("ERROR", reason);
    }
}

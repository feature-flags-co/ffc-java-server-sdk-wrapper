package co.featureflags.wrapper.model;

import co.featureflags.commons.model.BasicState;

public final class Message extends BasicState {
    private Message(boolean success, String message) {
        super(success, message);
    }

    public static Message ERROR(String message) {
        return new Message(false, message);
    }

    public static Message OK(String message) {
        return new Message(true, message);
    }
}

package alex.osenov.websockets.model;

import com.google.gson.Gson;

public class Message {
    private final String event;
    private final String channel;
    private final String symbol;

    public Message(String event, String channel, String symbol) {
        this.event = event;
        this.channel = channel;
        this.symbol = symbol;
    }

    public String asText() {
        return new Gson().toJson(this);
    }
}

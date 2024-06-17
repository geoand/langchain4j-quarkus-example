package dev.example;

import io.quarkus.websockets.next.OnOpen;
import io.quarkus.websockets.next.OnTextMessage;
import io.quarkus.websockets.next.WebSocket;

@WebSocket(path = "/chat")
public class ChatWebSocket {

    private final CustomerSupportAgent bot;

    public ChatWebSocket(CustomerSupportAgent bot) {
        this.bot = bot;
    }

    @OnOpen
    public String onOpen() {
        return "Hello, I'm the Miles of Smiles customer support agent, how can I assist you?";
    }

    @OnTextMessage
    public String onMessage(String message) {
        return bot.chat(message);
    }

}

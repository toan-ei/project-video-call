package com.example.webrtc.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SocketHandler extends TextWebSocketHandler{
    private final Map<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message)
            throws InterruptedException, IOException {
        JsonNode jsonNode = objectMapper.readTree(message.getPayload());

        String event = jsonNode.has("event") ? jsonNode.get("event").asText() : null;
        String sender = jsonNode.has("sender") ? jsonNode.get("sender").asText() : null;
        String target = jsonNode.has("target") ? jsonNode.get("target").asText() : null;
        String data = jsonNode.has("data") ? jsonNode.get("data").toString() : null;

        if ("register".equals(event)) {
            userSessions.put(sender, session);
            System.out.println(sender + " connect WebSocket.");
        }
        else if ("reject".equals(event)) {
            WebSocketSession webSocketSession = userSessions.get(target);
            if (webSocketSession != null && webSocketSession.isOpen()) {
                ObjectNode response = objectMapper.createObjectNode();
                response.put("event", "callRejected");
                response.put("sender", sender);
                webSocketSession.sendMessage(new TextMessage(response.toString()));
            }
        }
        else {
            System.out.println("Received JSON: " + message.getPayload());
            WebSocketSession webSocketSession = userSessions.get(target);
            if(webSocketSession != null && webSocketSession.isOpen()){
                webSocketSession.sendMessage(new TextMessage(message.getPayload()));
            }
            else {
                System.out.println("khong the gui tin nhan, nguoi nhan khong ton tai hoac da dong ket noi.");
            }
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session){
        System.out.println("connect new: " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) {
        String disconnectedUser = userSessions.entrySet().stream()
                .filter(entry -> entry.getValue().equals(session))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);

        if (disconnectedUser != null) {
            userSessions.remove(disconnectedUser);
            System.out.println(disconnectedUser + " disconnect WebSocket.");
        }
    }
}

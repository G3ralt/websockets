package alex.osenov.websockets.exchange;

import alex.osenov.websockets.model.Message;
import alex.osenov.websockets.model.Order;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class BitfinexOrderHandler implements WebSocketHandler {

    private final Gson gson;

    public BitfinexOrderHandler() {
        this.gson = new Gson();
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        Message subscriptionMessage = new Message("subscribe", "book", "tBTCUSD");
        return session
                .send(Mono.just(session.textMessage(subscriptionMessage.asText())))
                .and(session.receive()
                        .map(WebSocketMessage::getPayloadAsText)
                        .filter(s -> s.contains("["))
                        .map(s -> gson.fromJson(s, JsonArray.class).get(1).getAsJsonArray())
                        .map(jsonElement -> processJson(jsonElement))
                        .doOnNext(OrderBook::updateBook)
                );
    }

    private List<Order> processJson(JsonArray jsonArray) {
        if (jsonArray.size() > 3) {
            List<Order> orders = new ArrayList<>();
            for (JsonElement element : jsonArray) {
                orders.add(orderFromJson(element.getAsJsonArray()));
            }
            return orders;
        } else {
            return List.of(orderFromJson(jsonArray));
        }
    }

    private Order orderFromJson(JsonArray jsonElement) {
        double price = jsonElement.get(0).getAsDouble();
        int count = jsonElement.get(1).getAsInt();
        double amount = jsonElement.get(2).getAsDouble();
        Order.Pair pair = amount > 0 ? Order.Pair.BTC_USD_BID : Order.Pair.BTC_USD_ASK;
        return new Order(pair, count, BigDecimal.valueOf(price).setScale(8), BigDecimal.valueOf(amount).setScale(8).abs());
    }
}

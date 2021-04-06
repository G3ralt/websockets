package alex.osenov.websockets.exchange;

import alex.osenov.websockets.model.Message;
import alex.osenov.websockets.model.Order;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;

public class BitfinexOrderHandler implements WebSocketHandler {

    private final Map<Order.Pair, Map<Double, Order>> orders;
    private final Gson gson;

    public BitfinexOrderHandler(Map<Order.Pair, Map<Double, Order>> orders) {
        this.orders = orders;
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
                        .map(s -> gson.fromJson(s, JsonArray.class))
                        .doOnNext(array -> processJson(array)))
                .then();
    }

    private void processJson(JsonArray jsonElements) {
        JsonElement jsonElement = jsonElements.get(1);
        if (jsonElement.isJsonArray()) {
            JsonArray data = (JsonArray) jsonElement;
            if (data.size() > 3) {
                for (JsonElement element : data) {
                    processOrder((JsonArray) element);
                }
            } else {
                processOrder(data);
            }
        }
    }

    private void processOrder(JsonArray data) {
        double price = data.get(0).getAsDouble();
        int count = data.get(1).getAsInt();
        double amount = data.get(2).getAsDouble();

        if (count > 0) {
            if (amount > 0) {
                addNewOrder(price, amount, Order.Pair.BTC_USD_BID);
            } else {
                addNewOrder(price, amount, Order.Pair.BTC_USD_ASK);
            }
        } else {
            if (amount > 0) {
                orders.get(Order.Pair.BTC_USD_BID).remove(price);
            } else {
                orders.get(Order.Pair.BTC_USD_ASK).remove(price);
            }
        }
        System.out.println(orders);
    }

    private void addNewOrder(double price, double amount, Order.Pair pair) {
        Map<Double, Order> orderMap = this.orders.get(pair);
        Order fromMap = orderMap.get(price);
        BigDecimal newOrderAmount = BigDecimal.valueOf(amount).setScale(8);
        BigDecimal bigDecimalPrice = BigDecimal.valueOf(price).setScale(8);
        if (fromMap == null) {
            orderMap.put(price, new Order(pair, bigDecimalPrice, newOrderAmount));
        } else {
            BigDecimal sum = fromMap.getAmount().add(newOrderAmount).setScale(8);
            orderMap.put(price, new Order(pair, bigDecimalPrice, sum));
        }
    }

}

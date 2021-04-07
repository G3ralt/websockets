package alex.osenov.websockets.exchange;

import alex.osenov.websockets.model.Message;
import alex.osenov.websockets.model.Order;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class BitfinexOrderHandler implements WebSocketHandler {

    private final Gson gson;
    private Map<Order.Pair, Map<Double, Order>> orders;
    private OrderBook orderBook;

    public BitfinexOrderHandler() {
        this.gson = new Gson();
    }

    @Autowired
    public void setOrderBook(OrderBook orderBook) {
        this.orderBook = orderBook;
        this.orders = orderBook.getExchangeList().stream().filter(exchange -> exchange instanceof Bitfinex).findFirst().get().getOrders();
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        Message subscriptionMessage = new Message("subscribe", "book", "tBTCUSD");
        return session
                .send(Mono.just(session.textMessage(subscriptionMessage.asText())))
                .and(session.receive()
                        .map(WebSocketMessage::getPayloadAsText)
                        .filter(s -> s.contains("["))
                        .map(s -> gson.fromJson(s, JsonArray.class).get(1))
                        .filter(JsonElement::isJsonArray)
                        .map(JsonElement::getAsJsonArray)
                        .map(this::processJson)
                        .doOnNext(orderBook::updateBook)
                ).doOnError(Throwable::printStackTrace);
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
        Order order = new Order(pair, count, BigDecimal.valueOf(price).setScale(8), BigDecimal.valueOf(amount).setScale(8).abs());
        processOrder(order);
        return order;
    }

    private void processOrder(Order newOrder) {
        int count = newOrder.getCount();
        double price = newOrder.getPrice().doubleValue();
        Order.Pair pair = newOrder.getPair();
        Map<Double, Order> priceOrderMap = this.orders.get(pair);
        if (count > 0) {
            Order orderFromMap = priceOrderMap.get(price);
            if (orderFromMap == null) {
                priceOrderMap.put(price, newOrder);
            } else {
                BigDecimal amountSum = orderFromMap.getAmount().add(newOrder.getAmount());
                priceOrderMap.put(price, new Order(newOrder.getPair(), count, BigDecimal.valueOf(price), amountSum));
            }
        } else {
            priceOrderMap.remove(newOrder.getPrice());
        }
    }
}

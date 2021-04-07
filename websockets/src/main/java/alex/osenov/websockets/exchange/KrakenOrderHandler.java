package alex.osenov.websockets.exchange;

import alex.osenov.websockets.model.Order;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class KrakenOrderHandler implements WebSocketHandler {

    private final Gson gson;
    private Map<Order.Pair, Map<Double, Order>> orders;
    private OrderBook orderBook;

    public KrakenOrderHandler() {
        gson = new Gson();
    }

    @Autowired
    public void setOrderBook(OrderBook orderBook) {
        this.orderBook = orderBook;
        this.orders = orderBook.getExchangeList().stream().filter(exchange -> exchange instanceof Kraken).findFirst().get().getOrders();
    }

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
        return null;
    }
}

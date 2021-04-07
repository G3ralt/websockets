package alex.osenov.websockets.exchange;

import alex.osenov.websockets.model.Order;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class Exchange {

    private final ExecutorService executorService;
    private final Map<Order.Pair, Map<Double, Order>> orders;

    public Exchange() {
        executorService = Executors.newCachedThreadPool();
        orders = Map.of(
                Order.Pair.BTC_USD_BID, new HashMap<>(),
                Order.Pair.BTC_USD_ASK, new HashMap<>()
        );

    }

    protected void connectOrderBookStream(String endPoint, WebSocketHandler handler) {
        this.executorService.submit(() -> {
            WebSocketClient client = new ReactorNettyWebSocketClient();
            client.execute(URI.create(endPoint), handler)
                    .block();
        });
    }

    public Map<Order.Pair, Map<Double, Order>> getOrders() {
        return orders;
    }
}

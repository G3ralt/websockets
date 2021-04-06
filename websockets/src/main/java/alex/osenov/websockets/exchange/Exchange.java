package alex.osenov.websockets.exchange;

import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;

import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class Exchange {

    private final ExecutorService executorService;

    public Exchange() {
        executorService = Executors.newCachedThreadPool();
    }

    public void connectOrderBookStream(String endPoint, WebSocketHandler handler) {
        this.executorService.submit(() -> {
            WebSocketClient client = new ReactorNettyWebSocketClient();
            client.execute(URI.create(endPoint), handler)
                    .block();
        });
    }

}

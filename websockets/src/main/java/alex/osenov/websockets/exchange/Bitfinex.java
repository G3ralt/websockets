package alex.osenov.websockets.exchange;


import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;

import java.net.URI;

@Component
public class Bitfinex extends Exchange {

    public Bitfinex() {
        super();
        setUpOrdersSocket();
    }

    private void setUpOrdersSocket() {
        WebSocketClient client = new ReactorNettyWebSocketClient();
        client.execute(URI.create("wss://api-pub.bitfinex.com/ws/2"), new BitfinexOrderHandler(this.getOrders()))
                .block();
    }





}

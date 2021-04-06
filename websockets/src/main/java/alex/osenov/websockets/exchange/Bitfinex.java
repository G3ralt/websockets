package alex.osenov.websockets.exchange;


import org.springframework.stereotype.Component;

@Component
public class Bitfinex extends Exchange {

    private final String webSocketEndpoint = "wss://api-pub.bitfinex.com/ws/2";

    public Bitfinex(BitfinexOrderHandler bitfinexOrderHandler) {
        super();
        super.connectOrderBookStream(webSocketEndpoint, bitfinexOrderHandler);
    }
}

package alex.osenov.websockets.exchange;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class Bitfinex extends Exchange {

    private final String webSocketEndpoint = "wss://api-pub.bitfinex.com/ws/2";
    private BitfinexOrderHandler bitfinexOrderHandler;

    @Autowired
    public void setBitfinexOrderHandler(BitfinexOrderHandler bitfinexOrderHandler) {
        this.bitfinexOrderHandler = bitfinexOrderHandler;
    }

    @PostConstruct
    public void init() {
        super.connectOrderBookStream(webSocketEndpoint, bitfinexOrderHandler);
    }
}

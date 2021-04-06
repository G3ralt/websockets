package alex.osenov.websockets.exchange;

import alex.osenov.websockets.model.Order;

import java.util.HashMap;
import java.util.Map;

public abstract class Exchange {

    private final Map<Order.Pair, Map<Double, Order>> orders;

    public Exchange() {
        this.orders = Map.of(Order.Pair.BTC_USD_BID, new HashMap<>(), Order.Pair.BTC_USD_ASK, new HashMap<>());
    }

    public Map<Order.Pair, Map<Double, Order>> getOrders() {
        return orders;
    }


}

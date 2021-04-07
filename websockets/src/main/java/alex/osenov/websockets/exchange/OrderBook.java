package alex.osenov.websockets.exchange;

import alex.osenov.websockets.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

@Service
public class OrderBook {

    private final Map<Order.Pair, Map<Double, BigDecimal>> orderBook;
    private List<Exchange> exchangeList;

    public OrderBook() {
        this.orderBook = Map.of(Order.Pair.BTC_USD_BID, new TreeMap<>(Collections.reverseOrder()), Order.Pair.BTC_USD_ASK, new TreeMap<>(Collections.reverseOrder()));
    }

    public synchronized void updateBook(List<Order> orders) {


        orders.forEach(order -> processOrder(order));

        System.out.println(new Timestamp(System.currentTimeMillis()));
        System.out.println("Best bid: " + ((TreeMap) orderBook.get(Order.Pair.BTC_USD_BID)).firstEntry());
        System.out.println("Best ask: " + ((TreeMap) orderBook.get(Order.Pair.BTC_USD_ASK)).lastEntry());
        System.out.println("Bids:");
        orderBook.get(Order.Pair.BTC_USD_BID).forEach((price, amount) -> System.out.println("[" + price + "," + amount + "]"));
        System.out.println("Asks:");
        orderBook.get(Order.Pair.BTC_USD_ASK).forEach((price, amount) -> System.out.println("[" + price + "," + amount + "]"));
    }

    private void processOrder(Order order) {
        Order.Pair orderPair = order.getPair();
        int orderCount = order.getCount();
        double orderPrice = order.getPrice().doubleValue();

        Map<Double, BigDecimal> bookOrders = orderBook.get(orderPair);
        BigDecimal orderAmountFromBook = bookOrders.get(orderPrice);
        if (orderAmountFromBook == null) {
            bookOrders.put(orderPrice, order.getAmount());
        } else {
            double amountSum = exchangeList.stream()
                    .map(exchange -> exchange.getOrders().get(orderPair).get(orderPrice))
                    .filter(Objects::nonNull)
                    .mapToDouble(orderFromExchange -> orderFromExchange.getAmount().doubleValue()).sum();
            System.out.println("sum" + amountSum);
            if (amountSum == 0) {
                bookOrders.remove(orderPrice);
            } else {
                bookOrders.put(orderPrice, BigDecimal.valueOf(amountSum).setScale(8));
            }

        }

    }

    @Autowired
    public void setExchangeList(List<Exchange> exchangeList) {
        this.exchangeList = exchangeList;
    }

    public List<Exchange> getExchangeList() {
        return exchangeList;
    }
}

package alex.osenov.websockets.model;

import java.math.BigDecimal;

public class Order {

    public enum Pair {
        BTC_USD_BID,
        BTC_USD_ASK
    }

    private final Pair pair;
    private final BigDecimal price;
    private final BigDecimal amount;

    public Order(Pair pair, BigDecimal price, BigDecimal amount) {
        this.pair = pair;
        this.price = price;
        this.amount = amount;
    }

    public Pair getPair() {
        return pair;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "[" + price.doubleValue() + ", " + amount.doubleValue() + "]";
    }
}

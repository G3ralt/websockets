package alex.osenov.websockets.exchange;

import alex.osenov.websockets.model.Order;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderBook {

    public static void updateBook(List<Order> orders) {
        System.out.println(orders);
    }
}

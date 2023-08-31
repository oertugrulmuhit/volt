package com.oemspring.bookz.responses;

import com.oemspring.bookz.models.Order;
import com.oemspring.bookz.models.OrderStatus;
import lombok.Data;

@Data

public class OrderUpdateResponse {

    private Long id;
    private OrderStatus orderStatus = OrderStatus.CREATED;
    private Long productId;
    private Long userId;
    private int quantity;
    private  String message;

    public OrderUpdateResponse(Order order,String message) {
        this.id = order.getId();
        this.orderStatus = order.getOrderStatus();
        this.productId = order.getProduct().getId();
        this.quantity = order.getQuantity();
        this.userId = order.getUser().getId();
        this.message=message;
    }
    //for not found implementation
    public OrderUpdateResponse(String message){
        this.message=message;
    }
}

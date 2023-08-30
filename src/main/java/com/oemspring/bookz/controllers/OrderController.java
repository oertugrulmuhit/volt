package com.oemspring.bookz.controllers;

import com.oemspring.bookz.requests.OrderRequest;
import com.oemspring.bookz.requests.OrderUpdateRequest;
import com.oemspring.bookz.responses.OrderResponse;
import com.oemspring.bookz.services.OrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.quartz.SchedulerException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController

@RequestMapping("/api/orders")
@SecurityRequirement(name = "bookzapi")

public class OrderController {
    private final OrderService orderService;
/*
 https://www.springcloud.io/post/2022-02/spring-security-get-current-user/#gsc.tab=0
 */

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping

    public OrderResponse createOrder(Principal principal, @RequestBody OrderRequest orderRequest) {
        System.out.printf("order create->" + orderRequest + principal.getName());
        return orderService.createOrder(principal, orderRequest);


    }

    @PutMapping("/{orderId}")
    public OrderResponse updateOrder(Principal principal, @PathVariable Long orderId, @RequestBody OrderUpdateRequest orderUpdateRequest) throws SchedulerException {
        System.out.printf("order update with-> id:" + orderId + " " + orderUpdateRequest + " u: " + principal.getName());
        return orderService.updateOrder(principal, orderId, orderUpdateRequest);

    }


    @GetMapping("/myorders")
    public List<OrderResponse> getMyOrders(Principal principal) {
        System.out.print("getMyOrders");
        System.out.printf(principal.getName() + "->orders");

        return orderService.getMyOrders(principal);

        // return  orderService.getMyOrders(principal);
    }


    @GetMapping("/latestdaysdelivered")
    public List<OrderResponse> latestdaysdelivered() {

        return orderService.latestdaysdelivered().stream().map(p -> new OrderResponse(p)).toList();
    }
//cancelOrder

//listMyOrder
}
package com.oemspring.bookz.services;

import com.oemspring.bookz.SpringBookzPro;
import com.oemspring.bookz.jobs.TaskAcceptedToDelivered;
import com.oemspring.bookz.models.Order;
import com.oemspring.bookz.models.OrderStatus;
import com.oemspring.bookz.models.Product;
import com.oemspring.bookz.models.User;
import com.oemspring.bookz.repos.OrderRepository;
import com.oemspring.bookz.requests.OrderRequest;
import com.oemspring.bookz.requests.OrderUpdateRequest;
import com.oemspring.bookz.responses.OrderResponse;
import org.apache.commons.lang3.time.DateUtils;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class OrderService {
    OrderRepository orderRepository;
    ProductService productService;
    UserService userService;

    public OrderService(OrderRepository orderRepository, ProductService productService, UserService userService) {
        this.orderRepository = orderRepository;
        this.productService = productService;
        this.userService = userService;
    }

    public OrderResponse createOrder(Principal principal, OrderRequest orderRequest) {
        Product p = productService.getReferenceById((long) orderRequest.getProductId());

        if (p.getQuantity() >= orderRequest.getQuantity()) {
            Order o = new Order();
            o.setQuantity(orderRequest.getQuantity());
            o.setProduct(p);
            User owner = userService.findByUsername(principal.getName()).get();
            o.setUser(owner);
         /*
            User owner = userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).get();
            o.setUser(owner);*/
            return new OrderResponse(orderRepository.save(o));
        } else return null;

    }


    public String updateOrderAcceptedToDeliveredBase(Long orderId) {

        Order order = orderRepository.getReferenceById(orderId);


        System.out.println("updateOrderAcceptedToDeliveredBase");
        int newQ = order.getProduct().getQuantity() - order.getQuantity();
        System.out.println(order.getProduct().getQuantity() + "  " + newQ);

        System.out.println("must be:" + newQ);
        Product p = productService.getReferenceById(order.getProduct().getId());
        p.setQuantity(newQ);
        productService.saveOneProduct(p);


        order.setOrderStatus(

                OrderStatus.DELIVERED
        );
        order.setTouchedTIME(new Date());
        orderRepository.save(order);
        //  System.out.println("test"+order.toString());
        SpringBookzPro.logger.info("Accepted " + p.getName() + "Delivered olarak işaretlendi");
        return "return";
    }

    public Order getOneByRefId(Long refId) {
        return orderRepository.getReferenceById(refId);
    }

    public OrderResponse updateOrder(Principal principal, Long orderId, OrderUpdateRequest orderUpdateRequest) throws SchedulerException {
        Order order = orderRepository.getReferenceById(orderId);
        System.out.println(OrderStatus.CANCELLED);

        if (orderUpdateRequest.getOrderStatus().equals(OrderStatus.CANCELLED.toString()))
            if (order.getUser().getUsername().equals(principal.getName())
                    && order.getOrderStatus().equals(OrderStatus.CREATED)) {
                SpringBookzPro.logger.info("CANCELLED ->sipariş veren kullanıcı");
                System.out.println("CANCELLED ->sipariş veren kullanıcı");
                order.setOrderStatus(OrderStatus.valueOf(String.valueOf(orderUpdateRequest.getOrderStatus())));
                return new OrderResponse(orderRepository.save(order));
            } else return null;

        if (order.getProduct().getOwner().getUsername().equals(principal.getName())) {

            System.out.println("ACCEPTED-REJECTED--> ürünün sahibi");

            if (order.getOrderStatus().equals(OrderStatus.CREATED) && orderUpdateRequest.getOrderStatus().equals(OrderStatus.ACCEPTED.toString())) {
//talep accepted

                order.setOrderStatus(OrderStatus.valueOf(String.valueOf(orderUpdateRequest.getOrderStatus())));
                String jobName = "accepted->" + order.getId() + "delivered->" + UUID.randomUUID();
                String randomlyAcceptedorDelivered = new Random().nextBoolean() ? "DELIVERED" : "ACCEPTED";
                int randomly2to15 = new Random().nextInt(2, 15);
                SpringBookzPro.logger.info(randomlyAcceptedorDelivered + randomly2to15);
                JobDetail jobAcceptedToDelivered2to15 = JobBuilder.newJob(TaskAcceptedToDelivered.class).withIdentity(jobName + "job2to15", "group1").usingJobData("orderStatus", randomlyAcceptedorDelivered).usingJobData("orderId", order.getId()).build();
                JobDetail jobAcceptedToDelivered15 = JobBuilder.newJob(TaskAcceptedToDelivered.class).withIdentity(jobName + "job15", "group1").usingJobData("orderStatus", "DELIVERED").usingJobData("orderId", order.getId()).build();

//            Trigger trigger = TriggerBuilder.newTrigger().withIdentity(jobName+"trigger", "group1").startNow().withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(40).repeatForever()).build();
                Trigger trigger2to15seconds = TriggerBuilder.newTrigger().withIdentity(jobName + "trigger2to15", "group1").startAt(DateUtils.addSeconds(new Date(), randomly2to15))
                        .build();
                Trigger trigger15seconds = TriggerBuilder.newTrigger().withIdentity(jobName + "trigger15", "group1").startAt(DateUtils.addSeconds(new Date(), 15))
                        .build();
                SpringBookzPro.schedFact.getScheduler().scheduleJob(jobAcceptedToDelivered2to15, trigger2to15seconds);
                SpringBookzPro.schedFact.getScheduler().scheduleJob(jobAcceptedToDelivered15, trigger15seconds);


            }


            if (orderUpdateRequest.getOrderStatus().equals(OrderStatus.REJECTED.toString())) {
                System.out.println("//talep rejected");
                order.setOrderStatus(OrderStatus.valueOf(String.valueOf(orderUpdateRequest.getOrderStatus())));
            }


        }
        return new OrderResponse(orderRepository.save(order));
    }


    public List<OrderResponse> getMyOrders(Principal principal) {

        return
                orderRepository.findByUser(userService.findByUsername(principal.getName()).get()).stream().map(p -> {
                    return new OrderResponse(p);
                }).collect(Collectors.toList());


    }

    public List<Order> latestdaysdelivered() {

        return orderRepository.findAllDeliveredAgedOne().stream().toList();
    }
}

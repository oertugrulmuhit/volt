package com.oemspring.bookz.services;

import com.oemspring.bookz.SpringBookzPro;
import com.oemspring.bookz.exception.ItemOwnerException;
import com.oemspring.bookz.exception.ResourceNotFoundException;
import com.oemspring.bookz.jobs.JobCreatorAcceptedtoDelivered;
import com.oemspring.bookz.models.Order;
import com.oemspring.bookz.models.OrderStatus;
import com.oemspring.bookz.models.Product;
import com.oemspring.bookz.models.User;
import com.oemspring.bookz.repos.OrderRepository;
import com.oemspring.bookz.requests.OrderRequest;
import com.oemspring.bookz.responses.OrderResponse;
import com.oemspring.bookz.responses.OrderUpdateResponse;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Date;
import java.util.List;
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
        try {
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
                return new OrderResponse(orderRepository.save(o), "good");
            } else

                throw new ResourceNotFoundException("Sipariş için yeterli stok yok." + p.getId() + " " + p.getQuantity() + " " + orderRequest.getQuantity());
        } catch (Exception e) {

            throw new ResourceNotFoundException("ID li ürün yok." + orderRequest.getProductId());
        }
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

    public List<OrderResponse> getMyOrders(Principal principal) {

        return
                orderRepository.findByUser(userService.findByUsername(principal.getName()).get()).stream().map(p -> {
                    return new OrderResponse(p, "OK.");
                }).collect(Collectors.toList());


    }

    public List<Order> latestdaysdelivered() {

        return orderRepository.findAllDeliveredAgedOne().stream().toList();
    }

    public OrderUpdateResponse updateOrderAs(Principal principal, Long orderId, String status) throws SchedulerException {

        Order order;
        if (orderRepository.existsById(orderId)) {
            order = orderRepository.getReferenceById(orderId);
        } else {

            throw new ResourceNotFoundException("ID li order yok." + orderId);

        }

        String message = "";
        if (order.getOrderStatus().equals(OrderStatus.CREATED)) {
            if (status.equals("ACCEPTED")) {


                if (order.getProduct().getOwner().getUsername().equals(principal.getName())) {


                    order.setOrderStatus(OrderStatus.valueOf("ACCEPTED"));
                    JobCreatorAcceptedtoDelivered.jobCreator(order);
                    message = "Sipariş edilen ürün satıcısı ACCEPTED etti.";

                } else
                    throw new ItemOwnerException("ACCEPTED isteği sipariş edilen ürün satıcısına ait değil.");


            } else if (status.equals("REJECTED")) {

                if (order.getProduct().getOwner().getUsername().equals(principal.getName())) {


                    order.setOrderStatus(OrderStatus.valueOf("REJECTED"));
                    message = "Sipariş edilen ürün satıcısı REJECTED etti.";

                } else

                    throw new ItemOwnerException("REJECTED isteği sipariş edilen ürün satıcısına ait değil.");

            } else if (status.equals("CANCELLED")) {
                if (order.getUser().getUsername().equals(principal.getName())) {
                    SpringBookzPro.logger.info("CANCELLED ->sipariş veren kullanıcı");
                    order.setOrderStatus(OrderStatus.valueOf("CANCELLED"));
                    message = "Sipariş veren kullanıcı CANCELLED etti.";

                } else
                    throw new ItemOwnerException("CANCELLED isteği sipariş sahibine ait değil.");


            }
            return new OrderUpdateResponse(orderRepository.save(order), message);

        }
        throw new ItemOwnerException("ACCEPTED REJECTED CANCELLED sadece CREATED siparişlere yapılabilir. ");

    }
}
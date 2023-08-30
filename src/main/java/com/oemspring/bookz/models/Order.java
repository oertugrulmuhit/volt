package com.oemspring.bookz.models;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Date;

@Entity
@Table(name = "orders")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus = OrderStatus.CREATED;
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)

    private Product product;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)

    private User user;

    private int quantity;


    //oluşturma zamanı ve ne zaman güncelleme yapılırsa güncellenecek zaman
    @Temporal(TemporalType.TIMESTAMP)
    protected Date touchedTIME = new Date();


}

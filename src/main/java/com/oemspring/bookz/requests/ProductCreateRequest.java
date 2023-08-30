package com.oemspring.bookz.requests;

import lombok.Data;

@Data
public class ProductCreateRequest {

    private Long id;
    private String name;

    private String description;

    private int quantity;
}

package com.oemspring.bookz.responses;

import com.oemspring.bookz.models.Product;
import lombok.Data;

@Data
public class ProductResponse {
    Long id;
    String name;
    String description;
    int quantity;
    Long ownerId;


    public ProductResponse(Product entity) {
        this.id = entity.getId();
        this.ownerId = entity.getOwner().getId();
        this.description = entity.getDescription();
        this.name = entity.getName();
        this.quantity = entity.getQuantity();
    }

}

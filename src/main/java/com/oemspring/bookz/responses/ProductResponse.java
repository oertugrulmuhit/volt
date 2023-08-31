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
    String message;


    public ProductResponse(Product entity,String message) {
        this.id = entity.getId();
        this.ownerId = entity.getOwner().getId();
        this.description = entity.getDescription();
        this.name = entity.getName();
        this.quantity = entity.getQuantity();
        this.message=message;
    }

    //for not found implementation
public ProductResponse(String message){
        this.message=message;
}
}

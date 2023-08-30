package com.oemspring.bookz.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderRequest {
    @NotBlank
    private int quantity;

    @NotBlank
    private int productId;

}

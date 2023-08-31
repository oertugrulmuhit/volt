package com.oemspring.bookz.controllers;

import com.oemspring.bookz.requests.ProductCreateRequest;
import com.oemspring.bookz.responses.ProductResponse;
import com.oemspring.bookz.services.ProductService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;


@RestController

@RequestMapping("/api/products")
@SecurityRequirement(name = "bookzapi")
public class ProductController {

    //@Autowired
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/all")

    public List<ProductResponse> getAllProducts() {
        System.out.print("getAllProducts");

        return productService.findAllProducts();
    }

    @GetMapping("/listbyname/{productName}")

    public List<ProductResponse> getlistbyNameProducts(@PathVariable String productName) {
        System.out.print("getlistbyNameProducts");

        return productService.getlistbyNameProducts(productName);
    }

    @GetMapping("/listbydesc/{productDesc}")

    public List<ProductResponse> getlistbyDescProducts(@PathVariable String productDesc) {
        System.out.print("getlistbyDescProducts");

        return productService.getlistbyDescProducts(productDesc);
    }

    @GetMapping("/listbyquantity/{quantity}")

    public List<ProductResponse> getlistbyQuantityProducts(@PathVariable int quantity) {
        System.out.print("getlistbyQuantityProducts");

        return productService.getlistbyQuantityProducts(quantity);
    }


    @GetMapping("/{productId}")
    public ProductResponse getOneProduct(@PathVariable Long productId) {
        System.out.printf("getOneProduct" + productId);
        //return new Customer();
        try {
            return productService.getOneProduct(productId);
        }
        catch (Exception e) {
            return new ProductResponse("Ürün bulunamadı.");
        }

    }


    @PutMapping("/{productId}")
    // public Product updateOneProduct(@PathVariable Long productId, @RequestBody ProductCreateRequest productCreateRequest){
    public ProductResponse updateOneProduct(Principal principal, @PathVariable Long productId, @RequestBody ProductCreateRequest productCreateRequest) {
        return productService.updateOneProduct(principal, productId, productCreateRequest);
    }

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public ProductResponse createProduct(Principal principal, @RequestBody ProductCreateRequest productCreateRequest) {

        return productService.createProduct(principal, productCreateRequest);

    }

    @DeleteMapping("/{productId}")
    public String deleteProduct(Principal principal, @PathVariable Long productId) {
        return productService.deleteById(principal, productId);
        //return "OK.";
    }
}

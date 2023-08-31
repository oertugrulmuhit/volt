package com.oemspring.bookz.services;

import com.oemspring.bookz.SpringBookzPro;
import com.oemspring.bookz.models.Product;
import com.oemspring.bookz.models.User;
import com.oemspring.bookz.repos.ProductRepository;
import com.oemspring.bookz.requests.ProductCreateRequest;
import com.oemspring.bookz.responses.ProductResponse;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;


@Service
public class ProductService {
    ProductRepository productRepository;
    UserService userService;

    public ProductService(ProductRepository productRepository, UserService userService) {
        this.productRepository = productRepository;
        this.userService = userService;
    }


    public List<ProductResponse> findAllProducts() {

        return productRepository.findAll().stream().map(p ->
                new ProductResponse(p,"OK.")
        ).toList();


    }

    public void saveOneProduct(Product product) {
        productRepository.save(product);

    }

    public ProductResponse getOneProduct(Long productId) {

       return new ProductResponse(productRepository.getReferenceById(productId), "OK.");


   }

    public Product getReferenceById(Long productId) {
        return productRepository.getReferenceById(productId);
    }

    public ProductResponse updateOneProduct(Principal principal, Long productId, ProductCreateRequest productCreateRequest) {
        Product islenen = getReferenceById(productId);
        if (islenen.getOwner().getUsername().equals(principal.getName())) {

            SpringBookzPro.logger.info("Değişiklik talebi ürün sahibine ait:" + islenen.getOwner().getUsername());

            //islenen.getOwner().equals()
            Product p = new Product();
            p.setOwner(islenen.getOwner());
            p.setName(productCreateRequest.getName());
            p.setQuantity(productCreateRequest.getQuantity());
            p.setDescription(productCreateRequest.getDescription());
            return new ProductResponse(productRepository.save(p),"OK.");
        } else return new ProductResponse("Değişiklik talebi ürün sahibine AİT DEĞİL.");

    }


    public String deleteById(Principal principal, Long productId) {

        System.out.println("deleteById" + productId);

        User kullanici = userService.findByUsername(principal.getName()).get();
        System.out.println(kullanici);
        try{
        if (productRepository.getReferenceById(productId).getOwner().equals(kullanici)) {
            productRepository.deleteById(productId);
            return "Silme başarılı";
        } else return "Başka kullacıya ait ürün.";}catch (Exception e){

            return "Ürün bulunamadı";

        }
    }

    public ProductResponse createProduct(Principal principal, ProductCreateRequest productCreateRequest) {
        Product p = new Product();


        User owner = userService.findByUsername(principal.getName()).get();
        p.setOwner(owner);
        //p.setId(productCreateRequest.getId());
        p.setName(productCreateRequest.getName());
        p.setDescription(productCreateRequest.getDescription());
        p.setQuantity(productCreateRequest.getQuantity());
        return new ProductResponse(productRepository.save(p),"OK.");

    }

    public List<ProductResponse> getlistbyDescProducts(String productDesc) {

        return productRepository.findByDescription(productDesc).stream().map(p -> new ProductResponse(p,"OK.")).toList();
    }

    public List<ProductResponse> getlistbyNameProducts(String productName) {
        return productRepository.findByName(productName).stream().map(p -> new ProductResponse(p,"OK.")).toList();

    }

    public List<ProductResponse> getlistbyQuantityProducts(int quantity) {
        return productRepository.findByQuantityGreaterThanEqual(quantity).stream().map(p -> new ProductResponse(p,"Ok.")).toList();
    }
}

package com.oemspring.bookz.repos;


import com.oemspring.bookz.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByName(String name);

    List<Product> findByDescription(String desc);

    List<Product> findByQuantityGreaterThanEqual(int quantity);


}

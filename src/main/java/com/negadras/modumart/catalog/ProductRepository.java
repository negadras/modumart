package com.negadras.modumart.catalog;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {

    List<Product> findByCategory(String category);

    @Query("SELECT * FROM products WHERE stock > :minStock")
    List<Product> findProductsInStock(@Param("minStock") Integer minStock);

    @Query("SELECT * FROM products WHERE name ILIKE %:name%")
    List<Product> findByNameContaining(@Param("name") String name);
}

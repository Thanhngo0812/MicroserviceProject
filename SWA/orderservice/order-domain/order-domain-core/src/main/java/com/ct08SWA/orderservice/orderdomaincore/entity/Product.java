package com.ct08SWA.orderservice.orderdomaincore.entity;

import com.ct08SWA.orderservice.orderdomaincore.valueobject.Money;
import com.ct08SWA.orderservice.orderdomaincore.valueobject.ProductId;

public class Product {
    private final ProductId id;
    private final String name;
    private final Money price;

    public Product(ProductId id, String name, Money price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }



    // Getters
    public ProductId getId() { return id; }
    public String getName() { return name; }
    public Money getPrice() { return price; }
}
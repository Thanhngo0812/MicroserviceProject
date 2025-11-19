package com.ct08SWA.restaurantservice.restaurantdataaccess.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/**
 * JPA Entity: Ánh xạ bảng 'restaurant_products'.
 * (Không Lombok)
 */
@Entity
@Table(name = "restaurant_products", schema = "restaurant")
public class ProductEntity {

    @Id
    private UUID id;

    private String name;
    private BigDecimal price;
    private boolean available;

    // Đây là "chủ" (owner) của mối quan hệ ManyToOne
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id") // Tên cột khóa ngoại
    private RestaurantEntity restaurant;

    // --- Constructors ---
    public ProductEntity() {}

    // --- Getters / Setters ---
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public RestaurantEntity getRestaurant() { return restaurant; }
    public void setRestaurant(RestaurantEntity restaurant) { this.restaurant = restaurant; }

    // --- Equals / HashCode ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductEntity that = (ProductEntity) o;
        return Objects.equals(id, that.id);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
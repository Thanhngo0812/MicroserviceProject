package com.ct08SWA.restaurantservice.restaurantdataaccess.entity;


import jakarta.persistence.*;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * JPA Entity: Ánh xạ bảng 'restaurants'.
 * (Không Lombok)
 */
@Entity
@Table(name = "restaurants", schema = "restaurant")
public class RestaurantEntity {

    @Id
    private UUID id;

    private String name;
    private boolean active;

    // 'mappedBy = "restaurant"' chỉ ra rằng
    // 'ProductEntity.restaurant' là chủ của mối quan hệ.
    // FetchType.LAZY là mặc định cho @OneToMany,
    // nhưng CascadeType.ALL đảm bảo Product được lưu/xóa cùng Restaurant.
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductEntity> products;

    // --- Constructors ---
    public RestaurantEntity() {}

    // --- Getters / Setters (Bắt buộc cho JPA) ---
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public List<ProductEntity> getProducts() { return products; }
    public void setProducts(List<ProductEntity> products) { this.products = products; }

    // --- Equals / HashCode (Dựa trên ID) ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestaurantEntity that = (RestaurantEntity) o;
        return Objects.equals(id, that.id);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
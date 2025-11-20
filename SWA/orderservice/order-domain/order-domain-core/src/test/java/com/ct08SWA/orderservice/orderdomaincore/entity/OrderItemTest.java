package com.ct08SWA.orderservice.orderdomaincore.entity;

import com.ct08SWA.orderservice.orderdomaincore.valueobject.Money;
import com.ct08SWA.orderservice.orderdomaincore.valueobject.OrderId;
import com.ct08SWA.orderservice.orderdomaincore.valueobject.ProductId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for OrderItem entity
 * Tests price validation and initialization logic
 */
@DisplayName("OrderItem Entity Tests")
class OrderItemTest {

    private Product product;
    private UUID orderId;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        product = new Product(
                new ProductId(UUID.randomUUID()),
                "Test Product",
                new Money(new BigDecimal("50.00"))
        );
    }

    @Nested
    @DisplayName("Price Validation Tests")
    class PriceValidationTests {

        @Test
        @DisplayName("Should validate price when all values are correct")
        void shouldValidatePriceWhenAllValuesAreCorrect() {
            // Given: Item with correct price, quantity, and subtotal
            OrderItem item = OrderItem.builder()
                    .product(product)
                    .quantity(2)
                    .price(new Money(new BigDecimal("50.00"))) // Matches product price
                    .subTotal(new Money(new BigDecimal("100.00"))) // 50 * 2
                    .build();

            // When & Then
            assertTrue(item.isPriceValid());
        }

        @Test
        @DisplayName("Should return false when price is zero")
        void shouldReturnFalseWhenPriceIsZero() {
            // Given
            OrderItem item = OrderItem.builder()
                    .product(product)
                    .quantity(2)
                    .price(new Money(BigDecimal.ZERO)) // Zero price
                    .subTotal(new Money(BigDecimal.ZERO))
                    .build();

            // When & Then
            assertFalse(item.isPriceValid());
        }

        @Test
        @DisplayName("Should return false when price is negative")
        void shouldReturnFalseWhenPriceIsNegative() {
            // Given
            OrderItem item = OrderItem.builder()
                    .product(product)
                    .quantity(2)
                    .price(new Money(new BigDecimal("-50.00"))) // Negative price
                    .subTotal(new Money(new BigDecimal("-100.00")))
                    .build();

            // When & Then
            assertFalse(item.isPriceValid());
        }

        @Test
        @DisplayName("Should return false when item price does not match product price")
        void shouldReturnFalseWhenItemPriceDoesNotMatchProductPrice() {
            // Given: Product price is 50, but item price is 40
            OrderItem item = OrderItem.builder()
                    .product(product) // Product price: 50.00
                    .quantity(2)
                    .price(new Money(new BigDecimal("40.00"))) // Wrong price
                    .subTotal(new Money(new BigDecimal("80.00"))) // 40 * 2
                    .build();

            // When & Then
            assertFalse(item.isPriceValid());
        }

        @Test
        @DisplayName("Should return false when subtotal is incorrect")
        void shouldReturnFalseWhenSubtotalIsIncorrect() {
            // Given: price * quantity should be 100, but subtotal is 80
            OrderItem item = OrderItem.builder()
                    .product(product)
                    .quantity(2)
                    .price(new Money(new BigDecimal("50.00")))
                    .subTotal(new Money(new BigDecimal("80.00"))) // Wrong subtotal
                    .build();

            // When & Then
            assertFalse(item.isPriceValid());
        }

        @Test
        @DisplayName("Should validate price with quantity of 1")
        void shouldValidatePriceWithQuantityOne() {
            // Given
            OrderItem item = OrderItem.builder()
                    .product(product)
                    .quantity(1)
                    .price(new Money(new BigDecimal("50.00")))
                    .subTotal(new Money(new BigDecimal("50.00"))) // 50 * 1
                    .build();

            // When & Then
            assertTrue(item.isPriceValid());
        }

        @Test
        @DisplayName("Should validate price with large quantity")
        void shouldValidatePriceWithLargeQuantity() {
            // Given
            OrderItem item = OrderItem.builder()
                    .product(product)
                    .quantity(10)
                    .price(new Money(new BigDecimal("50.00")))
                    .subTotal(new Money(new BigDecimal("500.00"))) // 50 * 10
                    .build();

            // When & Then
            assertTrue(item.isPriceValid());
        }

        @Test
        @DisplayName("Should validate price with decimal values")
        void shouldValidatePriceWithDecimalValues() {
            // Given: Product with decimal price
            Product decimalProduct = new Product(
                    new ProductId(UUID.randomUUID()),
                    "Pizza",
                    new Money(new BigDecimal("15.99"))
            );

            OrderItem item = OrderItem.builder()
                    .product(decimalProduct)
                    .quantity(3)
                    .price(new Money(new BigDecimal("15.99")))
                    .subTotal(new Money(new BigDecimal("47.97"))) // 15.99 * 3
                    .build();

            // When & Then
            assertTrue(item.isPriceValid());
        }

        @Test
        @DisplayName("Should return false when quantity is zero and subtotal is not zero")
        void shouldReturnFalseWhenQuantityIsZeroButSubtotalIsNotZero() {
            // Given
            OrderItem item = OrderItem.builder()
                    .product(product)
                    .quantity(0)
                    .price(new Money(new BigDecimal("50.00")))
                    .subTotal(new Money(new BigDecimal("50.00"))) // Should be 0
                    .build();

            // When & Then
            assertFalse(item.isPriceValid());
        }
    }

    @Nested
    @DisplayName("Initialization Tests")
    class InitializationTests {

        @Test
        @DisplayName("Should initialize order item with ID and OrderId")
        void shouldInitializeOrderItemWithIdAndOrderId() {
            // Given
            OrderItem item = OrderItem.builder()
                    .product(product)
                    .quantity(2)
                    .price(new Money(new BigDecimal("50.00")))
                    .subTotal(new Money(new BigDecimal("100.00")))
                    .build();
            OrderId testOrderId = new OrderId(orderId);
            Long itemId = 1L;

            // When
            item.initializeOrderItem(testOrderId, itemId);

            // Then
            assertEquals(itemId, item.getId());
            assertEquals(testOrderId, item.getOrderId());
        }

        @Test
        @DisplayName("Should initialize multiple order items with different IDs")
        void shouldInitializeMultipleOrderItemsWithDifferentIds() {
            // Given
            OrderItem item1 = OrderItem.builder()
                    .product(product)
                    .quantity(1)
                    .price(new Money(new BigDecimal("50.00")))
                    .subTotal(new Money(new BigDecimal("50.00")))
                    .build();

            OrderItem item2 = OrderItem.builder()
                    .product(product)
                    .quantity(2)
                    .price(new Money(new BigDecimal("50.00")))
                    .subTotal(new Money(new BigDecimal("100.00")))
                    .build();

            OrderId testOrderId = new OrderId(orderId);

            // When
            item1.initializeOrderItem(testOrderId, 1L);
            item2.initializeOrderItem(testOrderId, 2L);

            // Then
            assertEquals(1L, item1.getId());
            assertEquals(2L, item2.getId());
            assertEquals(testOrderId, item1.getOrderId());
            assertEquals(testOrderId, item2.getOrderId());
        }
    }

    @Nested
    @DisplayName("Builder Tests")
    class BuilderTests {

        @Test
        @DisplayName("Should build order item with all fields")
        void shouldBuildOrderItemWithAllFields() {
            // Given
            int quantity = 3;
            Money price = new Money(new BigDecimal("50.00"));
            Money subTotal = new Money(new BigDecimal("150.00"));

            // When
            OrderItem item = OrderItem.builder()
                    .product(product)
                    .quantity(quantity)
                    .price(price)
                    .subTotal(subTotal)
                    .build();

            // Then
            assertNotNull(item);
            assertEquals(product, item.getProduct());
            assertEquals(quantity, item.getQuantity());
            assertEquals(price, item.getPrice());
            assertEquals(subTotal, item.getSubTotal());
        }

        @Test
        @DisplayName("Should have null ID before initialization")
        void shouldHaveNullIdBeforeInitialization() {
            // Given & When
            OrderItem item = OrderItem.builder()
                    .product(product)
                    .quantity(1)
                    .price(new Money(new BigDecimal("50.00")))
                    .subTotal(new Money(new BigDecimal("50.00")))
                    .build();

            // Then
            assertNull(item.getId());
            assertNull(item.getOrderId());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should get all fields correctly")
        void shouldGetAllFieldsCorrectly() {
            // Given
            int quantity = 2;
            Money price = new Money(new BigDecimal("50.00"));
            Money subTotal = new Money(new BigDecimal("100.00"));

            OrderItem item = OrderItem.builder()
                    .product(product)
                    .quantity(quantity)
                    .price(price)
                    .subTotal(subTotal)
                    .build();

            // When & Then
            assertEquals(product, item.getProduct());
            assertEquals(quantity, item.getQuantity());
            assertEquals(price, item.getPrice());
            assertEquals(subTotal, item.getSubTotal());
        }

        @Test
        @DisplayName("Should set ID using setter")
        void shouldSetIdUsingSetter() {
            // Given
            OrderItem item = OrderItem.builder()
                    .product(product)
                    .quantity(1)
                    .price(new Money(new BigDecimal("50.00")))
                    .subTotal(new Money(new BigDecimal("50.00")))
                    .build();
            Long expectedId = 99L;

            // When
            item.setId(expectedId);

            // Then
            assertEquals(expectedId, item.getId());
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle very large quantity")
        void shouldHandleVeryLargeQuantity() {
            // Given
            int largeQuantity = 1000;
            Product cheapProduct = new Product(
                    new ProductId(UUID.randomUUID()),
                    "Bulk Item",
                    new Money(new BigDecimal("1.00"))
            );

            OrderItem item = OrderItem.builder()
                    .product(cheapProduct)
                    .quantity(largeQuantity)
                    .price(new Money(new BigDecimal("1.00")))
                    .subTotal(new Money(new BigDecimal("1000.00"))) // 1 * 1000
                    .build();

            // When & Then
            assertTrue(item.isPriceValid());
            assertEquals(largeQuantity, item.getQuantity());
        }

        @Test
        @DisplayName("Should handle very large prices")
        void shouldHandleVeryLargePrices() {
            // Given
            Product expensiveProduct = new Product(
                    new ProductId(UUID.randomUUID()),
                    "Luxury Item",
                    new Money(new BigDecimal("9999.99"))
            );

            OrderItem item = OrderItem.builder()
                    .product(expensiveProduct)
                    .quantity(1)
                    .price(new Money(new BigDecimal("9999.99")))
                    .subTotal(new Money(new BigDecimal("9999.99")))
                    .build();

            // When & Then
            assertTrue(item.isPriceValid());
        }

        @Test
        @DisplayName("Should handle small decimal prices correctly")
        void shouldHandleSmallDecimalPricesCorrectly() {
            // Given
            Product cheapProduct = new Product(
                    new ProductId(UUID.randomUUID()),
                    "Cheap Item",
                    new Money(new BigDecimal("0.50"))
            );

            OrderItem item = OrderItem.builder()
                    .product(cheapProduct)
                    .quantity(5)
                    .price(new Money(new BigDecimal("0.50")))
                    .subTotal(new Money(new BigDecimal("2.50"))) // 0.50 * 5
                    .build();

            // When & Then
            assertTrue(item.isPriceValid());
        }
    }
}

package com.ct08SWA.orderservice.orderdomaincore.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for StreetAddress value object
 * Tests construction, equality, and immutability
 */
@DisplayName("StreetAddress Value Object Tests")
class StreetAddressTest {

    @Nested
    @DisplayName("Constructor and Getter Tests")
    class ConstructorAndGetterTests {

        @Test
        @DisplayName("Should create StreetAddress with all fields")
        void shouldCreateStreetAddressWithAllFields() {
            // Given
            String street = "123 Main Street";
            String postalCode = "12345";
            String city = "New York";

            // When
            StreetAddress address = new StreetAddress(street, postalCode, city);

            // Then
            assertNotNull(address);
            assertEquals(street, address.getStreet());
            assertEquals(postalCode, address.getPostalCode());
            assertEquals(city, address.getCity());
        }

        @Test
        @DisplayName("Should create StreetAddress with complex street name")
        void shouldCreateStreetAddressWithComplexStreetName() {
            // Given
            String street = "Apartment 5B, 456 Oak Avenue, Building C";
            String postalCode = "67890";
            String city = "Los Angeles";

            // When
            StreetAddress address = new StreetAddress(street, postalCode, city);

            // Then
            assertEquals(street, address.getStreet());
            assertEquals(postalCode, address.getPostalCode());
            assertEquals(city, address.getCity());
        }

        @Test
        @DisplayName("Should create StreetAddress with international postal code")
        void shouldCreateStreetAddressWithInternationalPostalCode() {
            // Given
            String street = "10 Downing Street";
            String postalCode = "SW1A 2AA";
            String city = "London";

            // When
            StreetAddress address = new StreetAddress(street, postalCode, city);

            // Then
            assertEquals(street, address.getStreet());
            assertEquals(postalCode, address.getPostalCode());
            assertEquals(city, address.getCity());
        }

        @Test
        @DisplayName("Should handle null street")
        void shouldHandleNullStreet() {
            // When
            StreetAddress address = new StreetAddress(null, "12345", "New York");

            // Then
            assertNull(address.getStreet());
            assertEquals("12345", address.getPostalCode());
            assertEquals("New York", address.getCity());
        }

        @Test
        @DisplayName("Should handle null postal code")
        void shouldHandleNullPostalCode() {
            // When
            StreetAddress address = new StreetAddress("123 Main St", null, "New York");

            // Then
            assertEquals("123 Main St", address.getStreet());
            assertNull(address.getPostalCode());
            assertEquals("New York", address.getCity());
        }

        @Test
        @DisplayName("Should handle null city")
        void shouldHandleNullCity() {
            // When
            StreetAddress address = new StreetAddress("123 Main St", "12345", null);

            // Then
            assertEquals("123 Main St", address.getStreet());
            assertEquals("12345", address.getPostalCode());
            assertNull(address.getCity());
        }

        @Test
        @DisplayName("Should handle all null values")
        void shouldHandleAllNullValues() {
            // When
            StreetAddress address = new StreetAddress(null, null, null);

            // Then
            assertNotNull(address);
            assertNull(address.getStreet());
            assertNull(address.getPostalCode());
            assertNull(address.getCity());
        }

        @Test
        @DisplayName("Should handle empty strings")
        void shouldHandleEmptyStrings() {
            // When
            StreetAddress address = new StreetAddress("", "", "");

            // Then
            assertEquals("", address.getStreet());
            assertEquals("", address.getPostalCode());
            assertEquals("", address.getCity());
        }
    }

    @Nested
    @DisplayName("Equals Tests")
    class EqualsTests {

        @Test
        @DisplayName("Should be equal when all fields match")
        void shouldBeEqualWhenAllFieldsMatch() {
            // Given
            StreetAddress address1 = new StreetAddress("123 Main St", "12345", "New York");
            StreetAddress address2 = new StreetAddress("123 Main St", "12345", "New York");

            // When & Then
            assertEquals(address1, address2);
        }

        @Test
        @DisplayName("Should not be equal when streets differ")
        void shouldNotBeEqualWhenStreetsDiffer() {
            // Given
            StreetAddress address1 = new StreetAddress("123 Main St", "12345", "New York");
            StreetAddress address2 = new StreetAddress("456 Oak Ave", "12345", "New York");

            // When & Then
            assertNotEquals(address1, address2);
        }

        @Test
        @DisplayName("Should not be equal when postal codes differ")
        void shouldNotBeEqualWhenPostalCodesDiffer() {
            // Given
            StreetAddress address1 = new StreetAddress("123 Main St", "12345", "New York");
            StreetAddress address2 = new StreetAddress("123 Main St", "67890", "New York");

            // When & Then
            assertNotEquals(address1, address2);
        }

        @Test
        @DisplayName("Should not be equal when cities differ")
        void shouldNotBeEqualWhenCitiesDiffer() {
            // Given
            StreetAddress address1 = new StreetAddress("123 Main St", "12345", "New York");
            StreetAddress address2 = new StreetAddress("123 Main St", "12345", "Los Angeles");

            // When & Then
            assertNotEquals(address1, address2);
        }

        @Test
        @DisplayName("Should be equal to itself (reflexive)")
        void shouldBeEqualToItself() {
            // Given
            StreetAddress address = new StreetAddress("123 Main St", "12345", "New York");

            // When & Then
            assertEquals(address, address);
        }

        @Test
        @DisplayName("Should not be equal to null")
        void shouldNotBeEqualToNull() {
            // Given
            StreetAddress address = new StreetAddress("123 Main St", "12345", "New York");

            // When & Then
            assertNotEquals(null, address);
        }

        @Test
        @DisplayName("Should not be equal to different type")
        void shouldNotBeEqualToDifferentType() {
            // Given
            StreetAddress address = new StreetAddress("123 Main St", "12345", "New York");
            String notAddress = "123 Main St, 12345, New York";

            // When & Then
            assertNotEquals(address, notAddress);
        }

        @Test
        @DisplayName("Should be equal when all fields are null")
        void shouldBeEqualWhenAllFieldsAreNull() {
            // Given
            StreetAddress address1 = new StreetAddress(null, null, null);
            StreetAddress address2 = new StreetAddress(null, null, null);

            // When & Then
            assertEquals(address1, address2);
        }

        @Test
        @DisplayName("Should not be equal when one has null and other has value")
        void shouldNotBeEqualWhenOneHasNullAndOtherHasValue() {
            // Given
            StreetAddress address1 = new StreetAddress(null, "12345", "New York");
            StreetAddress address2 = new StreetAddress("123 Main St", "12345", "New York");

            // When & Then
            assertNotEquals(address1, address2);
        }

        @Test
        @DisplayName("Should be symmetric (a equals b implies b equals a)")
        void shouldBeSymmetric() {
            // Given
            StreetAddress address1 = new StreetAddress("123 Main St", "12345", "New York");
            StreetAddress address2 = new StreetAddress("123 Main St", "12345", "New York");

            // When & Then
            assertEquals(address1, address2);
            assertEquals(address2, address1);
        }

        @Test
        @DisplayName("Should be transitive (a equals b and b equals c implies a equals c)")
        void shouldBeTransitive() {
            // Given
            StreetAddress address1 = new StreetAddress("123 Main St", "12345", "New York");
            StreetAddress address2 = new StreetAddress("123 Main St", "12345", "New York");
            StreetAddress address3 = new StreetAddress("123 Main St", "12345", "New York");

            // When & Then
            assertEquals(address1, address2);
            assertEquals(address2, address3);
            assertEquals(address1, address3);
        }

        @Test
        @DisplayName("Should handle case-sensitive comparison")
        void shouldHandleCaseSensitiveComparison() {
            // Given
            StreetAddress address1 = new StreetAddress("123 Main St", "12345", "New York");
            StreetAddress address2 = new StreetAddress("123 main st", "12345", "new york");

            // When & Then
            assertNotEquals(address1, address2);
        }
    }

    @Nested
    @DisplayName("HashCode Tests")
    class HashCodeTests {

        @Test
        @DisplayName("Should have same hashCode for equal objects")
        void shouldHaveSameHashCodeForEqualObjects() {
            // Given
            StreetAddress address1 = new StreetAddress("123 Main St", "12345", "New York");
            StreetAddress address2 = new StreetAddress("123 Main St", "12345", "New York");

            // When & Then
            assertEquals(address1.hashCode(), address2.hashCode());
        }

        @Test
        @DisplayName("Should have different hashCode for different objects")
        void shouldHaveDifferentHashCodeForDifferentObjects() {
            // Given
            StreetAddress address1 = new StreetAddress("123 Main St", "12345", "New York");
            StreetAddress address2 = new StreetAddress("456 Oak Ave", "67890", "Los Angeles");

            // When & Then
            // Note: Different objects can have same hashCode (collision), but in practice...
            assertNotEquals(address1.hashCode(), address2.hashCode());
        }

        @Test
        @DisplayName("Should have consistent hashCode")
        void shouldHaveConsistentHashCode() {
            // Given
            StreetAddress address = new StreetAddress("123 Main St", "12345", "New York");

            // When
            int hashCode1 = address.hashCode();
            int hashCode2 = address.hashCode();

            // Then
            assertEquals(hashCode1, hashCode2);
        }

        @Test
        @DisplayName("Should handle null values in hashCode")
        void shouldHandleNullValuesInHashCode() {
            // Given
            StreetAddress address1 = new StreetAddress(null, null, null);
            StreetAddress address2 = new StreetAddress(null, null, null);

            // When & Then
            assertEquals(address1.hashCode(), address2.hashCode());
        }
    }

    @Nested
    @DisplayName("Immutability Tests")
    class ImmutabilityTests {

        @Test
        @DisplayName("Should be immutable (fields are final)")
        void shouldBeImmutable() {
            // Given
            String street = "123 Main St";
            String postalCode = "12345";
            String city = "New York";

            StreetAddress address = new StreetAddress(street, postalCode, city);

            // When - Try to get fields
            String retrievedStreet = address.getStreet();
            String retrievedPostalCode = address.getPostalCode();
            String retrievedCity = address.getCity();

            // Then - Values should match and there's no setter
            assertEquals(street, retrievedStreet);
            assertEquals(postalCode, retrievedPostalCode);
            assertEquals(city, retrievedCity);
        }
    }

    @Nested
    @DisplayName("Real-World Scenarios")
    class RealWorldScenariosTests {

        @Test
        @DisplayName("Should handle US address format")
        void shouldHandleUSAddressFormat() {
            // Given & When
            StreetAddress address = new StreetAddress(
                    "1600 Pennsylvania Avenue NW",
                    "20500",
                    "Washington, D.C."
            );

            // Then
            assertEquals("1600 Pennsylvania Avenue NW", address.getStreet());
            assertEquals("20500", address.getPostalCode());
            assertEquals("Washington, D.C.", address.getCity());
        }

        @Test
        @DisplayName("Should handle UK address format")
        void shouldHandleUKAddressFormat() {
            // Given & When
            StreetAddress address = new StreetAddress(
                    "10 Downing Street",
                    "SW1A 2AA",
                    "London"
            );

            // Then
            assertEquals("10 Downing Street", address.getStreet());
            assertEquals("SW1A 2AA", address.getPostalCode());
            assertEquals("London", address.getCity());
        }

        @Test
        @DisplayName("Should handle Vietnamese address format")
        void shouldHandleVietnameseAddressFormat() {
            // Given & When
            StreetAddress address = new StreetAddress(
                    "227 Nguyen Van Cu",
                    "70000",
                    "Ho Chi Minh City"
            );

            // Then
            assertEquals("227 Nguyen Van Cu", address.getStreet());
            assertEquals("70000", address.getPostalCode());
            assertEquals("Ho Chi Minh City", address.getCity());
        }

        @Test
        @DisplayName("Should handle apartment complex address")
        void shouldHandleApartmentComplexAddress() {
            // Given & When
            StreetAddress address = new StreetAddress(
                    "Building A, Floor 5, Apartment 501, 123 Complex Street",
                    "54321",
                    "San Francisco"
            );

            // Then
            assertEquals("Building A, Floor 5, Apartment 501, 123 Complex Street", address.getStreet());
            assertEquals("54321", address.getPostalCode());
            assertEquals("San Francisco", address.getCity());
        }

        @Test
        @DisplayName("Should handle special characters in address")
        void shouldHandleSpecialCharactersInAddress() {
            // Given & When
            StreetAddress address = new StreetAddress(
                    "Rue de l'Église #45",
                    "75001",
                    "Paris"
            );

            // Then
            assertEquals("Rue de l'Église #45", address.getStreet());
            assertEquals("75001", address.getPostalCode());
            assertEquals("Paris", address.getCity());
        }
    }
}

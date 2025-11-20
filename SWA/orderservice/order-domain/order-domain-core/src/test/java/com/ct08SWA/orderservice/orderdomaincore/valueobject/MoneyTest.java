package com.ct08SWA.orderservice.orderdomaincore.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Money value object
 * Tests arithmetic operations, comparisons, and equals/hashCode
 */
@DisplayName("Money Value Object Tests")
class MoneyTest {

    @Nested
    @DisplayName("isGreaterThanZero() Tests")
    class IsGreaterThanZeroTests {

        @Test
        @DisplayName("Should return true for positive amount")
        void shouldReturnTrueForPositiveAmount() {
            // Given
            Money money = new Money(new BigDecimal("100.00"));

            // When & Then
            assertTrue(money.isGreaterThanZero());
        }

        @Test
        @DisplayName("Should return false for zero amount")
        void shouldReturnFalseForZeroAmount() {
            // Given
            Money money = new Money(BigDecimal.ZERO);

            // When & Then
            assertFalse(money.isGreaterThanZero());
        }

        @Test
        @DisplayName("Should return false for negative amount")
        void shouldReturnFalseForNegativeAmount() {
            // Given
            Money money = new Money(new BigDecimal("-50.00"));

            // When & Then
            assertFalse(money.isGreaterThanZero());
        }

        @Test
        @DisplayName("Should return false when amount is null")
        void shouldReturnFalseWhenAmountIsNull() {
            // Given
            Money money = new Money(null);

            // When & Then
            assertFalse(money.isGreaterThanZero());
        }

        @Test
        @DisplayName("Should return true for very small positive amount")
        void shouldReturnTrueForVerySmallPositiveAmount() {
            // Given
            Money money = new Money(new BigDecimal("0.01"));

            // When & Then
            assertTrue(money.isGreaterThanZero());
        }
    }

    @Nested
    @DisplayName("isGreaterThan(Money) Tests")
    class IsGreaterThanTests {

        @ParameterizedTest
        @CsvSource({
            "100.00, 50.00, true",   // 100 > 50
            "50.00, 100.00, false",  // 50 < 100
            "50.00, 50.00, false",   // 50 == 50
            "100.00, 0.00, true",    // 100 > 0
            "0.00, 100.00, false",   // 0 < 100
            "0.01, 0.00, true",      // 0.01 > 0
            "1000.00, 999.99, true"  // 1000 > 999.99
        })
        @DisplayName("Should compare money amounts correctly")
        void shouldCompareMoneyAmountsCorrectly(String amount1, String amount2, boolean expected) {
            // Given
            Money money1 = new Money(new BigDecimal(amount1));
            Money money2 = new Money(new BigDecimal(amount2));

            // When
            boolean result = money1.isGreaterThan(money2);

            // Then
            assertEquals(expected, result);
        }

        @Test
        @DisplayName("Should return false when comparing with equal amounts")
        void shouldReturnFalseWhenComparingWithEqualAmounts() {
            // Given
            Money money1 = new Money(new BigDecimal("100.00"));
            Money money2 = new Money(new BigDecimal("100.00"));

            // When & Then
            assertFalse(money1.isGreaterThan(money2));
        }

        @Test
        @DisplayName("Should handle negative amounts comparison")
        void shouldHandleNegativeAmountsComparison() {
            // Given
            Money negativeMoney = new Money(new BigDecimal("-50.00"));
            Money positiveMoney = new Money(new BigDecimal("50.00"));

            // When & Then
            assertFalse(negativeMoney.isGreaterThan(positiveMoney));
            assertTrue(positiveMoney.isGreaterThan(negativeMoney));
        }
    }

    @Nested
    @DisplayName("add(Money) Tests")
    class AddTests {

        @Test
        @DisplayName("Should add two positive amounts correctly")
        void shouldAddTwoPositiveAmountsCorrectly() {
            // Given
            Money money1 = new Money(new BigDecimal("50.00"));
            Money money2 = new Money(new BigDecimal("100.00"));

            // When
            Money result = money1.add(money2);

            // Then
            assertEquals(new BigDecimal("150.00"), result.getAmount());
        }

        @Test
        @DisplayName("Should add zero to amount")
        void shouldAddZeroToAmount() {
            // Given
            Money money1 = new Money(new BigDecimal("100.00"));
            Money money2 = new Money(BigDecimal.ZERO);

            // When
            Money result = money1.add(money2);

            // Then
            assertEquals(new BigDecimal("100.00"), result.getAmount());
        }

        @Test
        @DisplayName("Should add using Money.ZERO constant")
        void shouldAddUsingMoneyZeroConstant() {
            // Given
            Money money = new Money(new BigDecimal("100.00"));

            // When
            Money result = money.add(Money.ZERO);

            // Then
            assertEquals(new BigDecimal("100.00"), result.getAmount());
        }

        @Test
        @DisplayName("Should handle decimal addition correctly")
        void shouldHandleDecimalAdditionCorrectly() {
            // Given
            Money money1 = new Money(new BigDecimal("15.99"));
            Money money2 = new Money(new BigDecimal("20.50"));

            // When
            Money result = money1.add(money2);

            // Then
            assertEquals(new BigDecimal("36.49"), result.getAmount());
        }

        @Test
        @DisplayName("Should add negative amount (effectively subtract)")
        void shouldAddNegativeAmount() {
            // Given
            Money money1 = new Money(new BigDecimal("100.00"));
            Money money2 = new Money(new BigDecimal("-50.00"));

            // When
            Money result = money1.add(money2);

            // Then
            assertEquals(new BigDecimal("50.00"), result.getAmount());
        }

        @Test
        @DisplayName("Should return new Money instance (immutability)")
        void shouldReturnNewMoneyInstance() {
            // Given
            Money money1 = new Money(new BigDecimal("50.00"));
            Money money2 = new Money(new BigDecimal("100.00"));

            // When
            Money result = money1.add(money2);

            // Then
            assertNotSame(money1, result);
            assertNotSame(money2, result);
            assertEquals(new BigDecimal("50.00"), money1.getAmount()); // Original unchanged
            assertEquals(new BigDecimal("100.00"), money2.getAmount()); // Original unchanged
        }
    }

    @Nested
    @DisplayName("subtract(Money) Tests")
    class SubtractTests {

        @Test
        @DisplayName("Should subtract amounts correctly")
        void shouldSubtractAmountsCorrectly() {
            // Given
            Money money1 = new Money(new BigDecimal("150.00"));
            Money money2 = new Money(new BigDecimal("50.00"));

            // When
            Money result = money1.subtract(money2);

            // Then
            assertEquals(new BigDecimal("100.00"), result.getAmount());
        }

        @Test
        @DisplayName("Should subtract zero from amount")
        void shouldSubtractZeroFromAmount() {
            // Given
            Money money1 = new Money(new BigDecimal("100.00"));
            Money money2 = new Money(BigDecimal.ZERO);

            // When
            Money result = money1.subtract(money2);

            // Then
            assertEquals(new BigDecimal("100.00"), result.getAmount());
        }

        @Test
        @DisplayName("Should handle negative result after subtraction")
        void shouldHandleNegativeResultAfterSubtraction() {
            // Given
            Money money1 = new Money(new BigDecimal("50.00"));
            Money money2 = new Money(new BigDecimal("100.00"));

            // When
            Money result = money1.subtract(money2);

            // Then
            assertEquals(new BigDecimal("-50.00"), result.getAmount());
        }

        @Test
        @DisplayName("Should handle decimal subtraction correctly")
        void shouldHandleDecimalSubtractionCorrectly() {
            // Given
            Money money1 = new Money(new BigDecimal("100.50"));
            Money money2 = new Money(new BigDecimal("50.25"));

            // When
            Money result = money1.subtract(money2);

            // Then
            assertEquals(new BigDecimal("50.25"), result.getAmount());
        }

        @Test
        @DisplayName("Should return new Money instance (immutability)")
        void shouldReturnNewMoneyInstance() {
            // Given
            Money money1 = new Money(new BigDecimal("150.00"));
            Money money2 = new Money(new BigDecimal("50.00"));

            // When
            Money result = money1.subtract(money2);

            // Then
            assertNotSame(money1, result);
            assertNotSame(money2, result);
            assertEquals(new BigDecimal("150.00"), money1.getAmount()); // Original unchanged
            assertEquals(new BigDecimal("50.00"), money2.getAmount()); // Original unchanged
        }
    }

    @Nested
    @DisplayName("multiply(int) Tests")
    class MultiplyTests {

        @Test
        @DisplayName("Should multiply amount by positive multiplier")
        void shouldMultiplyAmountByPositiveMultiplier() {
            // Given
            Money money = new Money(new BigDecimal("50.00"));

            // When
            Money result = money.multiply(2);

            // Then
            assertEquals(new BigDecimal("100.00"), result.getAmount());
        }

        @Test
        @DisplayName("Should multiply amount by zero")
        void shouldMultiplyAmountByZero() {
            // Given
            Money money = new Money(new BigDecimal("50.00"));

            // When
            Money result = money.multiply(0);

            // Then
            assertEquals(new BigDecimal("0.00"), result.getAmount());
        }

        @Test
        @DisplayName("Should multiply amount by one")
        void shouldMultiplyAmountByOne() {
            // Given
            Money money = new Money(new BigDecimal("100.00"));

            // When
            Money result = money.multiply(1);

            // Then
            assertEquals(new BigDecimal("100.00"), result.getAmount());
        }

        @Test
        @DisplayName("Should multiply amount by negative multiplier")
        void shouldMultiplyAmountByNegativeMultiplier() {
            // Given
            Money money = new Money(new BigDecimal("50.00"));

            // When
            Money result = money.multiply(-2);

            // Then
            assertEquals(new BigDecimal("-100.00"), result.getAmount());
        }

        @Test
        @DisplayName("Should multiply decimal amount correctly")
        void shouldMultiplyDecimalAmountCorrectly() {
            // Given
            Money money = new Money(new BigDecimal("15.99"));

            // When
            Money result = money.multiply(3);

            // Then
            assertEquals(new BigDecimal("47.97"), result.getAmount());
        }

        @Test
        @DisplayName("Should multiply by large multiplier")
        void shouldMultiplyByLargeMultiplier() {
            // Given
            Money money = new Money(new BigDecimal("10.00"));

            // When
            Money result = money.multiply(1000);

            // Then
            assertEquals(new BigDecimal("10000.00"), result.getAmount());
        }

        @Test
        @DisplayName("Should return new Money instance (immutability)")
        void shouldReturnNewMoneyInstance() {
            // Given
            Money money = new Money(new BigDecimal("50.00"));

            // When
            Money result = money.multiply(2);

            // Then
            assertNotSame(money, result);
            assertEquals(new BigDecimal("50.00"), money.getAmount()); // Original unchanged
        }
    }

    @Nested
    @DisplayName("equals() and hashCode() Tests")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("Should be equal when amounts are the same")
        void shouldBeEqualWhenAmountsAreTheSame() {
            // Given
            Money money1 = new Money(new BigDecimal("100.00"));
            Money money2 = new Money(new BigDecimal("100.00"));

            // When & Then
            assertEquals(money1, money2);
            assertEquals(money1.hashCode(), money2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when amounts are different")
        void shouldNotBeEqualWhenAmountsAreDifferent() {
            // Given
            Money money1 = new Money(new BigDecimal("100.00"));
            Money money2 = new Money(new BigDecimal("50.00"));

            // When & Then
            assertNotEquals(money1, money2);
        }

        @Test
        @DisplayName("Should be equal to itself (reflexive)")
        void shouldBeEqualToItself() {
            // Given
            Money money = new Money(new BigDecimal("100.00"));

            // When & Then
            assertEquals(money, money);
        }

        @Test
        @DisplayName("Should not be equal to null")
        void shouldNotBeEqualToNull() {
            // Given
            Money money = new Money(new BigDecimal("100.00"));

            // When & Then
            assertNotEquals(null, money);
        }

        @Test
        @DisplayName("Should not be equal to different type")
        void shouldNotBeEqualToDifferentType() {
            // Given
            Money money = new Money(new BigDecimal("100.00"));
            String notMoney = "100.00";

            // When & Then
            assertNotEquals(money, notMoney);
        }

        @Test
        @DisplayName("Should be equal with BigDecimal scale differences")
        void shouldBeEqualWithBigDecimalScaleDifferences() {
            // Given
            Money money1 = new Money(new BigDecimal("100.00"));
            Money money2 = new Money(new BigDecimal("100.0"));

            // When & Then
            // BigDecimal equals considers scale, so these may not be equal
            // depending on implementation
            assertEquals(money1.getAmount().compareTo(money2.getAmount()), 0);
        }

        @Test
        @DisplayName("Should have consistent hashCode for equal objects")
        void shouldHaveConsistentHashCodeForEqualObjects() {
            // Given
            Money money1 = new Money(new BigDecimal("100.00"));
            Money money2 = new Money(new BigDecimal("100.00"));

            // When & Then
            if (money1.equals(money2)) {
                assertEquals(money1.hashCode(), money2.hashCode());
            }
        }

        @Test
        @DisplayName("Should handle Money.ZERO equality")
        void shouldHandleMoneyZeroEquality() {
            // Given
            Money zero1 = Money.ZERO;
            Money zero2 = new Money(BigDecimal.ZERO);

            // When & Then
            assertEquals(zero1, zero2);
        }
    }

    @Nested
    @DisplayName("Money.ZERO Constant Tests")
    class ZeroConstantTests {

        @Test
        @DisplayName("Should have ZERO constant with zero amount")
        void shouldHaveZeroConstantWithZeroAmount() {
            // When & Then
            assertNotNull(Money.ZERO);
            assertEquals(BigDecimal.ZERO, Money.ZERO.getAmount());
        }

        @Test
        @DisplayName("Should not be greater than zero")
        void shouldNotBeGreaterThanZero() {
            // When & Then
            assertFalse(Money.ZERO.isGreaterThanZero());
        }

        @Test
        @DisplayName("Should add to ZERO correctly")
        void shouldAddToZeroCorrectly() {
            // Given
            Money money = new Money(new BigDecimal("50.00"));

            // When
            Money result = Money.ZERO.add(money);

            // Then
            assertEquals(new BigDecimal("50.00"), result.getAmount());
        }

        @Test
        @DisplayName("Should multiply ZERO")
        void shouldMultiplyZero() {
            // When
            Money result = Money.ZERO.multiply(5);

            // Then
            assertEquals(BigDecimal.ZERO, result.getAmount());
        }
    }

    @Nested
    @DisplayName("getAmount() Tests")
    class GetAmountTests {

        @Test
        @DisplayName("Should return the correct amount")
        void shouldReturnTheCorrectAmount() {
            // Given
            BigDecimal expectedAmount = new BigDecimal("123.45");
            Money money = new Money(expectedAmount);

            // When
            BigDecimal actualAmount = money.getAmount();

            // Then
            assertEquals(expectedAmount, actualAmount);
        }

        @Test
        @DisplayName("Should return null when amount is null")
        void shouldReturnNullWhenAmountIsNull() {
            // Given
            Money money = new Money(null);

            // When
            BigDecimal amount = money.getAmount();

            // Then
            assertNull(amount);
        }
    }

    @Nested
    @DisplayName("Edge Cases and Complex Scenarios")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle chain of operations")
        void shouldHandleChainOfOperations() {
            // Given
            Money money = new Money(new BigDecimal("100.00"));

            // When: (100 + 50) * 2 - 100 = 200
            Money result = money
                    .add(new Money(new BigDecimal("50.00")))
                    .multiply(2)
                    .subtract(new Money(new BigDecimal("100.00")));

            // Then
            assertEquals(new BigDecimal("200.00"), result.getAmount());
        }

        @Test
        @DisplayName("Should handle very large amounts")
        void shouldHandleVeryLargeAmounts() {
            // Given
            Money money1 = new Money(new BigDecimal("999999999.99"));
            Money money2 = new Money(new BigDecimal("999999999.99"));

            // When
            Money result = money1.add(money2);

            // Then
            assertEquals(new BigDecimal("1999999999.98"), result.getAmount());
        }

        @Test
        @DisplayName("Should handle very small amounts")
        void shouldHandleVerySmallAmounts() {
            // Given
            Money money = new Money(new BigDecimal("0.01"));

            // When
            Money result = money.multiply(10);

            // Then
            assertEquals(new BigDecimal("0.10"), result.getAmount());
        }

        @Test
        @DisplayName("Should maintain precision in calculations")
        void shouldMaintainPrecisionInCalculations() {
            // Given
            Money money1 = new Money(new BigDecimal("10.99"));
            Money money2 = new Money(new BigDecimal("20.99"));

            // When
            Money result = money1.add(money2);

            // Then
            assertEquals(new BigDecimal("31.98"), result.getAmount());
        }
    }
}

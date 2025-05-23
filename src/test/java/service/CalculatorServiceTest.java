package service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CalculatorServiceTest {

    @Test
    void testCalculate_withValidInput() {
        double amount = 10000;
        double rate = 12;
        int months = 12;

        CalculatorService.CalculationResult result = CalculatorService.calculate(amount, rate, months);

        assertEquals(1200, result.getProfit(), 0.0001);
        assertEquals(11200, result.getTotal(), 0.0001);
    }

    @Test
    void testCalculate_zeroValues() {
        CalculatorService.CalculationResult result = CalculatorService.calculate(0, 0, 0);

        assertEquals(0, result.getProfit(), 0.0001);
        assertEquals(0, result.getTotal(), 0.0001);
    }

    @Test
    void testCalculate_partialZeroInterest() {
        CalculatorService.CalculationResult result = CalculatorService.calculate(10000, 0, 12);

        assertEquals(0, result.getProfit(), 0.0001);
        assertEquals(10000, result.getTotal(), 0.0001);
    }

    @Test
    void testCalculate_negativeAmount_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            CalculatorService.calculate(-1000, 10, 12);
        });
    }

    @Test
    void testCalculate_negativeRate_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            CalculatorService.calculate(1000, -5, 12);
        });
    }

    @Test
    void testCalculate_negativeMonths_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            CalculatorService.calculate(1000, 5, -3);
        });
    }
}

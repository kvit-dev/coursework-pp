package service;

public class CalculatorService {

    public static CalculationResult calculate(double amount, double ratePercent, int months) {
        if (amount < 0 || ratePercent < 0 || months < 0) {
            throw new IllegalArgumentException("Усі значення повинні бути додатні");
        }

        double rate = ratePercent / 100;
        double profit = amount * rate * months / 12;
        double total = amount + profit;
        return new CalculationResult(profit, total);
    }

    public static class CalculationResult {
        private final double profit;
        private final double total;

        public CalculationResult(double profit, double total) {
            this.profit = profit;
            this.total = total;
        }

        public double getProfit() {
            return profit;
        }

        public double getTotal() {
            return total;
        }
    }
}

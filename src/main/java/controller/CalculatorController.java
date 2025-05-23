package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.CalculatorService;

public class CalculatorController {
    @FXML private TextField amountField;
    @FXML private TextField rateField;
    @FXML private TextField monthsField;
    @FXML private Label resultLabel;

    private static final Logger logger = LoggerFactory.getLogger(CalculatorController.class);

    @FXML
    public void onCalculate() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            double rate = Double.parseDouble(rateField.getText());
            int months = Integer.parseInt(monthsField.getText());

            if (amount <= 0 || rate <= 0 || months <= 0) {
                throw new IllegalArgumentException("Значення повинні бути більше 0");
            }

            CalculatorService.CalculationResult result = CalculatorService.calculate(amount, rate, months);

            resultLabel.setText("Прибуток: " + Math.round(result.getProfit()) +
                    " ₴, Разом: " + Math.round(result.getTotal()) + " ₴");

            logger.info("Розрахунок успішний: сума={}, ставка={}, місяці={}", amount, rate, months);

        } catch (NumberFormatException e) {
            resultLabel.setText("Помилка введення");
            logger.warn("Невірний формат числа: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            resultLabel.setText("Усі значення мають бути додатні!");
            logger.warn("Невірне значення: {}", e.getMessage());
        } catch (Exception e) {
            resultLabel.setText("Помилка введення");
            logger.error("Помилка при розрахунку", e);
        }
    }
}

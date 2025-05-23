package service;

import javafx.scene.control.Alert;
import model.Deposit;
import model.DepositDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class AddDepositService {
    private static final Logger logger = LoggerFactory.getLogger(AddDepositService.class);

    public boolean addDeposit(String bankName, String depositName, String interestRateText, String termText,
                              boolean earlyWithdrawal, boolean replenishment) throws SQLException {
        try {
            if (bankName.isEmpty() || depositName.isEmpty() || interestRateText.isEmpty() || termText.isEmpty()) {
                logger.warn("Не всі поля заповнені");
                showAlert(Alert.AlertType.WARNING, "Будь ласка, заповніть усі поля");
                return false;
            }

            double interestRate = Double.parseDouble(interestRateText);
            int term = Integer.parseInt(termText);

            if (interestRate <= 0 || term <= 0) {
                logger.warn("Ставка або термін мають неприпустиму значення: {}, {}", interestRate, term);
                showAlert(Alert.AlertType.WARNING, "Ставка та термін мають бути додатніми");
                return false;
            }

            Deposit deposit = new Deposit(0,
                    bankName,
                    depositName,
                    interestRate,
                    term,
                    earlyWithdrawal,
                    replenishment);

            new DepositDAO().addDeposit(deposit);
            logger.info("Вклад додано: {} у {}", depositName, bankName);
            showAlert(Alert.AlertType.INFORMATION, "Вклад успішно додано");

            return true;

        } catch (NumberFormatException e) {
            logger.warn("Поганий формат числа при додаванні вкладу: {}", e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Ставка або термін мають бути числами");
            return false;
        } catch (Exception e) {
            logger.error("Помилка при додаванні вкладу: {}", e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Помилка при додаванні: " + e.getMessage());
            return false;
        }
    }

    void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message);
        alert.showAndWait();
    }
}

package service;

import javafx.scene.control.Alert;
import javafx.stage.Stage;
import model.Deposit;
import model.DepositDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangeDepositInfoService {
    private static final Logger logger = LoggerFactory.getLogger(ChangeDepositInfoService.class);
    private DepositDAO depositDAO;

    public ChangeDepositInfoService(DepositDAO depositDAO) {
        this.depositDAO = depositDAO;
    }

    public void changeDeposit(
            int depositId,
            String interestRateText,
            String termText,
            boolean earlyWithdrawal,
            boolean replenishment,
            Runnable onSuccess,
            Stage currentStage
    ) {
        try {
            double interestRate = Double.parseDouble(interestRateText);
            int term = Integer.parseInt(termText);

            logger.info("Спроба оновлення депозиту з id = {}", depositId);
            Deposit depositUpdate = depositDAO.getDepositById(depositId);

            if (depositUpdate == null) {
                String msg = "Депозит з id " + depositId + " не знайдено";
                logger.error(msg);
                throw new Exception(msg);
            }

            depositUpdate.setInterestRate(interestRate);
            depositUpdate.setTermMonths(term);
            depositUpdate.setEarlyWithdrawal(earlyWithdrawal);
            depositUpdate.setReplenishment(replenishment);

            depositDAO.updateDeposit(depositUpdate);
            logger.info("Депозит оновлено: id = {}", depositId);

            showAlert(Alert.AlertType.INFORMATION, "Інформацію про депозит успішно оновлено");

            if (onSuccess != null) {
                onSuccess.run();
                logger.info("Список депозитів оновлено після редагування");
            }

            if (currentStage != null) {
                currentStage.close();
            }

        } catch (Exception e) {
            logger.error("Помилка при оновленні депозиту", e);
            showAlert(Alert.AlertType.ERROR, "Помилка при оновленні депозиту: " + e.getMessage());
        }
    }

    protected void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(type == Alert.AlertType.ERROR ? "Помилка" : "Успіх");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

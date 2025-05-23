package service;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import model.DepositDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Optional;

public class RemoveDepositService {
    private static final Logger logger = LoggerFactory.getLogger(RemoveDepositService.class);

    public boolean deleteDepositWithConfirmation(
            int depositId,
            Runnable onSuccess,
            Stage currentStage
    ) {
        try {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Підтвердження видалення");
            confirmAlert.setHeaderText(null);
            confirmAlert.setContentText("Ви дійсно хочете видалити депозит з ID = " + depositId + "?");

            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                DepositDAO dao = new DepositDAO();

                dao.deleteDeposit(depositId);
                logger.info("Депозит з id = {} успішно видалено", depositId);

                if (onSuccess != null) {
                    onSuccess.run();
                }

                if (currentStage != null) {
                    currentStage.close();
                }
                return true;
            } else {
                logger.info("Користувач скасував видалення депозиту з id = {}", depositId);
                return false;
            }

        } catch (SQLException e) {
            logger.error("Помилка SQL при видаленні депозиту", e);
            showError("Сталася помилка при видаленні: " + e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("Помилка при видаленні депозиту", e);
            showError("Сталася помилка при видаленні: " + e.getMessage());
            return false;
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Помилка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
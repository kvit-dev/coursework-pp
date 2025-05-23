package service;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import model.Deposit;
import model.DepositDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import controller.ChangeDepositInfo;
import controller.MainController;

import java.util.Optional;

public class DepositCardService {

    private static final Logger logger = LoggerFactory.getLogger(DepositCardService.class);

    public void editDeposit(Deposit deposit, MainController mainController) {
        try {
            logger.info("Редагування депозиту id = {}", deposit.getId());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/change-info.fxml"));
            Scene scene = new Scene(loader.load(), 400, 400);

            ChangeDepositInfo controller = loader.getController();
            controller.setMainController(mainController);
            controller.setDeposit(deposit);

            Stage stage = new Stage();
            stage.setTitle("Редагувати вклад");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            logger.error("Помилка відкриття редагування для депозиту id: {}", deposit.getId(), e);
            showAlert("Помилка при відкритті редагування", e.getMessage());
        }
    }

    public void deleteDeposit(Deposit deposit, MainController mainController, Runnable onSuccess) {
        try {
            logger.info("Спроба видалити депозит з id = {}", deposit.getId());

            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Підтвердження");
            confirmation.setHeaderText("Видалити депозит?");
            confirmation.setContentText("Ви впевнені, що хочете видалити депозит \"" + deposit.getDepositName() + "\"?");

            Optional<ButtonType> result = confirmation.showAndWait();
            if (result.isEmpty() || result.get() != ButtonType.OK) {
                logger.info("Користувач скасував видалення депозиту з id = {}", deposit.getId());
                return;
            }

            DepositDAO depositDAO = new DepositDAO();
            depositDAO.deleteDeposit(deposit.getId());
            logger.info("Депозит з id = {} видалено", deposit.getId());

            if (onSuccess != null) {
                onSuccess.run();
            }

            if (mainController != null) {
                mainController.refreshDeposits();
            }

        } catch (Exception e) {
            logger.error("Помилка при видаленні депозиту id: {}", deposit.getId(), e);
            showAlert("Помилка при видаленні", e.getMessage());
        }
    }

    void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
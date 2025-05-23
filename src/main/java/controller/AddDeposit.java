package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import service.AddDepositService;

import java.sql.SQLException;

public class AddDeposit {
    @FXML
    TextField bankNameField;
    @FXML
    TextField depositNameField;
    @FXML
    TextField interestRateField;
    @FXML
    TextField termField;
    @FXML
    CheckBox earlyWithdrawalBox;
    @FXML
    CheckBox replenishmentBox;

    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void onAddDeposit() throws SQLException {
        AddDepositService service = new AddDepositService();
        boolean success = service.addDeposit(
                bankNameField.getText().trim(),
                depositNameField.getText().trim(),
                interestRateField.getText().trim(),
                termField.getText().trim(),
                earlyWithdrawalBox.isSelected(),
                replenishmentBox.isSelected()
        );

        if (success) {
            if (mainController != null) {
                mainController.refreshDeposits();
            }
            Stage stage = (Stage) bankNameField.getScene().getWindow();
            stage.close();
        }
    }
}

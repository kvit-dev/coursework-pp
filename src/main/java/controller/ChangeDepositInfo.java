package controller;

import javafx.fxml.FXML;
import model.Deposit;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.DepositDAO;
import service.ChangeDepositInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class ChangeDepositInfo {
    @FXML
    TextField interestRateField;
    @FXML
    TextField termField;
    @FXML
    CheckBox earlyWithdrawalBox;
    @FXML
    CheckBox replenishmentBox;

    private MainController mainController;
    private int depositId;

    private static final Logger logger = LoggerFactory.getLogger(ChangeDepositInfo.class);
    private final ChangeDepositInfoService changeDepositService =
            new ChangeDepositInfoService(new DepositDAO());

    public ChangeDepositInfo() throws SQLException {
    }


    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void onChangeDeposit() {
        Stage stage = (Stage) interestRateField.getScene().getWindow();
        changeDepositService.changeDeposit(
                depositId,
                interestRateField.getText(),
                termField.getText(),
                earlyWithdrawalBox.isSelected(),
                replenishmentBox.isSelected(),
                () -> {
                    if (mainController != null) {
                        mainController.refreshDeposits();
                    }
                },
                stage
        );
    }

    public void setDeposit(Deposit deposit) {
        this.depositId = deposit.getId();
        interestRateField.setText(String.valueOf(deposit.getInterestRate()));
        termField.setText(String.valueOf(deposit.getTermMonths()));
        earlyWithdrawalBox.setSelected(deposit.isEarlyWithdrawal());
        replenishmentBox.setSelected(deposit.isReplenishment());
        logger.info("Відкрито вікно редагування для депозиту з id = {}", depositId);
    }
}

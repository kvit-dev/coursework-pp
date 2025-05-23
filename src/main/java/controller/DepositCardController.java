package controller;

import javafx.fxml.FXML;
import javafx.scene.text.Text;
import model.Deposit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.DepositCardService;

public class DepositCardController {

    @FXML
    Text bankName;
    @FXML
    Text depositName;
    @FXML
    Text interestRate;
    @FXML
    Text termMonths;
    @FXML
    Text earlyWithdrawal;
    @FXML
    Text replenishment;

    private MainController mainController;
    private Deposit deposit;
    private final DepositCardService service = new DepositCardService();
    private static final Logger logger = LoggerFactory.getLogger(DepositCardController.class);

    public void setDeposit(Deposit deposit) {
        this.deposit = deposit;
        bankName.setText(deposit.getBankName());
        depositName.setText(deposit.getDepositName());
        interestRate.setText("Ставка: " + deposit.getInterestRate() + "%");
        termMonths.setText("Термін: " + deposit.getTermMonths() + " міс.");
        earlyWithdrawal.setText("Достр. зняття: " + (deposit.isEarlyWithdrawal() ? "так" : "ні"));
        replenishment.setText("Поповнення: " + (deposit.isReplenishment() ? "так" : "ні"));
    }

    @FXML
    void onEdit() {
        service.editDeposit(deposit, mainController);
    }

    @FXML
    void onDelete() {
        service.deleteDeposit(deposit, mainController, () -> {
            if (mainController != null) {
                mainController.refreshDeposits();
            }
        });
    }

    public void setMainController(MainController controller) {
        this.mainController = controller;
    }
}
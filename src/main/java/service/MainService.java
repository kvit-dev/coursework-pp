package service;

import controller.MainController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import model.Deposit;
import model.DepositDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MainService {

    private static final Logger logger = LoggerFactory.getLogger(MainService.class);
    private final DepositDAO depositDAO = new DepositDAO();

    public MainService() throws SQLException {
    }

    public List<Deposit> loadAllDeposits() throws Exception {
        logger.info("Завантаження всіх депозитів");
        return depositDAO.getAllDeposits();
    }

    public List<Deposit> searchDeposits(String bankName) throws Exception {
        logger.info("Пошук депозитів за банком: {}", bankName);
        if (bankName == null || bankName.isEmpty()) {
            return depositDAO.getAllDeposits();
        }
        return depositDAO.searchByBankName(bankName);
    }

    public List<Deposit> sortDepositsByTerm(List<Deposit> deposits) {
        logger.info("Сортування депозитів за терміном");
        return deposits.stream()
                .sorted(Comparator.comparingInt(Deposit::getTermMonths))
                .collect(Collectors.toList());
    }

    public Parent createDepositCard(Deposit deposit, MainController mainController) throws Exception {
        logger.debug("Створення картки депозиту для: {}", deposit);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/deposit-card.fxml"));
        Parent card = loader.load();
        card.getStyleClass().add("card");
        controller.DepositCardController cardController = loader.getController();
        cardController.setDeposit(deposit);
        cardController.setMainController(mainController);
        return card;
    }
}

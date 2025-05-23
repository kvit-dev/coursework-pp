package service;

import controller.DepositCardController;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import model.Deposit;
import model.DepositDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class CardsService {

    private static final Logger logger = LoggerFactory.getLogger(CardsService.class);

    public List<Pane> createDepositCards() {
        List<Pane> cards = new ArrayList<>();
        try {
            DepositDAO dao = new DepositDAO();
            List<Deposit> deposits = dao.getAllDeposits();

            for (Deposit d : deposits) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/deposit-card.fxml"));
                Pane card = loader.load();

                DepositCardController controller = loader.getController();
                controller.setDeposit(d);

                cards.add(card);
            }
        } catch (Exception e) {
            logger.error("Помилка при створенні карток депозитів", e);
        }
        return cards;
    }
}

package controller;

import javafx.fxml.FXML;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import service.CardsService;

import java.util.List;

public class CardsController {

    @FXML
    FlowPane cardsRoot;

    private final CardsService cardsService = new CardsService();

    @FXML
    public void initialize() {
        List<Pane> cards = cardsService.createDepositCards();
        cardsRoot.getChildren().setAll(cards);
    }
}

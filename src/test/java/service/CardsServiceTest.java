package service;

import javafx.scene.layout.Pane;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CardsServiceTest {

    private CardsService cardsService;

    @BeforeEach
    void setUp() {
        cardsService = new CardsService();
    }

    @Test
    void testCreateDepositCards_returnsCardsList() {
        List<Pane> cards = cardsService.createDepositCards();

        assertNotNull(cards, "Список карток не повинен бути null");
        assertFalse(cards.isEmpty(), "Список карток повинен містити хоча б один елемент");

        for (Pane card : cards) {
            assertNotNull(card);
            assertFalse(card.getChildren().isEmpty(), "Картка має містити елементи");
        }
    }

}

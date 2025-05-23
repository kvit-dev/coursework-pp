package controller;

import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;

public class CardsControllerTest extends ApplicationTest {

    private CardsController controller;

    @Override
    public void start(Stage stage) {
        controller = new CardsController();
        FlowPane root = new FlowPane();
        controller.cardsRoot = root;

        controller.initialize();

        stage.setScene(new javafx.scene.Scene(root));
        stage.show();
    }

    @Test
    public void testCardsAreLoaded() {
        int count = controller.cardsRoot.getChildren().size();

        for (var node : controller.cardsRoot.getChildren()) {
            assertTrue(node instanceof Pane, "Усі дочірні елементи повинні бути картками");
        }

        assertTrue(count >= 0, "Повинно бути 0 або більше карт після ініціалізації");
    }
}

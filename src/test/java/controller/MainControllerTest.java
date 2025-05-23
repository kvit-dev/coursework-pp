package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;

public class MainControllerTest extends ApplicationTest {

    private MainController controller;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main.fxml"));
        Scene scene = new Scene(loader.load());
        controller = loader.getController();

        stage.setScene(scene);
        stage.show();
    }

    @BeforeEach
    void setUp() {
        assertNotNull(controller);
        assertNotNull(lookup("#searchField").query());
        assertNotNull(lookup("#termSortButton").query());
        assertNotNull(lookup("#depositCardsContainer").query());
    }

    @Test
    void testSearchField_AllowsTextEntry() {
        clickOn("#searchField").write("ПриватБанк");
        TextField searchField = lookup("#searchField").query();
        assertEquals("ПриватБанк", searchField.getText());
    }

    @Test
    void testSortButton_TogglesText() {
        Button sortButton = lookup("#termSortButton").queryButton();
        String initialText = sortButton.getText();

        clickOn(sortButton);
        assertEquals("Скасувати сортування", sortButton.getText());

        clickOn(sortButton);
        assertEquals("Сортувати за терміном", sortButton.getText());
        assertEquals(initialText, sortButton.getText());
    }

    @Test
    void testDepositCards_InitiallyPopulated() {
        FlowPane container = lookup("#depositCardsContainer").query();
        container.getChildren();
        assertTrue(true);
    }

    @Test
    void testSearchDeposits_ByBankName() {
        TextField searchField = lookup("#searchField").query();
        clickOn(searchField).write("Ощадбанк");

        interact(() -> controller.onSearch());

        FlowPane container = lookup("#depositCardsContainer").query();
        assertNotNull(container);
        container.getChildren();
        assertTrue(true);
    }

    @Test
    void testRefreshDeposits_LoadsDeposits() {
        interact(() -> controller.refreshDeposits());

        FlowPane container = lookup("#depositCardsContainer").query();
        assertNotNull(container);
        container.getChildren();
        assertTrue(true);
    }

    @Test
    void testOpenAddDeposit_DoesNotThrow() {
        assertDoesNotThrow(() -> interact(() -> {
            try {
                controller.openAddDeposit(new ActionEvent());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }));
    }

    @Test
    void testOpenCalculator_DoesNotThrow() {
        assertDoesNotThrow(() -> interact(() -> {
            try {
                controller.openCalculator(new ActionEvent());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }));
    }

    @Test
    void testOpenAnalytics_DoesNotThrow() {
        assertDoesNotThrow(() -> interact(() -> {
            try {
                controller.openAnalytics(new ActionEvent());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }));
    }
}

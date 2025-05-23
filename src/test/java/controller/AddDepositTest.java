package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.NodeQueryUtils.hasText;

public class AddDepositTest extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/add-deposit.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void testFieldsAndAddButton() {
        clickOn("#bankNameField").write("ПриватБанк");
        clickOn("#depositNameField").write("Вклад 2");
        clickOn("#interestRateField").write("10.5");
        clickOn("#termField").write("12");
        clickOn("#earlyWithdrawalBox").clickOn();
        clickOn("#replenishmentBox").clickOn();
        clickOn("Додати");

    }

    @Test
    public void testEmptyFieldsShouldNotProceed() {
        clickOn("Додати");
        verifyThat("#interestRateField", hasText(""));
    }

    @Test
    public void testInvalidInterestRate() {
        clickOn("#bankNameField").write("Альфа");
        clickOn("#depositNameField").write("Новий");
        clickOn("#interestRateField").write("abc");
        clickOn("#termField").write("12");
        clickOn("Додати");
    }
}

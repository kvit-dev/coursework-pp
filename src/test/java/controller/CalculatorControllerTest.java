package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(ApplicationExtension.class)
public class CalculatorControllerTest {

    private TextField amountField;
    private TextField rateField;
    private TextField monthsField;
    private Label resultLabel;

    @Start
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/calculator.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @BeforeEach
    public void setup(FxRobot robot) {
        amountField = robot.lookup("#amountField").queryAs(TextField.class);
        rateField = robot.lookup("#rateField").queryAs(TextField.class);
        monthsField = robot.lookup("#monthsField").queryAs(TextField.class);
        resultLabel = robot.lookup("#resultLabel").queryAs(Label.class);

        robot.interact(() -> {
            amountField.clear();
            rateField.clear();
            monthsField.clear();
            resultLabel.setText("");
        });
    }

    @Test
    public void testValidCalculation(FxRobot robot) {
        robot.clickOn("#amountField").write("10000");
        robot.clickOn("#rateField").write("12");
        robot.clickOn("#monthsField").write("6");
        robot.clickOn("Розрахувати");

        assertEquals("Прибуток: 600 ₴, Разом: 10600 ₴", resultLabel.getText());
    }

    @Test
    public void testNegativeInput_ShowsValidation(FxRobot robot) {
        robot.clickOn("#amountField").write("-5000");
        robot.clickOn("#rateField").write("10");
        robot.clickOn("#monthsField").write("3");
        robot.clickOn("Розрахувати");

        assertEquals("Усі значення мають бути додатні!", resultLabel.getText());
    }

    @Test
    public void testInvalidTextInput_ShowsError(FxRobot robot) {
        robot.clickOn("#amountField").write("abc");
        robot.clickOn("#rateField").write("5");
        robot.clickOn("#monthsField").write("12");
        robot.clickOn("Розрахувати");

        assertEquals("Помилка введення", resultLabel.getText());
    }

    @Test
    public void testZeroValues(FxRobot robot) {
        robot.clickOn("#amountField").write("0");
        robot.clickOn("#rateField").write("5");
        robot.clickOn("#monthsField").write("6");
        robot.clickOn("Розрахувати");

        assertEquals("Усі значення мають бути додатні!", resultLabel.getText());
    }
}

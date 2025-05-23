package controller;

import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Deposit;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import service.ChangeDepositInfoService;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ChangeDepositInfoTest extends ApplicationTest {

    private ChangeDepositInfo controller;

    @Override
    public void start(Stage stage) throws SQLException {
        controller = new ChangeDepositInfo();

        controller.interestRateField = new TextField();
        controller.termField = new TextField();
        controller.earlyWithdrawalBox = new CheckBox();
        controller.replenishmentBox = new CheckBox();

        VBox root = new VBox(
                controller.interestRateField,
                controller.termField,
                controller.earlyWithdrawalBox,
                controller.replenishmentBox
        );
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Test
    public void testSetDeposit_PopulatesFieldsCorrectly() {
        Deposit deposit = new Deposit();
        deposit.setId(1);
        deposit.setInterestRate(5.5);
        deposit.setTermMonths(12);
        deposit.setEarlyWithdrawal(true);
        deposit.setReplenishment(false);

        controller.setDeposit(deposit);

        assertEquals("5.5", controller.interestRateField.getText());
        assertEquals("12", controller.termField.getText());
        assertTrue(controller.earlyWithdrawalBox.isSelected());
        assertFalse(controller.replenishmentBox.isSelected());
    }

    @Test
    public void testOnChangeDeposit_CallsServiceAndRefreshesMainController() {
        Deposit deposit = new Deposit();
        deposit.setId(1);
        controller.setDeposit(deposit);

        controller.interestRateField.setText("4.5");
        controller.termField.setText("18");
        controller.earlyWithdrawalBox.setSelected(true);
        controller.replenishmentBox.setSelected(false);

        ChangeDepositInfoService mockService = mock(ChangeDepositInfoService.class);
        MainController mockMainController = mock(MainController.class);

        try {
            var field = ChangeDepositInfo.class.getDeclaredField("changeDepositService");
            field.setAccessible(true);
            field.set(controller, mockService);
        } catch (Exception e) {
            fail("Не вдалося підмінити сервіс");
        }

        controller.setMainController(mockMainController);
        Stage stage = (Stage) controller.interestRateField.getScene().getWindow();
        controller.onChangeDeposit();

        verify(mockService).changeDeposit(
                eq(1),
                eq("4.5"),
                eq("18"),
                eq(true),
                eq(false),
                any(Runnable.class),
                eq(stage)
        );
    }
}

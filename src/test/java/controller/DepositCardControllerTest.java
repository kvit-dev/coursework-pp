package controller;

import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import model.Deposit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import service.DepositCardService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DepositCardControllerTest extends ApplicationTest {

    private DepositCardController controller;

    @Override
    public void start(Stage stage) {
        controller = new DepositCardController();

        controller.bankName = new Text();
        controller.depositName = new Text();
        controller.interestRate = new Text();
        controller.termMonths = new Text();
        controller.earlyWithdrawal = new Text();
        controller.replenishment = new Text();

        VBox root = new VBox(
                controller.bankName,
                controller.depositName,
                controller.interestRate,
                controller.termMonths,
                controller.earlyWithdrawal,
                controller.replenishment
        );

        stage.setScene(new Scene(root));
        stage.show();
    }

    @BeforeEach
    public void resetController() throws Exception {
        DepositCardService mockService = mock(DepositCardService.class);
        var field = DepositCardController.class.getDeclaredField("service");
        field.setAccessible(true);
        field.set(controller, mockService);
    }

    @Test
    public void testSetDeposit_PopulatesFieldsCorrectly() {
        Deposit deposit = new Deposit();
        deposit.setBankName("ПриватБанк");
        deposit.setDepositName("Строковий");
        deposit.setInterestRate(7.2);
        deposit.setTermMonths(24);
        deposit.setEarlyWithdrawal(true);
        deposit.setReplenishment(false);

        controller.setDeposit(deposit);

        assertEquals("ПриватБанк", controller.bankName.getText());
        assertEquals("Строковий", controller.depositName.getText());
        assertEquals("Ставка: 7.2%", controller.interestRate.getText());
        assertEquals("Термін: 24 міс.", controller.termMonths.getText());
        assertEquals("Достр. зняття: так", controller.earlyWithdrawal.getText());
        assertEquals("Поповнення: ні", controller.replenishment.getText());
    }

    @Test
    public void testSetDeposit_AlternateBooleans() {
        Deposit deposit = new Deposit();
        deposit.setBankName("Monobank");
        deposit.setDepositName("Гнучкий");
        deposit.setInterestRate(5.0);
        deposit.setTermMonths(6);
        deposit.setEarlyWithdrawal(false);
        deposit.setReplenishment(true);

        controller.setDeposit(deposit);

        assertEquals("Достр. зняття: ні", controller.earlyWithdrawal.getText());
        assertEquals("Поповнення: так", controller.replenishment.getText());
    }

    @Test
    public void testSetMainController_SetsCorrectly() throws Exception {
        MainController mockMain = mock(MainController.class);
        controller.setMainController(mockMain);

        var field = DepositCardController.class.getDeclaredField("mainController");
        field.setAccessible(true);
        Object actual = field.get(controller);
        assertSame(mockMain, actual);
    }

    @Test
    public void testOnEdit_CallsService() throws Exception {
        Deposit deposit = new Deposit();
        controller.setDeposit(deposit);

        MainController mockMain = mock(MainController.class);
        controller.setMainController(mockMain);

        DepositCardService mockService = mock(DepositCardService.class);
        var field = DepositCardController.class.getDeclaredField("service");
        field.setAccessible(true);
        field.set(controller, mockService);
        controller.onEdit();

        verify(mockService).editDeposit(deposit, mockMain);
    }

    @Test
    public void testOnDelete_WithMainController() throws Exception {
        Deposit deposit = new Deposit();
        controller.setDeposit(deposit);

        MainController mockMain = mock(MainController.class);
        controller.setMainController(mockMain);

        DepositCardService mockService = mock(DepositCardService.class);
        var field = DepositCardController.class.getDeclaredField("service");
        field.setAccessible(true);
        field.set(controller, mockService);

        controller.onDelete();

        verify(mockService).deleteDeposit(eq(deposit), eq(mockMain), any());
    }

    @Test
    public void testOnDelete_WithoutMainController() throws Exception {
        Deposit deposit = new Deposit();
        controller.setDeposit(deposit);
        controller.setMainController(null);

        DepositCardService mockService = mock(DepositCardService.class);
        var field = DepositCardController.class.getDeclaredField("service");
        field.setAccessible(true);
        field.set(controller, mockService);

        controller.onDelete();

        verify(mockService).deleteDeposit(eq(deposit), isNull(), any());
    }
}

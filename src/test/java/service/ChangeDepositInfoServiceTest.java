package service;

import javafx.stage.Stage;
import model.Deposit;
import model.DepositDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.mockito.Mockito.*;

class ChangeDepositInfoServiceTest {

    private DepositDAO mockDAO;
    private ChangeDepositInfoService service;

    @BeforeEach
    void setup() {
        mockDAO = mock(DepositDAO.class);
        service = new ChangeDepositInfoService(mockDAO) {
            @Override
            protected void showAlert(javafx.scene.control.Alert.AlertType type, String message) {
            }
        };
    }

    @Test
    void testChangeDeposit_SuccessfulUpdate() throws SQLException {
        Deposit deposit = new Deposit();
        when(mockDAO.getDepositById(1)).thenReturn(deposit);

        Runnable onSuccess = mock(Runnable.class);
        Stage stage = mock(Stage.class);

        service.changeDeposit(1, "5.5", "12", true, false, onSuccess, stage);

        verify(mockDAO).updateDeposit(deposit);
        verify(onSuccess).run();
        verify(stage).close();
    }

    @Test
    void testChangeDeposit_DepositNotFound() throws SQLException {
        when(mockDAO.getDepositById(99)).thenReturn(null);

        service.changeDeposit(99, "3.5", "6", false, true, null, null);

        verify(mockDAO, never()).updateDeposit(any());
    }

    @Test
    void testChangeDeposit_InvalidInterestRate() throws SQLException {
        service.changeDeposit(1, "abc", "12", false, false, null, null);

        verify(mockDAO, never()).getDepositById(anyInt());
    }

    @Test
    void testChangeDeposit_InvalidTerm() throws SQLException {
        service.changeDeposit(1, "5.0", "xyz", false, false, null, null);

        verify(mockDAO, never()).getDepositById(anyInt());
    }
}

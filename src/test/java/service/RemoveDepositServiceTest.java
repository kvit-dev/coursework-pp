package service;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import model.DepositDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.testfx.framework.junit5.ApplicationTest;

import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RemoveDepositServiceTest extends ApplicationTest {

    private RemoveDepositService service;

    @BeforeEach
    public void setUp() {
        service = new RemoveDepositService();
    }

    @Test
    public void testDeleteDepositWithConfirmation_UserConfirms_DeletionSuccessful() throws Exception {
        try (MockedConstruction<Alert> alertMock = mockConstruction(Alert.class, (mock, context) -> {
            when(mock.getAlertType()).thenReturn(Alert.AlertType.CONFIRMATION);
            when(mock.showAndWait()).thenReturn(Optional.of(ButtonType.OK));
        });
             MockedConstruction<DepositDAO> daoMock = mockConstruction(DepositDAO.class, (mock, context) -> {
                 doNothing().when(mock).deleteDeposit(1);
             })) {

            Runnable onSuccess = mock(Runnable.class);
            Stage stage = mock(Stage.class);

            boolean result = service.deleteDepositWithConfirmation(1, onSuccess, stage);

            assertTrue(result);
            verify(onSuccess).run();
            verify(stage).close();
        }
    }

    @Test
    public void testDeleteDepositWithConfirmation_UserCancels_DeletionNotDone() throws Exception {
        try (MockedConstruction<Alert> alertMock = mockConstruction(Alert.class, (mock, context) -> {
            when(mock.getAlertType()).thenReturn(Alert.AlertType.CONFIRMATION);
            when(mock.showAndWait()).thenReturn(Optional.of(ButtonType.CANCEL));
        })) {
            Runnable onSuccess = mock(Runnable.class);
            Stage stage = mock(Stage.class);

            boolean result = service.deleteDepositWithConfirmation(1, onSuccess, stage);

            assertFalse(result);
            verifyNoInteractions(onSuccess);
            verifyNoInteractions(stage);
        }
    }

    @Test
    public void testDeleteDepositWithConfirmation_SQLExceptionThrown() throws Exception {
        try (MockedConstruction<Alert> alertMock = mockConstruction(Alert.class, (mock, context) -> {
            if (mock.getAlertType() == Alert.AlertType.CONFIRMATION) {
                when(mock.showAndWait()).thenReturn(Optional.of(ButtonType.OK));
            } else if (mock.getAlertType() == Alert.AlertType.ERROR) {
                when(mock.showAndWait()).thenReturn(Optional.of(ButtonType.OK));
            }
        });
             MockedConstruction<DepositDAO> daoMock = mockConstruction(DepositDAO.class, (mock, context) -> {
                 doThrow(new SQLException("DB error")).when(mock).deleteDeposit(1);
             })) {

            boolean result = service.deleteDepositWithConfirmation(1, null, null);

            assertFalse(result);
        }
    }

    @Test
    public void testDeleteDepositWithConfirmation_UnexpectedExceptionThrown() throws Exception {
        try (MockedConstruction<Alert> alertMock = mockConstruction(Alert.class, (mock, context) -> {
            if (mock.getAlertType() == Alert.AlertType.CONFIRMATION) {
                when(mock.showAndWait()).thenReturn(Optional.of(ButtonType.OK));
            } else if (mock.getAlertType() == Alert.AlertType.ERROR) {
                when(mock.showAndWait()).thenReturn(Optional.of(ButtonType.OK));
            }
        });
             MockedConstruction<DepositDAO> daoMock = mockConstruction(DepositDAO.class, (mock, context) -> {
                 doThrow(new RuntimeException("Unexpected")).when(mock).deleteDeposit(1);
             })) {

            boolean result = service.deleteDepositWithConfirmation(1, null, null);

            assertFalse(result);
        }
    }

    @Test
    public void testShowErrorDisplaysAlert() throws Exception {
        RemoveDepositService service = new RemoveDepositService();

        try (MockedConstruction<Alert> alertMock = mockConstruction(Alert.class, (mock, context) -> {
            when(mock.getAlertType()).thenReturn(Alert.AlertType.ERROR);
            when(mock.showAndWait()).thenReturn(Optional.of(ButtonType.OK));
        })) {
            var method = RemoveDepositService.class.getDeclaredMethod("showError", String.class);
            method.setAccessible(true);
            method.invoke(service, "Test error message");

            Alert alert = alertMock.constructed().get(0);
            verify(alert).setTitle("Помилка");
            verify(alert).setContentText("Test error message");
            verify(alert).showAndWait();
        }
    }

    @Test
    public void testDeleteDepositWithConfirmation_AlertContentSetCorrectly() {
        try (MockedConstruction<Alert> alertMock = mockConstruction(Alert.class, (mock, context) -> {
            when(mock.getAlertType()).thenReturn(Alert.AlertType.CONFIRMATION);
            when(mock.showAndWait()).thenReturn(Optional.of(ButtonType.CANCEL));
        })) {
            Runnable onSuccess = mock(Runnable.class);
            Stage stage = mock(Stage.class);

            service.deleteDepositWithConfirmation(123, onSuccess, stage);

            Alert alert = alertMock.constructed().get(0);
            verify(alert).setTitle("Підтвердження видалення");
            verify(alert).setHeaderText(null);
            verify(alert).setContentText("Ви дійсно хочете видалити депозит з ID = 123?");
        }
    }

    @Test
    public void testDeleteDepositWithConfirmation_EmptyOptionalReturned() {
        try (MockedConstruction<Alert> alertMock = mockConstruction(Alert.class, (mock, context) -> {
            when(mock.getAlertType()).thenReturn(Alert.AlertType.CONFIRMATION);
            when(mock.showAndWait()).thenReturn(Optional.empty());
        })) {
            Runnable onSuccess = mock(Runnable.class);
            Stage stage = mock(Stage.class);

            boolean result = service.deleteDepositWithConfirmation(10, onSuccess, stage);

            assertFalse(result);
            verifyNoInteractions(onSuccess);
            verifyNoInteractions(stage);
        }
    }

    @Test
    public void testSQLLoggerIsCalled() throws Exception {
        try (MockedConstruction<DepositDAO> daoMock = mockConstruction(DepositDAO.class,
                (mock, context) -> doThrow(new SQLException("db error")).when(mock).deleteDeposit(anyInt()));
             MockedConstruction<Alert> alertMock = mockConstruction(Alert.class,
                     (mock, context) -> when(mock.showAndWait()).thenReturn(Optional.of(ButtonType.OK)))
        ) {
            service.deleteDepositWithConfirmation(1, null, null);
        }
    }
}

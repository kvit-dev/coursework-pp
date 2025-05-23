package service;

import controller.ChangeDepositInfo;
import controller.MainController;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.fxml.FXMLLoader;
import model.Deposit;
import model.DepositDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationExtension;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
class DepositCardServiceTest {

    private DepositCardService service;
    private Deposit mockDeposit;
    private MainController mockMainController;

    @BeforeEach
    void setUp() {
        service = new DepositCardService();
        mockDeposit = mock(Deposit.class);
        mockMainController = mock(MainController.class);

        when(mockDeposit.getId()).thenReturn(1);
        when(mockDeposit.getDepositName()).thenReturn("Test Deposit");
    }

    @Test
    void deleteDeposit_shouldDeleteAndRefresh() throws SQLException {
        try (
                MockedConstruction<DepositDAO> mockedDAO = mockConstruction(DepositDAO.class, (dao, context) ->
                        doNothing().when(dao).deleteDeposit(1));
                MockedConstruction<Alert> mockedAlert = mockConstruction(Alert.class, (alert, context) -> {
                    when(alert.showAndWait()).thenReturn(Optional.of(ButtonType.OK));
                })
        ) {
            Runnable successCallback = mock(Runnable.class);

            service.deleteDeposit(mockDeposit, mockMainController, successCallback);

            DepositDAO dao = mockedDAO.constructed().get(0);
            verify(dao).deleteDeposit(1);
            verify(successCallback).run();
            verify(mockMainController).refreshDeposits();
        }
    }

    @Test
    void deleteDeposit_shouldCancelOnUserDismiss() {
        try (
                MockedConstruction<DepositDAO> mockedDAO = mockConstruction(DepositDAO.class);
                MockedConstruction<Alert> mockedAlert = mockConstruction(Alert.class, (alert, context) -> {
                    when(alert.showAndWait()).thenReturn(Optional.of(ButtonType.CANCEL));
                })
        ) {
            service.deleteDeposit(mockDeposit, mockMainController, null);

            assert mockedDAO.constructed().isEmpty();
            verify(mockMainController, never()).refreshDeposits();
        }
    }

    @Test
    void deleteDeposit_shouldHandleExceptionGracefully() throws SQLException {
        try (
                MockedConstruction<DepositDAO> mockedDAO = mockConstruction(DepositDAO.class, (dao, context) ->
                        doThrow(new RuntimeException("DB error")).when(dao).deleteDeposit(1));
                MockedConstruction<Alert> mockedAlert = mockConstruction(Alert.class, (alert, context) ->
                        when(alert.showAndWait()).thenReturn(Optional.of(ButtonType.OK)))
        ) {
            DepositCardService spyService = Mockito.spy(service);
            doNothing().when(spyService).showAlert(anyString(), anyString());

            spyService.deleteDeposit(mockDeposit, mockMainController, null);

            DepositDAO dao = mockedDAO.constructed().get(0);
            verify(dao).deleteDeposit(1);
            verify(spyService).showAlert(eq("Помилка при видаленні"), contains("DB error"));
        }
    }

    @Test
    void editDeposit_shouldHandleExceptionGracefully() throws Exception {
        DepositCardService spyService = Mockito.spy(service);
        doNothing().when(spyService).showAlert(anyString(), anyString());

        try (MockedConstruction<FXMLLoader> mocked = mockConstruction(FXMLLoader.class,
                (mockLoader, context) -> {
                    when(mockLoader.load()).thenThrow(new IOException("FXML load error"));
                    when(mockLoader.getLocation()).thenReturn(getClass().getResource("/view/change-info.fxml"));
                })) {

            spyService.editDeposit(mockDeposit, mockMainController);

            verify(spyService).showAlert(eq("Помилка при відкритті редагування"), contains("FXML load error"));
        }
    }

    @Test
    void editDeposit_shouldOpenEditWindowSuccessfully() throws Exception {
        try (MockedConstruction<FXMLLoader> mockedLoader = mockConstruction(FXMLLoader.class,
                (loader, context) -> {
                    ChangeDepositInfo mockController = mock(ChangeDepositInfo.class);
                    when(loader.load()).thenReturn(new javafx.scene.layout.Pane());
                    when(loader.getController()).thenReturn(mockController);
                    when(loader.getLocation()).thenReturn(getClass().getResource("/view/change-info.fxml"));
                })) {

            DepositCardService spyService = Mockito.spy(service);
            doNothing().when(spyService).showAlert(anyString(), anyString());

            spyService.editDeposit(mockDeposit, mockMainController);

            FXMLLoader loader = mockedLoader.constructed().getFirst();
            verify(loader).load();
            verify(loader).getController();
        }
    }

    @Test
    void showAlert_shouldDisplayErrorAlert() {
        DepositCardService realService = new DepositCardService();

        try (MockedConstruction<Alert> mockedAlert = mockConstruction(Alert.class, (alert, context) -> {
            when(alert.showAndWait()).thenReturn(Optional.of(ButtonType.OK));
        })) {
            realService.showAlert("Test Title", "Test Message");

            Alert alert = mockedAlert.constructed().getFirst();
            verify(alert).setTitle("Test Title");
            verify(alert).setContentText("Test Message");
            verify(alert).showAndWait();
        }
    }

    @Test
    void deleteDeposit_shouldDoNothingWhenUserClosesDialog() {
        try (
                MockedConstruction<DepositDAO> mockedDAO = mockConstruction(DepositDAO.class);
                MockedConstruction<Alert> mockedAlert = mockConstruction(Alert.class, (alert, context) -> {
                    when(alert.showAndWait()).thenReturn(Optional.empty());
                })
        ) {
            service.deleteDeposit(mockDeposit, mockMainController, null);

            assert mockedDAO.constructed().isEmpty();
            verify(mockMainController, never()).refreshDeposits();
        }
    }

    @Test
    void deleteDeposit_shouldHandleNullMainControllerAndOnSuccess() throws SQLException {
        try (
                MockedConstruction<DepositDAO> mockedDAO = mockConstruction(DepositDAO.class, (dao, context) -> {
                    doNothing().when(dao).deleteDeposit(1);
                });
                MockedConstruction<Alert> mockedAlert = mockConstruction(Alert.class, (alert, context) -> {
                    when(alert.showAndWait()).thenReturn(Optional.of(ButtonType.OK));
                })
        ) {
            service.deleteDeposit(mockDeposit, null, null);

            DepositDAO dao = mockedDAO.constructed().get(0);
            verify(dao).deleteDeposit(1);
        }
    }
}

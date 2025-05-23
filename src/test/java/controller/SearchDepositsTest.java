package controller;

import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import model.Deposit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.SearchDepositsService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SearchDepositsTest {

    private SearchDeposits controller;
    private SearchDepositsService mockService;

    @BeforeEach
    public void setUp() {
        mockService = mock(SearchDepositsService.class);
        controller = new SearchDeposits(mockService);

        controller.bankField = new TextField();
        controller.tableView = new TableView<>();
        controller.initialize();
    }

    @Test
    public void testOnSearch_successfulSearch_populatesTable() throws Exception {
        controller.bankField.setText("MonoBank");
        Deposit deposit = new Deposit("MonoBank", "SuperDeposit", 12.5, 6, true, false);
        List<Deposit> deposits = List.of(deposit);
        when(mockService.searchByBankName("MonoBank")).thenReturn(deposits);

        controller.onSearch();

        ObservableList<Deposit> items = controller.tableView.getItems();
        assertEquals(1, items.size());
        Deposit actual = items.get(0);
        assertNotNull(actual);
        assertEquals(deposit, actual);
    }

    @Test
    public void testOnSearch_emptyResult_clearsTable() throws Exception {
        controller.bankField.setText("UnknownBank");
        when(mockService.searchByBankName("UnknownBank"))
                .thenReturn(List.of());

        controller.onSearch();
        assertTrue(controller.tableView.getItems().isEmpty());
    }

    @Test
    public void testOnSearch_serviceThrowsException_showsAlert() throws Exception {
        controller.bankField.setText("TestBank");
        when(mockService.searchByBankName("TestBank"))
                .thenThrow(new RuntimeException("DB Error"));

        SearchDeposits spyController = spy(controller);
        doNothing().when(spyController).showErrorAlert(anyString());

        spyController.onSearch();

        verify(spyController).showErrorAlert("DB Error");
    }
}

package service;

import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import model.Deposit;
import model.DepositDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AnalyticsServiceTest {

    private AnalyticsService analyticsService;

    @BeforeEach
    void setUp() {
        analyticsService = new AnalyticsService();
    }

    @Test
    void testFetchDeposits_success() throws Exception {
        List<Deposit> mockDeposits = List.of(
                new Deposit(1, "Bank1", "Dep1", 5.0, 12, true, false),
                new Deposit(2, "Bank2", "Dep2", 6.0, 6, false, true)
        );

        try (MockedConstruction<DepositDAO> mocked = Mockito.mockConstruction(DepositDAO.class,
                (mock, context) -> Mockito.when(mock.getAllDeposits()).thenReturn(mockDeposits))) {

            List<Deposit> result = analyticsService.fetchDeposits();
            assertEquals(2, result.size());
            assertEquals("Bank1", result.get(0).getBankName());
        }
    }

    @Test
    void testGetWithdrawalPieData() {
        List<Deposit> deposits = List.of(
                new Deposit(1, "Bank1", "Dep1", 5.0, 12, true, false),
                new Deposit(2, "Bank2", "Dep2", 5.5, 6, false, false),
                new Deposit(3, "Bank3", "Dep3", 6.0, 12, true, false)
        );

        ObservableList<PieChart.Data> data = analyticsService.getWithdrawalPieData(deposits);
        assertEquals(2, data.size());

        PieChart.Data withdrawAllowed = data.get(0);
        PieChart.Data withdrawNotAllowed = data.get(1);

        assertEquals("Дострокове зняття дозволено", withdrawAllowed.getName());
        assertEquals(2, withdrawAllowed.getPieValue());

        assertEquals("Без дострокового зняття", withdrawNotAllowed.getName());
        assertEquals(1, withdrawNotAllowed.getPieValue());
    }

    @Test
    void testGetAverageRateByTermSeries() {
        List<Deposit> deposits = List.of(
                new Deposit(1, "Bank1", "Dep1", 5.0, 12, true, false),
                new Deposit(2, "Bank2", "Dep2", 7.0, 12, false, true),
                new Deposit(3, "Bank3", "Dep3", 4.0, 6, true, false)
        );

        XYChart.Series<String, Number> series = analyticsService.getAverageRateByTermSeries(deposits);

        assertEquals(2, series.getData().size());

        XYChart.Data<String, Number> data1 = series.getData().get(0);
        XYChart.Data<String, Number> data2 = series.getData().get(1);

        boolean found12 = false;
        boolean found6 = false;

        for (XYChart.Data<String, Number> data : series.getData()) {
            if (data.getXValue().equals("12 міс")) {
                assertEquals(6.0, data.getYValue().doubleValue(), 0.001);
                found12 = true;
            } else if (data.getXValue().equals("6 міс")) {
                assertEquals(4.0, data.getYValue().doubleValue(), 0.001);
                found6 = true;
            }
        }
        assertTrue(found12 && found6);
    }
}

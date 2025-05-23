package controller;

import javafx.collections.FXCollections;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.Deposit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import service.AnalyticsService;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AnalyticsControllerTest extends ApplicationTest {

    private AnalyticsController controller;

    @Override
    public void start(Stage stage) {
        stage.setScene(new javafx.scene.Scene(new StackPane(), 800, 600));
        stage.show();
    }

    @BeforeEach
    public void setUp() throws Exception {
        controller = new AnalyticsController();

        controller.withdrawalPie = new PieChart();
        controller.avgRateChart = new BarChart<>(new javafx.scene.chart.CategoryAxis(), new javafx.scene.chart.NumberAxis());

        AnalyticsService analyticsService = mock(AnalyticsService.class);
        List<Deposit> mockDeposits = Arrays.asList(
                new Deposit("Банк 1", "Депозит 1", 10.0, 6, true),
                new Deposit("Банк 2", "Депозит 2", 12.0, 12, false)
        );

        when(analyticsService.fetchDeposits()).thenReturn(mockDeposits);
        when(analyticsService.getWithdrawalPieData(mockDeposits)).thenReturn(FXCollections.observableArrayList(
                new PieChart.Data("Можна знімати", 1),
                new PieChart.Data("Не можна знімати", 1)
        ));

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Середня ставка");
        series.getData().add(new XYChart.Data<>("6", 10));
        series.getData().add(new XYChart.Data<>("12", 12));
        when(analyticsService.getAverageRateByTermSeries(mockDeposits)).thenReturn(series);

        setPrivateField(controller, "analyticsService", analyticsService);
    }

    @Test
    public void testInitialize_PopulatesCharts() {
        runOnFxThread(() -> {
            controller.initialize();

            assertEquals(2, controller.withdrawalPie.getData().size());
            assertEquals("Можна знімати", controller.withdrawalPie.getData().get(0).getName());
            assertEquals(1, controller.withdrawalPie.getData().get(0).getPieValue());

            assertEquals(1, controller.avgRateChart.getData().size());
            XYChart.Series<String, Number> series = controller.avgRateChart.getData().get(0);
            assertEquals("Середня ставка", series.getName());
            assertEquals(2, series.getData().size());
        });
    }

    private void runOnFxThread(Runnable action) {
        try {
            javafx.application.Platform.runLater(action);
            waitForFxEvents();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void waitForFxEvents() throws InterruptedException {
        Thread.sleep(100);
    }

    private void setPrivateField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Не вдалося встановити поле: " + fieldName, e);
        }
    }

    @Test
    public void testInitialize_ExceptionHandledGracefully() throws Exception {
        AnalyticsService faultyService = mock(AnalyticsService.class);
        when(faultyService.fetchDeposits()).thenThrow(new RuntimeException("Помилка з'єднання"));

        setPrivateField(controller, "analyticsService", faultyService);

        runOnFxThread(() -> {
            assertDoesNotThrow(() -> controller.initialize());
            assertTrue(controller.withdrawalPie.getData().isEmpty());
            assertTrue(controller.avgRateChart.getData().isEmpty());
        });
    }
}

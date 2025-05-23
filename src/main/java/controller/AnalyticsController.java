package controller;

import javafx.fxml.FXML;
import javafx.scene.chart.*;
import model.Deposit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.AnalyticsService;

import java.util.List;

public class AnalyticsController {
    private static final Logger logger = LoggerFactory.getLogger(AnalyticsController.class);
    private final AnalyticsService analyticsService = new AnalyticsService();

    @FXML
    PieChart withdrawalPie;
    @FXML
    BarChart<String, Number> avgRateChart;

    @FXML
    public void initialize() {
        try {
            List<Deposit> deposits = analyticsService.fetchDeposits();
            withdrawalPie.setData(analyticsService.getWithdrawalPieData(deposits));
            avgRateChart.getData().add(analyticsService.getAverageRateByTermSeries(deposits));
        } catch (Exception e) {
            logger.error("Помилка під час ініціалізації аналітики", e);
        }
    }
}

package service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import model.Deposit;
import model.DepositDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class AnalyticsService {
    private static final Logger logger = LoggerFactory.getLogger(AnalyticsService.class);

    public List<Deposit> fetchDeposits() throws Exception {
        DepositDAO dao = new DepositDAO();
        List<Deposit> deposits = dao.getAllDeposits();
        logger.info("Завантажено {} депозитів для аналітики", deposits.size());
        return deposits;
    }

    public ObservableList<PieChart.Data> getWithdrawalPieData(List<Deposit> deposits) {
        long canWithdraw = deposits.stream().filter(Deposit::isEarlyWithdrawal).count();
        long cannotWithdraw = deposits.size() - canWithdraw;

        return FXCollections.observableArrayList(
                new PieChart.Data("Дострокове зняття дозволено", canWithdraw),
                new PieChart.Data("Без дострокового зняття", cannotWithdraw)
        );
    }

    public XYChart.Series<String, Number> getAverageRateByTermSeries(List<Deposit> deposits) {
        Map<Integer, List<Deposit>> grouped = deposits.stream()
                .collect(Collectors.groupingBy(Deposit::getTermMonths));

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (var entry : grouped.entrySet()) {
            double avg = entry.getValue().stream().mapToDouble(Deposit::getInterestRate).average().orElse(0);
            series.getData().add(new XYChart.Data<>(entry.getKey() + " міс", avg));
        }
        return series;
    }
}

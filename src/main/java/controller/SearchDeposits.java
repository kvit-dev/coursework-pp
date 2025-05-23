package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Deposit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.SearchDepositsService;

import java.sql.SQLException;
import java.util.List;

public class SearchDeposits {
    @FXML public TextField bankField;
    @FXML public TableView<Deposit> tableView;
    @FXML public Button searchButton;

    private static final Logger logger = LoggerFactory.getLogger(SearchDeposits.class);
    private final SearchDepositsService searchService;

    // Основний конструктор для JavaFX
    public SearchDeposits() throws SQLException {
        this(new SearchDepositsService());
    }

    // Інжектований конструктор для тестування
    public SearchDeposits(SearchDepositsService searchService) {
        this.searchService = searchService;
    }

    @FXML
    public void onSearch() {
        String bankName = bankField.getText();
        logger.info("Пошук депозитів по банку: {}", bankName);

        try {
            List<Deposit> results = searchService.searchByBankName(bankName);
            tableView.getItems().setAll(results);
            logger.info("Знайдено {} депозитів для банку: {}", results.size(), bankName);
        } catch (Exception e) {
            logger.error("Помилка пошуку депозитів", e);
            showErrorAlert(e.getMessage());
        }
    }

    protected void showErrorAlert(String message) {
        new Alert(Alert.AlertType.ERROR, "Помилка пошуку: " + message).show();
    }

    @FXML
    public void initialize() {
        tableView.getColumns().addAll(
                new TableColumn<>("Банк") {{ setCellValueFactory(new PropertyValueFactory<>("bankName")); }},
                new TableColumn<>("Депозит") {{ setCellValueFactory(new PropertyValueFactory<>("depositName")); }},
                new TableColumn<>("Ставка") {{ setCellValueFactory(new PropertyValueFactory<>("interestRate")); }},
                new TableColumn<>("Термін") {{ setCellValueFactory(new PropertyValueFactory<>("termMonths")); }},
                new TableColumn<>("Достр. зняття") {{ setCellValueFactory(new PropertyValueFactory<>("earlyWithdrawal")); }},
                new TableColumn<>("Поповнення") {{ setCellValueFactory(new PropertyValueFactory<>("replenishment")); }}
        );
        logger.info("Ініціалізовано таблицю для пошуку депозитів");
    }
}

package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import javafx.scene.Parent;
import model.Deposit;
import service.MainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class MainController {

    @FXML private FlowPane depositCardsContainer;
    @FXML private TextField searchField;
    @FXML private Button termSortButton;

    private boolean isSortingActive = false;

    private final MainService mainService = new MainService();
    private List<Deposit> currentDeposits;
    private List<Deposit> originalDeposits;

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    public MainController() throws SQLException {
    }

    @FXML
    void initialize() {
        logger.info("Ініціалізація");
        loadAllDeposits();
        resetSortingState();
    }

    public void refreshDeposits() {
        logger.info("Оновлення депозитів");
        loadAllDeposits();
        resetSortingState();
    }

    private void loadAllDeposits() {
        try {
            currentDeposits = mainService.loadAllDeposits();
            originalDeposits = List.copyOf(currentDeposits);
            displayDeposits(currentDeposits);
        } catch (Exception e) {
            logger.error("Помилка завантаження депозитів", e);
            showError("Помилка завантаження депозитів", e);
        }
    }

    @FXML
    public void onSearch() {
        try {
            String bankName = searchField.getText().trim();
            currentDeposits = mainService.searchDeposits(bankName);
            originalDeposits = List.copyOf(currentDeposits);
            displayDeposits(currentDeposits);
            resetSortingState();
        } catch (Exception e) {
            logger.error("Помилка пошуку", e);
            showError("Помилка пошуку", e);
        }
    }

    @FXML
    public void onTermSort() {
        isSortingActive = !isSortingActive;
        if (isSortingActive) {
            logger.info("Сортування активне");
            termSortButton.setText("Скасувати сортування");
            currentDeposits = mainService.sortDepositsByTerm(currentDeposits);
        } else {
            logger.info("Сортування скасоване");
            termSortButton.setText("Сортувати за терміном");
            currentDeposits = List.copyOf(originalDeposits);
        }
        displayDeposits(currentDeposits);
    }

    @FXML
    public void openAddDeposit(ActionEvent event) throws Exception {
        logger.info("Відкриття вікна 'Додати вклад'");
        AddDeposit controller = loadWindow("/view/add-deposit.fxml", "Додати вклад", 400, 400, false);
        controller.setMainController(this);
    }

    @FXML
    public void openCalculator(ActionEvent event) throws Exception {
        logger.info("Відкриття вікна 'Калькулятор доходу'");
        loadWindow("/view/calculator.fxml", "Калькулятор доходу", 400, 400, false);
    }

    @FXML
    public void openAnalytics(ActionEvent event) throws Exception {
        logger.info("Відкриття вікна 'Аналітика'");
        loadWindow("/view/analytics.fxml", "Аналітика", 600, 700, false);
    }

    private <T> T loadWindow(String fxmlPath, String title, int width, int height, boolean resizable) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        Scene scene = new Scene(root, width, height);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style/styles.css")).toExternalForm());
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(scene);
        stage.setResizable(resizable);
        stage.show();

        return loader.getController();
    }

    private void showError(String title, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }

    private void resetSortingState() {
        isSortingActive = false;
        if (termSortButton != null) {
            termSortButton.setText("Сортувати за терміном");
        }
    }

    private void displayDeposits(List<Deposit> deposits) {
        try {
            depositCardsContainer.getChildren().clear();
            for (Deposit deposit : deposits) {
                depositCardsContainer.getChildren().add(mainService.createDepositCard(deposit, this));
            }
        } catch (Exception e) {
            logger.error("Помилка відображення депозитів", e);
            showError("Помилка відображення депозитів", e);
        }
    }
}

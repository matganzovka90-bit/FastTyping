package speed.fasttyping.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import speed.fasttyping.dao.DaoFactory;
import speed.fasttyping.dao.TypingResultDao;
import speed.fasttyping.model.Achievement;
import speed.fasttyping.model.TypingResult;
import speed.fasttyping.util.AchievementManager;
import speed.fasttyping.util.SceneNavigator;
import speed.fasttyping.util.SessionManager;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StatsController {

    @FXML private Label bestWpmLabel;
    @FXML private Label avgAccuracyLabel;
    @FXML private Label totalSessionsLabel;

    @FXML private TableView<TypingResult> resultsTable;
    @FXML private TableColumn<TypingResult, Integer> wpmColumn;
    @FXML private TableColumn<TypingResult, Double> accuracyColumn;
    @FXML private TableColumn<TypingResult, Integer> errorsColumn;
    @FXML private TableColumn<TypingResult, String> modeColumn;
    @FXML private TableColumn<TypingResult, String> dateColumn;

    @FXML private Label    achievementsCountLabel;
    @FXML private FlowPane achievementsPane;

    @FXML
    public void initialize() {
        setupColumns();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        dateColumn.setCellValueFactory(cellData -> {
            LocalDateTime dt = cellData.getValue().getCreatedAt();
            String formattedDate = (dt != null) ? dt.format(formatter) : "";
            return new SimpleStringProperty(formattedDate);
        });

        loadStats();
        loadAchievements();
    }
    @FXML
    private void handleBackClick(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneNavigator.navigateTo(stage, "main.fxml", "Тренажер сліпого друку");
    }

    private void setupColumns() {
        wpmColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getWpm()).asObject());

        accuracyColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleDoubleProperty(cell.getValue().getAccuracy()).asObject());

        errorsColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getErrors()).asObject());

        modeColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(cell.getValue().getModeName()));

        dateColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().getCreatedAt().toString()
                ));
    }

    private void loadStats() {
        if (!SessionManager.getInstance().isLoggedIn()) return;

        int userId = SessionManager.getInstance().getCurrentUser().getId();

        try {
            TypingResultDao dao = DaoFactory.getInstance().getTypingResultDao();

            List<TypingResult> results = dao.getByUserId(userId);

            resultsTable.setItems(FXCollections.observableArrayList(results));

            int bestWpm = dao.getBestWpm(userId);
            bestWpmLabel.setText(String.valueOf(bestWpm));

            totalSessionsLabel.setText(String.valueOf(results.size()));

            if (!results.isEmpty()) {
                double avgAccuracy = results.stream()
                        .mapToDouble(TypingResult::getAccuracy)
                        .average()
                        .orElse(0.0);
                avgAccuracyLabel.setText(String.format("%.1f%%", avgAccuracy));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadAchievements() {
        if (!SessionManager.getInstance().isLoggedIn()) return;

        int userId = SessionManager.getInstance().getCurrentUser().getId();

        AchievementManager mgr = AchievementManager.getInstance();
        mgr.loadFromDatabase(userId);

        achievementsCountLabel.setText(
                mgr.getUnlockedCount() + " / " + mgr.getTotalCount()
        );

        achievementsPane.getChildren().clear();
        for (Achievement a : mgr.getAllAchievements()) {
            achievementsPane.getChildren().add(buildCard(a));
        }
    }

    private VBox buildCard(Achievement a) {
        boolean done = a.isUnlocked();

        Label icon = new Label(done ? a.getIcon() : "🔒");
        icon.setStyle("-fx-font-size: 28px;");

        Label title = new Label(a.getTitle());
        title.setWrapText(true);
        title.setPrefWidth(170);
        title.setStyle(done
                ? "-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #c5e1a5;"
                : "-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #666666;");

        Label desc = new Label(a.getDescription());
        desc.setWrapText(true);
        desc.setPrefWidth(170);
        desc.setStyle(done
                ? "-fx-font-size: 11px; -fx-text-fill: #8fba6a;"
                : "-fx-font-size: 11px; -fx-text-fill: #ffffff;");

        VBox card = new VBox(6, icon, title, desc);
        card.setPrefWidth(210);
        card.setMaxWidth(210);
        card.setStyle(done
                ? "-fx-background-color: #1e3a1e; -fx-border-color: #629755;" +
                "-fx-border-width: 1.5; -fx-border-radius: 10;" +
                "-fx-background-radius: 10; -fx-padding: 12;"
                : "-fx-background-color: #323232; -fx-border-color: #444444;" +
                "-fx-border-width: 1; -fx-border-radius: 10;" +
                "-fx-background-radius: 10; -fx-padding: 12; -fx-opacity: 0.55;");

        Tooltip tip = new Tooltip(done ? "Розблоковано!" : "🔒 " + a.getDescription());
        tip.setStyle("-fx-font-size: 12px;");
        Tooltip.install(card, tip);

        return card;
    }
}
package speed.fasttyping.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import speed.fasttyping.dao.DaoFactory;
import speed.fasttyping.dao.TypingResultDao;
import speed.fasttyping.model.TypingResult;
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

    @FXML
    private void handleBackClick(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneNavigator.navigateTo(stage, "main.fxml", "Тренажер сліпого друку");
    }
}
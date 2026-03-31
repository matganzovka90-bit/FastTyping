package speed.fasttyping.util;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import speed.fasttyping.model.TypingResult;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChartBuilder {
    public static void buildWpmChart(LineChart<String, Number> chart,
                                     List<TypingResult> results) {
        chart.getData().clear();
        chart.setAnimated(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("WPM");

        int start = Math.max(0, results.size() - 20);
        List<TypingResult> last20 = results.subList(start, results.size());

        for (int i = 0; i < last20.size(); i++) {
            series.getData().add(
                    new XYChart.Data<>(String.valueOf(i + 1), last20.get(i).getWpm())
            );
        }

        chart.getData().add(series);
        styleChart(chart, "#4b6eaf");
    }

    public static void buildAccuracyChart(BarChart<String, Number> chart,
                                          List<TypingResult> results) {
        chart.getData().clear();
        chart.setAnimated(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Середня точність (%)");

        Map<String, Double> avgByMode = results.stream()
                .collect(Collectors.groupingBy(
                        TypingResult::getModeName,
                        Collectors.averagingDouble(TypingResult::getAccuracy)
                ));

        for (Map.Entry<String, Double> entry : avgByMode.entrySet()) {
            double rounded = Math.round(entry.getValue() * 10.0) / 10.0;
            series.getData().add(new XYChart.Data<>(entry.getKey(), rounded));
        }

        chart.getData().add(series);
        styleChart(chart, "#629755");
    }

    public static void buildErrorsChart(BarChart<String, Number> chart,
                                        List<TypingResult> results) {
        chart.getData().clear();
        chart.setAnimated(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Середні помилки");

        Map<String, Double> avgErrorsByMode = results.stream()
                .collect(Collectors.groupingBy(
                        TypingResult::getModeName,
                        Collectors.averagingInt(TypingResult::getErrors)
                ));

        for (Map.Entry<String, Double> entry : avgErrorsByMode.entrySet()) {
            double rounded = Math.round(entry.getValue() * 10.0) / 10.0;
            series.getData().add(new XYChart.Data<>(entry.getKey(), rounded));
        }

        chart.getData().add(series);
        styleChart(chart, "#cc3232");
    }

    private static void styleChart(Chart chart, String accentColor) {
        chart.setStyle("-fx-background-color: transparent;");

        chart.applyCss();

        if (chart instanceof LineChart) {
            chart.lookupAll(".chart-line-symbol").forEach(s -> {
                s.setStyle("-fx-background-color: " + accentColor + ", #ffffff; " +
                        "-fx-background-radius: 5px; -fx-padding: 3px;");
            });
        }

        chart.lookupAll(".chart-bar").forEach(bar -> {
            bar.setStyle("-fx-bar-fill: " + accentColor + "; " +
                    "-fx-background-radius: 8 8 0 0; " +
                    "-fx-opacity: 0.85;");

            bar.setOnMouseEntered(e -> bar.setStyle("-fx-bar-fill: " + accentColor + "; -fx-opacity: 1.0;"));
            bar.setOnMouseExited(e -> bar.setStyle("-fx-bar-fill: " + accentColor + "; -fx-opacity: 0.85;"));
        });

        chart.lookupAll(".chart-legend").forEach(l ->
                l.setStyle("-fx-background-color: transparent; -fx-text-fill: #a9b7c6;"));
    }
}

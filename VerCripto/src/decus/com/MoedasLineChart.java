package decus.com;

import java.util.Map;

import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
// Grafico
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;

public class MoedasLineChart {
  private String moedaPadrao;
  private Map<String, Double> valores;

  public MoedasLineChart(String moedaPadrao, Map<String, Double> valores) {
    this.moedaPadrao = moedaPadrao;
    this.valores = valores;
  }

  public Scene getScene() {
    final CategoryAxis xAxis = new CategoryAxis();
    final NumberAxis yAxis = new NumberAxis();
    xAxis.setLabel("Dia");
    // creating the chart
    final LineChart<String, Number> lineChart = new LineChart<String, Number>(xAxis, yAxis);

    // defining a series
    XYChart.Series<String, Number> series = new XYChart.Series<>();
    series.setName(moedaPadrao);
    // populating the series with data
    for (String chave : valores.keySet()) {
      series.getData().add(new XYChart.Data<String, Number>(chave.substring(0, 5), valores.get(chave)));
    }
    lineChart.getData().add(series);

    for (XYChart.Series<String, Number> s : lineChart.getData()) {
      for (XYChart.Data<String, Number> d : s.getData()) {
        Tooltip.install(d.getNode(),
            new Tooltip("Valor: " + d.getYValue()));
        // Adding class on hover
        d.getNode().setOnMouseEntered(event -> d.getNode().getStyleClass().add("onHover"));
        // Removing class on exit
        d.getNode().setOnMouseExited(event -> d.getNode().getStyleClass().remove("onHover"));
      }
    }
    return new Scene(lineChart, 800, 600);
  }
}

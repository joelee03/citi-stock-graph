package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

public class App extends Application {

    private static Queue<Double> stockPrices = new LinkedList<>();
    private static Queue<Long> timeStamps = new LinkedList<>();
    private XYChart.Series<Number, Number> series = new XYChart.Series<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Dow Jones Industrial Average Tracker");

        // Defining the x and y axes
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Time");
        yAxis.setLabel("Stock Price");

        // Creating the line chart
        final LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Dow Jones Industrial Average");

        series.setName("DJIA Stock Price");
        lineChart.getData().add(series);

        // Setting up the scene
        Scene scene = new Scene(lineChart, 800, 600);
        stage.setScene(scene);
        stage.show();

        // Schedule the task to update the stock prices
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                fetchAndStoreStockData();
                javafx.application.Platform.runLater(() -> updateGraph());
            }
        }, 0, 5000);
    }

    private void fetchAndStoreStockData() {
        try {
            Stock stock = YahooFinance.get("^DJI");
            Double price = stock.getQuote().getPrice().doubleValue();
            Long currentTime = System.currentTimeMillis();

            stockPrices.add(price);
            timeStamps.add(currentTime);

            if (stockPrices.size() > 20) {  // Keep the last 20 points
                stockPrices.poll();
                timeStamps.poll();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateGraph() {
        series.getData().clear();
        int i = 0;
        for (Double price : stockPrices) {
            series.getData().add(new XYChart.Data<>(i++, price));
        }
    }
}

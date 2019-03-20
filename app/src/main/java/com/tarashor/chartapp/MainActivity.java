package com.tarashor.chartapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.tarashor.chartapp.models.TelegramFileData;
import com.tarashor.chartapp.models.ChartToChartDataConverter;
import com.tarashor.chartlib.TelegramChart;
import com.tarashor.chartlib.chart.Chart;
import com.tarashor.chartlib.data.DateToIntChartData;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TelegramChart telegramChart;
    private int chartIndex = 0;
    private List<TelegramFileData> telegramFileData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        telegramChart = findViewById(R.id.chart);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setData(100, 100);
            }
        });
        ChartJsonParser parser = new ChartJsonParser();
        telegramFileData = parser.parseColumns(this);
    }


    private void setData(int count, int range) {
        // now in hours

//        Date[] dates = new Date[count];
//        Integer[] yValues = new Integer[count];
//
//        for (int i = 0; i < count; i++) {
//            Calendar calendar = Calendar.getInstance();
//            calendar.add(Calendar.HOUR, i);
//            dates[i] = calendar.getTime();
//
//            yValues[i] = getRandom(range, 50);
//        }

//
//
//        Line<Integer> line = new Line<>(yValues, "#FF0000");
//
//        DateToIntChartData chartData = new DateToIntChartData(dates, new Line[]{line});
//
//        telegramChart.setData(chartData);


        if(chartIndex < telegramFileData.size()) {
            DateToIntChartData chartData = new ChartToChartDataConverter().convert(telegramFileData.get(chartIndex));
            chartIndex++;
            telegramChart.setData(chartData);
        }

    }

    protected int getRandom(int range, int start) {
        return (int)(Math.random() * range) + start;
    }
}

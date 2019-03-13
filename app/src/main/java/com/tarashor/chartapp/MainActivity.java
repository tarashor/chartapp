package com.tarashor.chartapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.tarashor.chartlib.DateToIntChartData;
import com.tarashor.chartlib.Line;
import com.tarashor.chartlib.TelegramChart;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private TelegramChart telegramChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        telegramChart = findViewById(R.id.chart);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setData(100, 100);
            }
        });
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
//        Line<Integer> line = new Line<>(yValues, "#FF0000");
//
//        DateToIntChartData chartData = new DateToIntChartData(dates, new Line[]{line});
//
//        telegramChart.setData(chartData);

        Date[] dates = new Date[3];
        Integer[] yValues = new Integer[3];

        Calendar calendar = Calendar.getInstance();
        dates[0] = calendar.getTime();
        calendar.add(Calendar.HOUR, 100);
        dates[1] = calendar.getTime();
        calendar.add(Calendar.HOUR, 100);
        dates[2] = calendar.getTime();
        yValues[0] = 100;
        yValues[1] = 500;
        yValues[2] = 300;


        Line<Integer> line = new Line<>(yValues, "#FF0000");

        DateToIntChartData chartData = new DateToIntChartData(dates, new Line[]{line});

        telegramChart.setData(chartData);

    }

    protected int getRandom(int range, int start) {
        return (int)(Math.random() * range) + start;
    }
}

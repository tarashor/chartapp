package com.tarashor.chartapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.github.mikephil.charting.data.ChartData;
import com.tarashor.chartapp.models.Column;
import com.tarashor.chartapp.models.TelegramFileData;
import com.tarashor.chartapp.models.ChartToChartDataConverter;
import com.tarashor.chartlib.chart.Chart;
import com.tarashor.chartlib.data.DateToIntChartData;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Chart telegramChart;
    private int chartIndex = 4;
    private List<TelegramFileData> telegramFileData;
    private ListView checkBoxList;
    private CheckBoxAdapter checkBoxAdapter;

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
        checkBoxList = findViewById(R.id.checkbox_list);
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
            addCheckBoxes(telegramFileData.get(chartIndex));
            DateToIntChartData chartData = new ChartToChartDataConverter().convert(telegramFileData.get(chartIndex));
            telegramChart.setData(chartData);
        }

    }

    private void addCheckBoxes(final TelegramFileData fileData) {
        List<CheckBoxItem> items = new ArrayList<>();
        for(Column<Integer> column : fileData.getYColumns()){
            CheckBoxItem checkBoxItem = new CheckBoxItem();
            checkBoxItem.setColor(column.getColor());
            checkBoxItem.setName(column.getName());
            checkBoxItem.setEnabled(column.isEnabled());
            items.add(checkBoxItem);
        }

        if(checkBoxAdapter == null) {
            checkBoxAdapter = new CheckBoxAdapter(this, items);
            checkBoxList.setAdapter(checkBoxAdapter);
            checkBoxAdapter.setOnCheckedChangeListener(new CheckBoxAdapter.CheckedChange() {
                @Override
                public void onChange(boolean checked, int pos) {
                    fileData.getYColumns().get(pos).setEnabled(checked);
                    setData(0, 0);
                }
            });
        } else {
            checkBoxAdapter.setCheckBoxItems(items);
        }
    }
}

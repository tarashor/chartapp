package com.tarashor.chartapp;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.Menu;
import android.view.MenuItem;

import com.tarashor.chartapp.models.ChartToChartDataConverter;
import com.tarashor.chartapp.models.TelegramFileData;
import com.tarashor.chartapp.view.LinesListView;
import com.tarashor.chartlib.TelegramChart;
import com.tarashor.chartlib.data.DateToIntChartData;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    private List<TelegramFileData> telegramFileData;

    private NestedScrollView nestedScrollView;

    private TelegramChart chart1;
    private LinesListView linesListView1;
    private TelegramChart chart2;
    private LinesListView linesListView2;
    private TelegramChart chart3;
    private LinesListView linesListView3;
    private TelegramChart chart4;
    private LinesListView linesListView4;
    private TelegramChart chart5;
    private LinesListView linesListView5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chart1 = findViewById(R.id.chart1);
        linesListView1 = findViewById(R.id.checkbox_list1);
        chart2 = findViewById(R.id.chart2);
        linesListView2 = findViewById(R.id.checkbox_list2);
        chart3 = findViewById(R.id.chart3);
        linesListView3 = findViewById(R.id.checkbox_list3);
        chart4 = findViewById(R.id.chart4);
        linesListView4 = findViewById(R.id.checkbox_list4);
        chart5 = findViewById(R.id.chart5);
        linesListView5 = findViewById(R.id.checkbox_list5);

        linesListView1.setOnCheckedChangeListener(new LinesListView.CheckedChange() {
            @Override
            public void onChange(boolean checked, String name) {
                chart1.setVisibilityForLine(name, checked);
            }
        });

        linesListView2.setOnCheckedChangeListener(new LinesListView.CheckedChange() {
            @Override
            public void onChange(boolean checked, String name) {
                chart2.setVisibilityForLine(name, checked);
            }
        });

        linesListView3.setOnCheckedChangeListener(new LinesListView.CheckedChange() {
            @Override
            public void onChange(boolean checked, String name) {
                chart3.setVisibilityForLine(name, checked);
            }
        });

        linesListView4.setOnCheckedChangeListener(new LinesListView.CheckedChange() {
            @Override
            public void onChange(boolean checked, String name) {
                chart4.setVisibilityForLine(name, checked);
            }
        });

        linesListView5.setOnCheckedChangeListener(new LinesListView.CheckedChange() {
            @Override
            public void onChange(boolean checked, String name) {
                chart5.setVisibilityForLine(name, checked);
            }
        });


        if(savedInstanceState == null) {
            ChartJsonParser parser = new ChartJsonParser();
            telegramFileData = parser.parseColumns(this);
            setData();
        }
    }


    private void setData() {
        ChartToChartDataConverter chartToChartDataConverter = new ChartToChartDataConverter();
        DateToIntChartData chartData1 = chartToChartDataConverter.convert(telegramFileData.get(0));
        DateToIntChartData chartData2 = chartToChartDataConverter.convert(telegramFileData.get(1));
        DateToIntChartData chartData3 = chartToChartDataConverter.convert(telegramFileData.get(2));
        DateToIntChartData chartData4 = chartToChartDataConverter.convert(telegramFileData.get(3));
        DateToIntChartData chartData5 = chartToChartDataConverter.convert(telegramFileData.get(4));

        chart1.setData(chartData1);
        chart2.setData(chartData2);
        chart3.setData(chartData3);
        chart4.setData(chartData4);
        chart5.setData(chartData5);

        linesListView1.setData(chartData1);
        linesListView2.setData(chartData2);
        linesListView3.setData(chartData3);
        linesListView4.setData(chartData4);
        linesListView5.setData(chartData5);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.night_mode){
            toggleTheme();
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggleTheme() {
        getDelegate().setLocalNightMode(isNightMode() ? AppCompatDelegate.MODE_NIGHT_NO : AppCompatDelegate.MODE_NIGHT_YES);
    }

    private boolean isNightMode(){
        int currentNightMode = getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;

        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                return false;
            case Configuration.UI_MODE_NIGHT_YES:
                return true;
                default: return false;
        }
    }
}

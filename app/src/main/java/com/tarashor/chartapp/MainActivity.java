package com.tarashor.chartapp;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.tarashor.chartapp.models.ChartToChartDataConverter;
import com.tarashor.chartapp.models.TelegramFileData;
import com.tarashor.chartapp.view.ChartView;
import com.tarashor.chartapp.view.LinesListView;
import com.tarashor.chartlib.TelegramChart;
import com.tarashor.chartlib.data.DateToIntChartData;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class MainActivity extends AppCompatActivity {

    private List<TelegramFileData> telegramFileData;
    private LinearLayout chartContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ChartJsonParser parser = new ChartJsonParser();
        telegramFileData = parser.parseColumns(this);
        chartContainer = findViewById(R.id.chart_container);

        setData();
    }


    private void setData() {
        ChartToChartDataConverter chartToChartDataConverter = new ChartToChartDataConverter();
        for(TelegramFileData fileData : telegramFileData){
            ChartView chartView = new ChartView(this);
            chartView.setData(chartToChartDataConverter.convert(fileData));
            chartContainer.addView(chartView);
        }
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
        getDelegate().applyDayNight();
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

package com.tarashor.chartapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.tarashor.chartapp.models.ChartToChartDataConverter;
import com.tarashor.chartapp.models.Column;
import com.tarashor.chartapp.models.TelegramFileData;
import com.tarashor.chartlib.chart.Chart;
import com.tarashor.chartlib.data.DateToIntChartData;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ChartFragment extends Fragment {
    private static final String CHART_DATA_KEY = "chart_data";

    private Chart chartView;
    private ListView checkBoxListView;
    private CheckBoxAdapter checkBoxAdapter;
    private TelegramFileData fileData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chart, container, false);
        chartView = view.findViewById(R.id.chart);
        checkBoxListView = view.findViewById(R.id.checkbox_list);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if(bundle != null){
           fileData = (TelegramFileData) bundle.getSerializable(CHART_DATA_KEY);
        }
        setData();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void addCheckBoxes() {
        List<CheckBoxItem> items = new ArrayList<>();
        for(Column<Integer> column : fileData.getYColumns()){
            CheckBoxItem checkBoxItem = new CheckBoxItem();
            checkBoxItem.setColor(column.getColor());
            checkBoxItem.setName(column.getName());
            checkBoxItem.setEnabled(column.isEnabled());
            items.add(checkBoxItem);
        }

        if(checkBoxAdapter == null) {
            checkBoxAdapter = new CheckBoxAdapter(getContext(), items);
            checkBoxListView.setAdapter(checkBoxAdapter);
            checkBoxAdapter.setOnCheckedChangeListener(new CheckBoxAdapter.CheckedChange() {
                @Override
                public void onChange(boolean checked, int pos) {
                    fileData.getYColumns().get(pos).setEnabled(checked);
                    setData();
                }
            });
        } else {
            if(checkBoxListView.getAdapter() == null){
                checkBoxListView.setAdapter(checkBoxAdapter);
            }
            checkBoxAdapter.setCheckBoxItems(items);
        }
    }

    private void setData(){
        DateToIntChartData chartData = new ChartToChartDataConverter().convert(fileData);
        chartView.setData(chartData);
        addCheckBoxes();
    }

    public static ChartFragment createInstance(TelegramFileData fileData){
        ChartFragment fragment = new ChartFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(CHART_DATA_KEY, fileData);
        fragment.setArguments(bundle);
        return fragment;
    }
}

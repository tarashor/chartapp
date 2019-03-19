package com.tarashor.chartapp;

import android.content.Context;

import com.tarashor.chartapp.models.Chart;
import com.tarashor.chartapp.models.Column;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChartJsonParser {
    private final static String JSON_FILE_NAME = "charts_data";

    private final static String COLUMNS_JSON_KEY = "columns";
    private final static String TYPES_JSON_KEY = "types";
    private final static String NAMES_JSON_KEY = "names";
    private final static String COLORS_JSON_KEY = "colors";

    public List<Chart> parseColumns(Context context){
        ArrayList<Chart> chartDataList = new ArrayList<>();
        JSONArray jsonArray = loadJSONFromAsset(context);

        if(jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    Chart chart = new Chart();
                    chart.setColumns(parseChartDataFromJsonObject(jsonArray.getJSONObject(i)));
                    chartDataList.add(chart);
                } catch (JSONException e) {
                    return chartDataList;
                }
            }
        }

        return chartDataList;
    }

    private JSONArray loadJSONFromAsset(Context context) {
        JSONArray jsonArray = new JSONArray();
        try {
            InputStream is = context.getAssets().open(JSON_FILE_NAME);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");
            jsonArray = new JSONArray(json);
        } catch (IOException ex) {
            return null;
        } catch (JSONException e) {

        }

        return jsonArray;
    }

    private List<Column> parseChartDataFromJsonObject(JSONObject jsonObject) throws JSONException{
        JSONArray columnsData = jsonObject.getJSONArray(COLUMNS_JSON_KEY);
        ArrayList<Column> columns = new ArrayList<>();

        for(int i = 0; i < columnsData.length(); i++){
            JSONArray columnsDigitData = columnsData.getJSONArray(i);
            Column column = new Column();
            column.setName(columnsDigitData.getString(0));
            ArrayList<Integer> columnValues = new ArrayList<>();
            for(int j = 1; j < columnsDigitData.length(); j++){
                columnValues.add(columnsDigitData.getInt(j));
            }
            column.setColumnsData(columnValues);
            column.setType(jsonObject.getJSONObject(TYPES_JSON_KEY).optString(column.getName(), ""));
            column.setVisibleName(jsonObject.getJSONObject(NAMES_JSON_KEY).optString(column.getName(), ""));
            column.setColor(jsonObject.getJSONObject(COLORS_JSON_KEY).optString(column.getName(), ""));
            columns.add(column);
        }

        return columns;
    }

    private HashMap<String, ArrayList<Column>> parseColumns(JSONArray jsonArray) throws JSONException {
        HashMap<String, ArrayList<Column>> columns = new HashMap<>();

        for(int i = 0; i < jsonArray.length(); i++){
            String columnName = jsonArray.getString(0);

            JSONArray columnData = jsonArray.getJSONArray(i);
            ArrayList<Integer> digitData = new ArrayList<>();
            for(int j = 1; j < columnData.length(); j++){
                digitData.add(columnData.getInt(j));
            }
        }

        return columns;
    }
}

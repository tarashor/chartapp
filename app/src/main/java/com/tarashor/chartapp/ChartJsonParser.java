package com.tarashor.chartapp;

import android.content.Context;

import com.tarashor.chartapp.models.TelegramFileData;
import com.tarashor.chartapp.models.Column;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ChartJsonParser {
    private final static String JSON_FILE_NAME = "charts_data.json";

    private final static String COLUMNS_JSON_KEY = "columns";
    private final static String TYPES_JSON_KEY = "types";
    private final static String NAMES_JSON_KEY = "names";
    private final static String COLORS_JSON_KEY = "colors";

    private final static String X_TYPE = "x";
    private final static String Y_TYPE = "line";

    public List<TelegramFileData> parseColumns(Context context){
        ArrayList<TelegramFileData> telegramFileDataDataList = new ArrayList<>();
        JSONArray jsonArray = loadJSONFromAsset(context);

        if(jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {

                    telegramFileDataDataList.add(parseChartDataFromJsonObject(jsonArray.getJSONObject(i)));
                } catch (JSONException e) {
                    return telegramFileDataDataList;
                }
            }
        }

        return telegramFileDataDataList;
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


    private TelegramFileData parseChartDataFromJsonObject(JSONObject jsonObject) throws JSONException{
        TelegramFileData telegramFileData = new TelegramFileData();

        Column<Long> xcolumn = new Column<>();
        List<Column<Integer>> ycolumns = new ArrayList<>();

        JSONArray columnsData = jsonObject.getJSONArray(COLUMNS_JSON_KEY);
        ArrayList<Column> columns = new ArrayList<>();

        for(int i = 0; i < columnsData.length(); i++){
            JSONArray columnsDigitData = columnsData.getJSONArray(i);
            String id = columnsDigitData.getString(0);
            String type = jsonObject.getJSONObject(TYPES_JSON_KEY).optString(id, "");
            if (type.equals(X_TYPE)) {
                xcolumn.setName(id);
                xcolumn.setType(type);
                Long[] columnValues = new Long[columnsDigitData.length() - 1];
                for (int j = 1; j < columnsDigitData.length(); j++) {
                    columnValues[j - 1] = columnsDigitData.getLong(j);
                }
                xcolumn.setColumnsData(columnValues);
            } else {
                Column<Integer> column = new Column<>();
                column.setName(id);
                Integer[] columnValues = new Integer[columnsDigitData.length() - 1];
                for (int j = 1; j < columnsDigitData.length(); j++) {
                    columnValues[j - 1] = columnsDigitData.getInt(j);
                }
                column.setColumnsData(columnValues);

                column.setVisibleName(jsonObject.getJSONObject(NAMES_JSON_KEY).optString(column.getName(), ""));
                column.setColor(jsonObject.getJSONObject(COLORS_JSON_KEY).optString(column.getName(), ""));
                ycolumns.add(column);
            }
        }

        telegramFileData.setXColumn(xcolumn);
        telegramFileData.setYColumns(ycolumns);

        return telegramFileData;
    }
}

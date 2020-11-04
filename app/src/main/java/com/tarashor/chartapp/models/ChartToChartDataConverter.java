package com.tarashor.chartapp.models;

import com.tarashor.chartlib.data.DateToIntChartData;
import com.tarashor.chartlib.data.Line;

import java.util.Calendar;
import java.util.Date;

public class ChartToChartDataConverter {
    public DateToIntChartData convert(TelegramFileData telegramFileData) {
        Date[] x = new Date[telegramFileData.getXColumn().getColumnsData().length];
        for (int i = 0; i < telegramFileData.getXColumn().getColumnsData().length; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(telegramFileData.getXColumn().getColumnsData()[i]);
            x[i] = calendar.getTime();
        }

        Line<Integer>[] lines = new Line[telegramFileData.getYColumns().size()];

        for (int i = 0; i < telegramFileData.getYColumns().size(); i++) {
            lines[i] = new Line<Integer>(telegramFileData.getYColumns().get(i).getName(), telegramFileData.getYColumns().get(i).getColumnsData(), telegramFileData.getYColumns().get(i).getColor(),
                    telegramFileData.getYColumns().get(i).isEnabled());
        }

        return new DateToIntChartData(x, lines);
    }
}

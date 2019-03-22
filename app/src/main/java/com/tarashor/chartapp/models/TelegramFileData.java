package com.tarashor.chartapp.models;

import java.io.Serializable;
import java.util.List;

public class TelegramFileData implements Serializable {
    private List<Column<Integer>> yColumns;
    private Column<Long> xColumn;

    public Column<Long> getXColumn(){
        return xColumn;
    }

    public List<Column<Integer>> getYColumns() {
        return yColumns;
    }

    public void setYColumns(List<Column<Integer>> columns) {
        this.yColumns = columns;
    }

    public void setXColumn(Column<Long> column){
        xColumn = column;
    }
}

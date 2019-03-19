package com.tarashor.chartapp.models;

import com.tarashor.chartlib.Line;

import java.util.List;

public class Chart {
    private List<Column> columns;

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    public Column getX(){
        return columns.get(0);
    }

    public Column getY(int index){
        return columns.get(index);
    }
}

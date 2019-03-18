package com.tarashor.chartapp.models;

import java.util.ArrayList;

public class Column {
    private String name;
    private ArrayList<Integer> columnsData;
    private String type;
    private String visibleName;
    private String color;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Integer> getColumnsData() {
        return columnsData;
    }

    public void setColumnsData(ArrayList<Integer> columnsData) {
        this.columnsData = columnsData;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVisibleName() {
        return visibleName;
    }

    public void setVisibleName(String visibleName) {
        this.visibleName = visibleName;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}

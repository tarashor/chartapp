package com.tarashor.chartapp.models;

import java.util.ArrayList;

public class Column<T> {
    private String name;
    private T[] columnsData;
    private String type;
    private String visibleName;
    private String color;
    private boolean enabled = true;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public T[] getColumnsData() {
        return columnsData;
    }

    public void setColumnsData(T[] columnsData) {
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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

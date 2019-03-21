package com.tarashor.chartapp;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.tarashor.chartlib.data.Line;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;

public class CheckBoxAdapter extends ArrayAdapter<CheckBoxItem> {
    private List<CheckBoxItem> checkBoxItems;
    private CheckedChange onCheckedChangeListener;

    public CheckBoxAdapter(@NonNull Context context, @NonNull List<CheckBoxItem> objects) {
        super(context, R.layout.check_box_item, objects);
        this.checkBoxItems = objects;
    }

    @Override
    public int getCount() {
        return checkBoxItems.size();
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.check_box_item, parent, false);
            viewHolder.checkBox = convertView.findViewById(R.id.check_box);
            viewHolder.textView = convertView.findViewById(R.id.check_box_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.checkBox.setSupportButtonTintList(createColorStateList(checkBoxItems.get(position).getColor()));
        viewHolder.textView.setText(checkBoxItems.get(position).getName());
        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(onCheckedChangeListener != null){
                    onCheckedChangeListener.onChange(isChecked, position);
                }
            }
        });
        viewHolder.checkBox.setChecked(checkBoxItems.get(position).isEnabled());

        return convertView;
    }


    private class ViewHolder {
        AppCompatCheckBox checkBox;
        TextView textView;
    }

    private ColorStateList createColorStateList(String color) {
        return new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_enabled} //enabled
                },
                new int[]{Color.parseColor(color)}
        );

    }

    public interface CheckedChange {
        void onChange(boolean checked, int pos);
    }

    public void setOnCheckedChangeListener(CheckedChange onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    public void setCheckBoxItems(List<CheckBoxItem> checkBoxItems){
        this.checkBoxItems = checkBoxItems;
        notifyDataSetChanged();
    }
}

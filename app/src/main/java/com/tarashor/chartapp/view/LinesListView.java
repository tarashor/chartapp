package com.tarashor.chartapp.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tarashor.chartapp.R;
import com.tarashor.chartlib.data.DateToIntChartData;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatCheckBox;

public class LinesListView  extends LinearLayout {
    private SparseArray<LineState> lines = new SparseArray<>();
    private SparseArray<CheckViewHolder> views = new SparseArray<>();
    private CheckedChange onCheckedChangeListener;

    public LinesListView(Context context) {
        super(context);
        init(context);
    }

    public LinesListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LinesListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LinesListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }


    private void init(Context context) {

    }

    public void setData(DateToIntChartData data) {
        lines.clear();

        for (int i = 0; i < data.getLinesCount(); i++) {
            LineState lineState = new LineState(data.getColor(i), data.getLineName(i), true);
            lines.put(i, lineState);
        }

        views.clear();
        removeAllViews();
        for (int i = 0; i < lines.size(); i++) {
            int key = lines.keyAt(i);
            LineState lineState = lines.get(key);
            inflate(getContext(), R.layout.check_box_item, this);
            //addView(itemView);
            CheckViewHolder checkViewHolder = new CheckViewHolder(getChildAt(i), key);
            checkViewHolder.setLineState(lineState);
            views.put(key, checkViewHolder);
        }



    }

    private class CheckViewHolder {
        AppCompatCheckBox checkBox;
        TextView textView;
        int key;


        public CheckViewHolder(View itemView, final int key) {
            checkBox = itemView.findViewById(R.id.check_box);
            textView = itemView.findViewById(R.id.check_box_name);
            this.key = key;

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    onCheckBoxChecked(CheckViewHolder.this.key, isChecked);

                }
            });
        }

        public void setLineState(LineState lineState) {
            checkBox.setSupportButtonTintList(createColorStateList(lineState.color));
            textView.setText(lineState.name);
            checkBox.setChecked(lineState.checked);
        }
    }

    private void onCheckBoxChecked(int key, boolean isChecked) {
        LineState lineState = lines.get(key);
        if (lineState != null) {
            lineState.setChecked(isChecked);
            if (onCheckedChangeListener != null) {
                onCheckedChangeListener.onChange(isChecked, lineState.name);
            }
        }
    }


    private ColorStateList createColorStateList(int color) {
        return new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_enabled} //enabled
                },
                new int[]{color}
        );

    }

    public interface CheckedChange {
        void onChange(boolean checked, String name);

    }

    public void setOnCheckedChangeListener(CheckedChange onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    private static class LineState{
        int color;
        String name;
        boolean checked;

        public LineState(int color, String name, boolean checked) {
            this.color = color;
            this.name = name;
            this.checked = checked;
        }

        public boolean isChecked() {
            return checked;
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
        }

        public int getColor() {
            return color;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj instanceof LineState){
                LineState other = (LineState) obj;
                return name.equals(other.name);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }
    }

}
package com.tarashor.chartlib.chart;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

class Utils {
    static float convertDpToPixel(Context context, float dp) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * metrics.density;
    }

}

package com.tarashor.chartlib;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class Utils {
    public static float convertDpToPixel(Context context, float dp) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * metrics.density;
    }

}

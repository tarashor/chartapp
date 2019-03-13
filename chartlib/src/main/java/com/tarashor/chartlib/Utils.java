package com.tarashor.chartlib;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

class Utils {
    public static float convertDpToPixel(Context context, float dp) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * metrics.density;
    }

//    public static int convertDpToPixel(Context context, int dp) {
//        Resources resources = context.getResources();
//        DisplayMetrics metrics = resources.getDisplayMetrics();
//        return (int) (dp * metrics.density);
//    }
}

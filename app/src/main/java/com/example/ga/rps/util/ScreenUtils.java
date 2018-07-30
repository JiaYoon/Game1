package com.example.ga.rps.util;

import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * Created by GA on 2018. 4. 14..
 */

public class ScreenUtils {
    public static int dpToPx(float dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

    public static DisplayMetrics getScreenSize() {
        return Resources.getSystem().getDisplayMetrics();
    }

    public static float pxToDp(float px) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return px / (metrics.densityDpi / 160f);
    }
}
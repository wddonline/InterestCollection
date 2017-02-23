package org.wdd.app.android.interestcollection.utils;

import java.math.RoundingMode;
import java.text.NumberFormat;

/**
 * Created by richard on 2/15/17.
 */

public class MathUtils {

    public static String formatDouble(double d) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        // 保留两位小数
        nf.setMaximumFractionDigits(2);
        // 如果不需要四舍五入，可以使用RoundingMode.DOWN
        nf.setRoundingMode(RoundingMode.UP);
        return nf.format(d);
    }

}

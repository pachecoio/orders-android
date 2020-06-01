package io.pacheco.orders.helpers;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.util.Locale;

public class Utils {
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static String getFormattedCurrency(double value) {
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.CANADA);
        return "R" + format.format(value).replaceAll("[\\.]", ",");
    }
}

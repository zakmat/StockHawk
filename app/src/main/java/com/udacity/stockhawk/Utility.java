package com.udacity.stockhawk;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by mateusz.zak on 21.05.2017.
 */

public class Utility {

    private static final DecimalFormat dollarFormatWithPlus;
    private static final DecimalFormat dollarFormat;
    private static final DecimalFormat percentageFormat;

    static {
        dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus.setPositivePrefix("+ $");
        dollarFormatWithPlus.setNegativePrefix("- $");
        percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
        percentageFormat.setMaximumFractionDigits(2);
        percentageFormat.setMinimumFractionDigits(2);
        percentageFormat.setPositivePrefix("+");
    }

    public static String formatPercentage(double number) {
        return percentageFormat.format(number);
    }

    public static String formatDollar(double number) {
        return dollarFormat.format(number);
    }

    public static String formatDollarWithPlus(double number) {
        return dollarFormatWithPlus.format(number);
    }
}

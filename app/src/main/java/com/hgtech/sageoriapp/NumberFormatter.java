package com.hgtech.sageoriapp;

import java.text.NumberFormat;
import java.util.Locale;

public class NumberFormatter {

    static public String getNumberString(int number){
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.KOREA);
        return numberFormat.format(number);
    }
}

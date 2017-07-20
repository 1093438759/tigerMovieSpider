package dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ty on 2017/6/23.
 */
public class DataSwitch {
    /**
     * 传入日期字符串 返回一个Date格式
     *
     * @param date 字符串类型的日期2016-09-30   或者20150119
     * @return Date
     */
    public static Date getDate(String date) {
        SimpleDateFormat sdf;
        if (date.length() == 10) {
            sdf = new SimpleDateFormat("yyyy-MM-dd");
        } else if (date.length() == 8) {
            sdf = new SimpleDateFormat("yyyyMMdd");
        } else {
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
        try {
            Date dateTime = sdf.parse(date);
            return dateTime;
        } catch (ParseException e) {
            System.out.println("日期转换错误");
        }
        return null;
    }

    /**
     * 对字符串的数字进行转换
     *
     * @param stringNumber 有可能是 xx亿 xx万 xxxx
     * @return long
     */
    public static long toNum(String stringNumber) {
        if (stringNumber.indexOf("亿") != -1) {
            double num = Double.parseDouble(stringNumber.substring(0, stringNumber.length() - 1));
            return (long) num * 100000000;
        }
        if (stringNumber.indexOf("万") != -1) {
            double num = Double.parseDouble(stringNumber.substring(0, stringNumber.length() - 1));
            return (long) num * 10000;
        }
        if (stringNumber.indexOf("W") != -1) {
            double num = Double.parseDouble(stringNumber.substring(0, stringNumber.length() - 1));
            return (long) num * 10000;
        }
        return Long.parseLong(stringNumber);
    }
}

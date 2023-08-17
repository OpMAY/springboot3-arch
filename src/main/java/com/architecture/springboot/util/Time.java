package com.architecture.springboot.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Time {
    public static String TimeFormatHMS() {
        Date now = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return dateFormat.format(now);
    }

    public static String TimeFormatNoSpecialCharacter() {
        Date now = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
        return dateFormat.format(now);
    }

    public static String TimeFormatter(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }

    public static String TimeFormatterDay(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    public static String TimeFormatDay() {
        Date now = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(now);
    }

    public static String MsToSecond(String date){
        if(date.lastIndexOf(".") < 0)
            return date;
        else
            return date.substring(0, date.lastIndexOf("."));
    }

    public static Date StringToDateTimeFormat(String dateString) throws ParseException {
        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return transFormat.parse(dateString);
    }

    public static Date StringToDateFormat(String dateString) throws ParseException{
        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd");
        return transFormat.parse(dateString);
    }

    public static Date LongTimeStamp(int year, int month, int date, int hour, int minute, int second) {
        Date currentDate = new Date();
        System.out.println(currentDate);
        // convert date to calendar
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
        // manipulate date
        c.add(Calendar.YEAR, year);
        c.add(Calendar.MONTH, month);
        c.add(Calendar.DATE, date); //same with c.add(Calendar.DAY_OF_MONTH, 1);
        c.add(Calendar.HOUR, hour);
        c.add(Calendar.MINUTE, minute);
        c.add(Calendar.SECOND, second);
        // convert calendar to date
        Date currentDatePlusOne = c.getTime();
        return currentDatePlusOne;
    }
}

/*
 * Copyright (c) 2020 ProDev+ (Pascal Gerner).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.simplelib.time;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class Time {
    private TimeZone timeZone;

    private int second, minute, hour;
    private int day, month, year;

    public Time() {
        setToNow();
    }

    public Time(Time time) {
        setToMillis(time.getAsMillis());
    }

    public Time(long millis) {
        setToMillis(millis);
    }

    public Time(int minute, int hour) {
        setToNow();

        this.minute = minute;
        this.hour = hour;
    }

    public Time(int second, int minute, int hour) {
        setToNow();

        this.second = second;
        this.minute = minute;
        this.hour = hour;
    }

    public Time(int second, int minute, int hour, int day, int month, int year) {
        setTo(second, minute, hour, day, month, year);
    }

    public static Time create() {
        return new Time();
    }

    public static Time create(Time time) {
        return new Time(time);
    }

    public static Time create(long millis) {
        return new Time(millis);
    }

    public static Time create(int minute, int hour) {
        return new Time(minute, hour);
    }

    public static Time create(int second, int minute, int hour) {
        return new Time(second, minute, hour);
    }

    public static Time create(int second, int minute, int hour, int day, int month, int year) {
        return new Time(second, minute, hour, day, month, year);
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setTimeZone(String timeZoneId) {
        setTimeZone(TimeZone.getTimeZone(timeZoneId));
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    public String asString() {
        return asString(false);
    }

    public String asString(boolean includeDate) {
        DecimalFormat df = new DecimalFormat("00");

        String secondText = df.format(getSecond());
        String minuteText = df.format(getMinute());
        String hourText = df.format(getHour());

        String dayText = df.format(getDay());
        String monthText = df.format(getMonth());
        String yearText = df.format(getYear());

        String text = hourText + ":" + minuteText + ":" + secondText;
        if (includeDate) {
            text += ", " + dayText;
            text += "/" + monthText;
            text += "/" + yearText;
        }

        return text;
    }

    public void setTo(int second, int minute, int hour, int day, int month, int year) {
        this.second = second;
        this.minute = minute;
        this.hour = hour;

        this.day = day;
        this.month = month;
        this.year = year;
    }

    public void setToNow() {
        setToMillis(System.currentTimeMillis());
    }

    public void setToMillis(long millis) {
        Calendar calendar = Calendar.getInstance();
        if (timeZone != null) calendar.setTimeZone(timeZone);
        calendar.setTimeInMillis(millis);

        this.second = calendar.get(Calendar.SECOND);
        this.minute = calendar.get(Calendar.MINUTE);
        this.hour = calendar.get(Calendar.HOUR_OF_DAY);

        this.day = calendar.get(Calendar.DAY_OF_MONTH);
        this.month = calendar.get(Calendar.MONTH);
        this.year = calendar.get(Calendar.YEAR);
    }

    public long getAsMillis() {
        Calendar calendar = Calendar.getInstance();
        if (timeZone != null) calendar.setTimeZone(timeZone);

        calendar.set(Calendar.SECOND, getSecond());
        calendar.set(Calendar.MINUTE, getMinute());
        calendar.set(Calendar.HOUR_OF_DAY, getHour());

        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);

        return calendar.getTimeInMillis();
    }

    public Time add(long millis) {
        return new Time(getAsMillis() + (millis >= 0 ? millis : -millis));
    }

    public Time remove(long millis) {
        return new Time(getAsMillis() - (millis >= 0 ? millis : -millis));
    }
}
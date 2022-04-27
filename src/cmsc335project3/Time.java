/*
 * Filename: Time.java
 * Author: Student Name
 * Date: 23/04/2022
 * Purpose: Create runnable Time class to get current time and update timeText text-field in GUI
 */

import java.text.SimpleDateFormat;
import java.util.Date;

public class Time implements Runnable {
    private final boolean isRunning;
    private final String timePattern = "hh:mm:ss a";
    private final SimpleDateFormat timeFormat = new SimpleDateFormat(timePattern);
    Date date = new Date(System.currentTimeMillis());

    public Time() {
        this.isRunning = Thread.currentThread().isAlive();
    }
    
    public String getTime() {
        date = new Date(System.currentTimeMillis());
        return timeFormat.format(date);
    }

    @Override
    public void run() {
        //While running, constantly update current time
        while (isRunning) {
            CMSC335Project3.timeText.setText(getTime());
        } 
    }
    
}

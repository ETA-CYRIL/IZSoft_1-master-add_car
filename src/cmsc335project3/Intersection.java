/*
 * Filename: Intersection.java
 * Author: Student Name
 * Date: 23/04/2022
 * Purpose: Runnable Intersection class to display traffic light colors
 */
import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Intersection implements Runnable {
    //Array of colors to cycle through
    private int i = 0;
    private String currentLight =  TrafficLightColor.GREEN.getName();
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    public final AtomicBoolean suspended = new AtomicBoolean(false);
    Thread thread;
    String threadName;
    //JLabel to print to
    private final JLabel j;
    
    public Intersection(String name, JLabel j) {
        this.threadName = name;
        this.j = j;
        System.out.println("Creating " + threadName);
    }
    //Synchronized method for getting traffic light color
    public synchronized String getColor() {
        this.currentLight = TrafficLightColor.values()[i].getName();
        return this.currentLight;
    }
    
    public void suspend() {
        suspended.set(true);
        System.out.println("Suspending " + threadName);
    }
    
    public synchronized void resume() {
        suspended.set(false);
        notify();
        System.out.println("Resuming " + threadName);
    }
    
    public void start() {
        System.out.println("Starting " + threadName);
        if(thread == null) {
            thread = new Thread(this, threadName);
            thread.start();
        }
    }
    
    public void stop() {
        thread.interrupt();
        isRunning.set(false);
        thread = null;
        System.out.println("Stopping " + threadName);
    }
    
    public void interrupt() {
        //If light is sleeping, we can call interrupt to wake it when hitting "Pause" button
        thread.interrupt();
    }

    
    @Override
    public void run() {
        System.out.println("Running " + threadName);
        isRunning.set(true);
        while(isRunning.get()) {
            try {
                synchronized(this) {
                        while(suspended.get()) {
                            System.out.println(threadName + " waiting");
                            wait();
                        }
                    }
                switch (getColor()) {
                    case "Green":
                        j.setForeground(new Color(0,200,10)); //Set font color to green
                        j.setText(getColor());
                        //Stay green for 10 seconds
                        Thread.sleep(10000);
                        i++;
                        
                        break;
                    case "Yellow":
                        j.setForeground(new Color(247, 226, 35)); //Font color yellow
                        j.setText(getColor());
                        //Yellow for 5 seconds
                        Thread.sleep(5000);
                        i++;
                        break;
                    case "Red":
                        j.setForeground(Color.RED); //Font color red
                        j.setText(getColor());
                        //Red for 15 seconds
                        Thread.sleep(15000);
                        //Set i back to 0
                        i = 0;
                        break;
                    default:
                        break;
                }
                
            } catch (InterruptedException ex) {
                //If thread gets interrupted, set suspended true
                suspended.set(true);
            }
        }
    }
    
}

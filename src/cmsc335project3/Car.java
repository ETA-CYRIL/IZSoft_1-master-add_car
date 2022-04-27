/*
 * Filename: Car.java
 * Author: Student Name
 * Date: 23/04/2022
 * Purpose: Runnable Car class to increment xPosition and display speed.
 */

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;


public class Car implements Runnable {
    private int xPosition;
    private final int yPosition = 0;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    //Make separate booleans for atLight and suspended (pause button)
    public final AtomicBoolean atLight = new AtomicBoolean(false);
    public final AtomicBoolean suspended = new AtomicBoolean(false);
    private final String threadName;
    public Thread thread;
    private int speed;
    
    //Constructor for name, max and min: Max and min for range of initial xPosition for car
    public Car(String name, int max, int min) {
        this.threadName = name;
        this.xPosition = ThreadLocalRandom.current().nextInt(min, max);
        System.out.println("Creating " + threadName);
    }

    public String getThreadName() {
        return threadName;
    }

    public synchronized int getPosition() {
        return xPosition;
    }
    
    public int getSpeed() {
        if(isRunning.get()) {
            if(atLight.get()) 
                speed = 0;
            else
                speed = 2*60;
        } else 
            speed = 0;
        return speed;
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
    
    public void suspend() {
        suspended.set(true);
        System.out.println("Suspending " + threadName);
    }
    
    public synchronized void resume() {
        //If car is suspended, set suspended to false and notify
        if(suspended.get() || atLight.get()) {
            suspended.set(false);
            atLight.set(false);
            notify();
            System.out.println("Resuming " + threadName);
        }
    }
    
    @Override
    public void run() {
        System.out.println("Running " + threadName);
        isRunning.set(true);
        while(isRunning.get()) {
            try {
                
                while(xPosition < 3000) {
                    synchronized(this) {
                        while(suspended.get() || atLight.get()) {
                            System.out.println(threadName + " waiting");
                            wait(); 
                        }
                    }
                    //Check if still running
                    if(isRunning.get()) {
                        Thread.sleep(100);
                        xPosition+=5;
                    }
                }
                xPosition = 0;     
            } catch (InterruptedException ex) {
                return;
            }
        }
    }
    
}

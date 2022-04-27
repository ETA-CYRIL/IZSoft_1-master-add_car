/*
 * Filename: CMSC335Project3.java
 * Author: Student Name
 * Date: 23/04/2022
 * Purpose: As a new engineer for a traffic congestion mitigation company, you have been tasked with developing a
 *
Java Swing GUI that displays time, traffic signals and other information for traffic analysts. The final GUI
design is up to you but should include viewing ports/panels to display the following components of the
simulation:
1. Current time stamps in 1 second intervals
2. Real-time Traffic light display for three major intersections
3. X, Y positions and speed of up to 3 cars as they traverse each of the 3 intersections
Some of the details of the simulation are up to you but the following guidelines will set the guardrails:
1. The components listed above should run in separate threads.
2. Loop through the simulation with button(s) providing the ability to start, pause, stop and
continue the simulation.
3. You will need to use basic distance formulas such as distance = Speed * time. Be sure to be
consistent and define your units of measure (e.g. mile/hour, versus km/hour)
4. Assume a straight distance between each traffic light of 1000 meters.
5. Since you are traveling a straight line, you can assume Y = 0 for your X,Y positions.
6. Provide the ability to add more cars and intersections to the simulation through the GUI.
7. Don't worry about physics. Assume cars will stop on a dime for red lights, and continue through
yellow lights and green lights.
8. Document all assumptions and limitations of your simulation.
 */

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

public class CMSC335Project3 extends JFrame implements Runnable, ChangeListener {
    
    static JLabel timeText = new JLabel();
    static JLabel trafficA = new JLabel();
    static JLabel trafficB = new JLabel();
    static JLabel trafficC = new JLabel();
    //JButtons to start, pause, and stop
    private final JButton start = new JButton("Start");
    private final JButton pause = new JButton("Pause");
    private final JButton stop = new JButton("Stop");
    private final JButton addCar = new JButton("Add a Car");
    private final JButton addIntersection = new JButton("Add an Intersection");
    JButton newCarBtn;
    JTextField carName;
    ArrayList<Car> carArray;
    ArrayList<JSlider> mySlider;
    ArrayList<Object[]> trafficData1;
    //JSliders for showing car progress
    static JSlider carSlider1 = new JSlider(0, 3000);
    static JSlider carSlider2 = new JSlider(0, 3000);
    static JSlider carSlider3 = new JSlider(0, 3000);
    static JSlider carSlider4 = new JSlider(0, 3000);
    static JSlider CarSlidern;

    //Dialog For Additions
    private static JDialog addCarPop = new JDialog();
    private static JDialog addIntersectionPop = new JDialog();
    
    private static boolean isRunning;
    private static final AtomicBoolean simIsRunning = new AtomicBoolean(false);

    //Create 3 runnable intersection objects, each on their own thread
    Intersection A = new Intersection("aThread", trafficA);
    Intersection B = new Intersection("bThread", trafficB);
    Intersection C = new Intersection("cThread", trafficC);
    //Create 4 runnable Car objects and a thread for each one
    Car car1 = new Car("Tesla", 300, 0);
    Car car2 = new Car("Toyota", 1000, 0);
    Car car3 = new Car("Mercedes", 1800, 1000);
    Car car4 = new Car("Audi", 2000, 1000);
    //Array of cars to loop through later

    {
        carArray = new ArrayList<>();
        carArray.add(car1);
        carArray.add(car2);
        carArray.add(car3);
        carArray.add(car4);
    }
    {
        mySlider = new ArrayList<>();
        mySlider.add( carSlider1 = new JSlider(0, 3000));
        mySlider.add( carSlider2 = new JSlider(0, 3000));
        mySlider.add( carSlider3 = new JSlider(0, 3000));
        mySlider.add( carSlider4 = new JSlider(0, 3000));
    }


    Intersection[] intersectionArray = {A, B, C};
    JSlider[] sliders = {carSlider1, carSlider2, carSlider3, carSlider4};
    static Thread gui;



    Object[][] trafficData = {
            {car1.getThreadName(), car1.getPosition(), 0, 0},
            {car2.getThreadName(), car2.getPosition(), 0, 0},
            {car3.getThreadName(), car3.getPosition(), 0, 0},
            {car4.getThreadName(), car4.getPosition(), 0, 0}
    };

    {
        trafficData1 = new ArrayList<>();
        trafficData1.add(new Object[] {car1.getThreadName(), car1.getPosition(), 0, 0});
        trafficData1.add(new Object[] {car2.getThreadName(), car2.getPosition(), 0, 0});
        trafficData1.add(new Object[] {car3.getThreadName(), car3.getPosition(), 0, 0});
        trafficData1.add(new Object[] {car4.getThreadName(), car4.getPosition(), 0, 0});

    };


    //Table for displaying data
    String[] columnNames = {"Cars", "X-Pos", "Y-Pos", "Speed km/h"};
    JTable dataTable = new JTable(trafficData, columnNames);


    public CMSC335Project3() {
        super("My CMSC 335 Project 3: Traffic Tracker Control");
        isRunning = Thread.currentThread().isAlive();
        buildGUI();
        setButtons();
    }
    
    private void display() {
        setSize(600,400);
        setVisible(true);
        //Centers the frame on the screen
        setLocationRelativeTo(null);
        //Sets the window to be closeable
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    private void buildGUI() {

        JLabel welcome = new JLabel("Traffic Tracker Simulator!");
        JLabel welcome2 = new JLabel("Press the Start button to begin simulation");
        
        JLabel time = new JLabel("Current time: ");
        JLabel trafficLightA = new JLabel("Intersection a: ");
        JLabel trafficLightB = new JLabel("Intersection b: ");
        JLabel trafficLightC = new JLabel("Intersection c: ");
        
        //Add changeListeners to car sliders
        for (JSlider j: mySlider) {
            j.addChangeListener(this);
        }
//        carSlider1.addChangeListener(this);
//        carSlider2.addChangeListener(this);
//        carSlider3.addChangeListener(this);
//        carSlider4.addChangeListener(this);

        for (int i = 0; i < mySlider.size(); i++) {
            sliders[i].setValue(carArray.get(i).getPosition());
        }
        /*carSlider1.setValue(car1.getPosition());
        carSlider2.setValue(car2.getPosition());
        carSlider3.setValue(car3.getPosition());
        carSlider4.setValue(car4.getPosition());*/

        for (int i = 0; i < 2; i++) {
            sliders[i].setMajorTickSpacing(1000);
            sliders[i].setPaintTicks(true);
        }
        /*carSlider1.setMajorTickSpacing(1000);
        carSlider1.setPaintTicks(true);
        
        carSlider2.setMajorTickSpacing(1000);
        carSlider2.setPaintTicks(true);*/
    
        dataTable.setPreferredScrollableViewportSize(new Dimension(400, 70));
        dataTable.setFillsViewportHeight(true);
        
        JPanel dataPanel = new JPanel();

        //Create the scroll pane and add the table to it
        JScrollPane scrollPane = new JScrollPane(dataTable);
        dataPanel.add(scrollPane);

        //Dialog
        JFrame adataPanel = new JFrame();
        carName = new JTextField("Car Name",10);
        JTextField minName = new JTextField("X Position",10);
        newCarBtn = new JButton("Add");

        addCarPop = new JDialog(adataPanel , "New Car", true);
        addCarPop.add(carName);
        addCarPop.add(minName);
        addCarPop.add(newCarBtn);

        addCarPop.setLayout( new FlowLayout());
        addCarPop.setLocationRelativeTo(null);
        addCarPop.setSize(300,100);


        addIntersectionPop = new JDialog(this , "New Intersection", true);
        addIntersectionPop.setLocationRelativeTo(null);
        addIntersectionPop.setSize(300,300);
        addIntersectionPop.setLayout( new FlowLayout());


        //GUI Layout
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        
        layout.setHorizontalGroup(layout.createSequentialGroup()
            .addContainerGap(30, 30) //Container gap on left side     
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)    
                .addComponent(welcome)
                .addComponent(welcome2)    
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(time)
                    .addComponent(timeText)))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)    
                .addGroup(layout.createSequentialGroup()    
                    .addComponent(start)
                    .addComponent(pause)
                    .addComponent(stop)
                    .addComponent(addCar)
                    .addComponent(addIntersection)))
                    .addComponent(carSlider1)
                    .addComponent(carSlider2)
                    .addComponent(carSlider3)
                    .addComponent(carSlider4)
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)   
                .addGroup(layout.createSequentialGroup()   
                    .addComponent(trafficLightA)
                    .addComponent(trafficA)
                        .addContainerGap(20, 20)
                    .addComponent(trafficLightB)
                    .addComponent(trafficB)
                        .addContainerGap(20, 20)
                    .addComponent(trafficLightC)
                    .addComponent(trafficC))
                    .addComponent(dataPanel)))
                        
            .addContainerGap(30, 30) //Container gap on right side
                
        );
        
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createSequentialGroup()
                    .addComponent(welcome)
                    .addComponent(welcome2))
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(time)
                    .addComponent(timeText))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(start)
                    .addComponent(pause)
                    .addComponent(stop)
                    .addComponent(addCar)
                    .addComponent(addIntersection))
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)    
                    .addComponent(carSlider1)
                    .addComponent(carSlider2)
                    .addComponent(carSlider3)
                    .addComponent(carSlider4))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(trafficLightA)
                    .addComponent(trafficA)
                    .addComponent(trafficLightB)
                    .addComponent(trafficB)
                    .addComponent(trafficLightC)
                    .addComponent(trafficC))
                .addComponent(dataPanel)

                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addGap(20, 20, 20))
                .addGap(20, 20, 20)
        );
        
        pack();
    }
    
    private void setButtons() {
        //Start car and intersection threads with start button
        start.addActionListener((ActionEvent e) -> {
            if(!simIsRunning.get()) {
                System.out.println(Thread.currentThread().getName() + " calling start");
                for(Car i: carArray) {
                    i.start();
                }
                for(Intersection i: intersectionArray) {
                    i.start();
                }
            }
            //Set simIsRunning to true
            simIsRunning.set(true);
        });
        
        pause.addActionListener((ActionEvent e) -> {
            if(simIsRunning.get()) {
                //Loop through cars and intersections to call suspend()
                for(Car i: carArray) {
                    i.suspend();
                    System.out.println(Thread.currentThread().getName() + " calling suspend");
                }
                for(Intersection i: intersectionArray) {
                    //Call interrupts for sleeping intersection threads
                    i.interrupt();
                    i.suspend();
                }
                
                pause.setText("Continue");
                simIsRunning.set(false);
            } else {
                for(Car i:carArray) {
                    if(i.suspended.get()) {
                        i.resume();
                        System.out.println(Thread.currentThread().getName() + " calling resume");
                    }
                }
                for(Intersection i: intersectionArray) {
                    i.resume();
                }
                pause.setText("Pause");
                simIsRunning.set(true);
            }
        });
        
        stop.addActionListener((ActionEvent e) -> {
            if(simIsRunning.get()) {
                System.out.println(Thread.currentThread().getName() + " calling stop");
                for(Car i: carArray) {
                    i.stop();
                }
                for(Intersection i: intersectionArray) {
                    i.stop();
                }
                simIsRunning.set(false);
            }
        });

        addCar.addActionListener((e) -> {
            addCarPop.setVisible(true);
            System.out.println(carName.getText());
        });
        //adding a new car with data from dialog box
        newCarBtn.addActionListener((e) -> {
            //new constructor with name and max , min postion
            Car newCar = new Car(carName.getText(), 2000, 1000);
            carArray.add(newCar);
            //setting new car slider
            mySlider.add( CarSlidern = new JSlider(0, 3000));
            //new car trafficData
            trafficData1.add(new Object[] {newCar.getThreadName(), newCar.getPosition(), 0, 0});

        });

        addIntersection.addActionListener((e) -> {
            addIntersectionPop.setVisible(true);
        });
    }
    
    @Override
    public void stateChanged(ChangeEvent e) {
        //When car sliders change, update data in table.

        for (int i = 0; i < mySlider.size(); i++) {
            trafficData1.get(i)[1] = sliders[i].getValue();
        }
        //Update speed
        for(int i=0; i<trafficData1.size(); i++){
            trafficData1.get(i)[3] = carArray.get(i).getSpeed() + " km/h";
        }
        //Update table
        dataTable.repaint();
    }
    
    private void getData() {
        if(simIsRunning.get()) {
        //Get colors for intersections, if Red check xPosition
        switch(A.getColor()) {
            case "Red":
                for(Car i: carArray) {
                    //If car xPosition is within 500 meters and light is red, set suspend to true for car to wait
                    if(i.getPosition()>800 && i.getPosition()<1000) {
                        i.atLight.set(true);
                    }
                }
                break;
            case "Green":
                for(Car i:carArray) {
                    if(i.atLight.get()) {
                        i.resume();
                    }
                }
                break;
        }
        
        switch(B.getColor()) {
            case "Red":
                for(Car i: carArray) {
                    //If car xPosition is within 500 meters and light is red, set suspend to true for car to wait
                    if(i.getPosition()>1800 && i.getPosition()<2000) {
                        i.atLight.set(true);
                    }
                }
                break;
            case "Green":
                for(Car i:carArray) {
                    if(i.atLight.get()) {
                        i.resume();
                    }
                }
                break;
        }
        
        switch(C.getColor()) {
            case "Red":
                for(Car i: carArray) {
                    //If car xPosition is within 500 meters and light is red, set suspend to true for car to wait
                    if(i.getPosition()>2800 && i.getPosition()<3000) {
                        i.atLight.set(true);
                    }
                }
                break;
            case "Green":
                for(Car i:carArray) {
                    if(i.atLight.get()) {
                        i.resume();
                    }
                }
                break;
            }
        }
    }
    
    @Override
    public void run() {
        while(isRunning) {
            //While running, if simulation is running, set car sliders to car xPosition and get data
            if(simIsRunning.get()) {
//                carSlider1.setValue(car1.getPosition());
//                carSlider2.setValue(car2.getPosition());
//                carSlider3.setValue(car3.getPosition());
//                carSlider4.setValue(car4.getPosition());
                for (int i = 0; i < mySlider.size(); i++) {
                    JSlider carSlider  = new JSlider();
                    Car  car = carArray.get(i);
                    carSlider = mySlider.get(i);
                    carSlider.setValue(car.getPosition());
                }
                getData();
            }
        }
    }
   
    public static void main(String[] args) {
        CMSC335Project3 test = new CMSC335Project3();
        test.display();
        gui = new Thread(test);
        gui.start();
        Thread time = new Thread(new Time());
        time.start();
    }
}

/*
**    KALC POS  - Professional Point of Sale
**
**    This file is part of KALC POS Version KALC V1.5.4
**
**    Copyright (c) 2015-2023 KALC & previous Openbravo POS related works   
**
**    https://www.KALC.co.uk
**   
**
*/


package ke.kalc.pos.util;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Action;
import javax.swing.Timer;

public class AutoRefresh implements ActionListener, AWTEventListener {

    private final static long KEY_EVENTS = AWTEvent.KEY_EVENT_MASK;
    private final static long MOUSE_EVENTS = AWTEvent.MOUSE_MOTION_EVENT_MASK
            + AWTEvent.MOUSE_EVENT_MASK;
    private final static long USER_EVENTS = KEY_EVENTS + MOUSE_EVENTS;

    private Action action;
    private final long eventMask;
    private Boolean running = false;
    private Timer LogoffTimer;

    private static AutoRefresh INSTANCE = new AutoRefresh();
    public static Boolean timer = false;

    // create a basic timer instance
    private AutoRefresh() {
        LogoffTimer = new Timer(5000, action);
        this.eventMask = 0;
        LogoffTimer.setInitialDelay(100);
    }

    public static AutoRefresh getInstance() {
        if (INSTANCE == null) {
            synchronized (AutoRefresh.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AutoRefresh();
                }
            }
        }
        return INSTANCE;
    }

    /*
     * 
     * Routines to control the timer
     * start() manually starts the timer
     * stop() manually stops the timer
     * restart() manually restart the timer
     * isRunning() returns the state of the timer
     * setTimer(Integer period, action ) set the interval rate of the timer and action event
     * 
     * 
     */
    public void start() {
        if (AutoRefresh.timer) {
            this.running = true;
            LogoffTimer.setRepeats(false);
            LogoffTimer.start();
            Toolkit.getDefaultToolkit().addAWTEventListener(this, eventMask);
        }
    }

    public void stop() {
        if (AutoRefresh.timer) {
            this.running = false;
            Toolkit.getDefaultToolkit().removeAWTEventListener(this);
            LogoffTimer.stop();
        }
    }

    // Implement ActionListener for the Timer
    @Override
    public void actionPerformed(ActionEvent e) {
        action.actionPerformed(e);
    }

    // Implement AWTEventListener, all events are dispatched via this
    @Override
    public void eventDispatched(AWTEvent e) { 
        if ((AutoRefresh.timer) && this.isTimerRunning()) {
            LogoffTimer.restart();
        }
    }

 

    // returns the timer state
    public boolean isTimerRunning() {
        if (AutoRefresh.timer) {
            return (this.running);
        } else {
            return false;
        }
    }

    // set the timer interval in seconds
    public void setTimer(Integer period, Action action) {
        System.out.println("set timer");
        AutoRefresh.timer = true;
        if (isTimerRunning()) {
            this.stop();
            LogoffTimer = new Timer(period, action);
            LogoffTimer.start();
        } else {
            LogoffTimer = new Timer(period, action);
            LogoffTimer.start();
        }
    }

    public void activateTimer() {
        AutoRefresh.timer = true; 
        this.running = true;
        this.start();
    }

    public void deactivateTimer() {
        this.stop();
        this.running = false;
        AutoRefresh.timer = false;
    }
}

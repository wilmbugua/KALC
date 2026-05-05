/*
**    KALC POS  - Professional Point of Sale
**
**    This file is part of KALC POS Version KALC V1.5.4
**
**    Copyright (c) 2015-2023 KALC & previous KALC POS related works   
**
**    https://www.kalc.co.ke
**   
**
*/


package ke.kalc.pos.util;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Refresh singleton for timer-based auto-refresh operations.
 */
public class Refresh {

    private static final Logger logger = Logger.getLogger(Refresh.class.getName());

    // Using volatile for double-checked locking
    private static volatile Refresh INSTANCE = null;

    private Timer refreshTimer;
    private Integer period = 10000;
    private Boolean running = false;

    private Refresh() {
        // Private constructor
    }

    /**
     * Get the singleton instance with double-checked locking.
     */
    public static Refresh getInstance() {
        if (INSTANCE == null) {
            synchronized (Refresh.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Refresh();
                }
            }
        }
        return INSTANCE;
    }

    public void pause() {
        if (running) {
            refreshTimer.cancel();
            running = false;
        }
    }

    public void stop() {
        if (running) {
            refreshTimer.cancel();
            running = false;
        }
    }

    public void start(TimerTask task) {
        if (running) stop();
        refreshTimer = new Timer();
        refreshTimer.scheduleAtFixedRate(task, 100, this.period);
        running = true;
    }

    public void setTimer(TimerTask task, Integer period) {
        if (running) stop();
        this.period = period;
        refreshTimer = new Timer();
        refreshTimer.scheduleAtFixedRate(task, 100, period);
        running = true;
    }

    public Boolean isRunning() {
        return this.running;
    }

    /**
     * Shutdown hook to ensure timer thread is cleaned up on JVM exit.
     */
    public void shutdown() {
        if (refreshTimer != null) {
            refreshTimer.cancel();
            refreshTimer.purge();
            running = false;
            logger.info("Refresh timer stopped and purged.");
        }
    }
}


package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Refresh manages timer-based refresh operations for the KALCPOS system.
 * Implements singleton pattern to ensure single instance across the application.
 * Supports scheduling periodic tasks and graceful cleanup.
 */
public class Refresh {
    private static final Logger logger = LoggerFactory.getLogger(Refresh.class);
    
    private static volatile Refresh INSTANCE;
    private Timer timer;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private long lastExecutionTime;
    private int executionCount;
    
    /**
     * Private constructor to prevent direct instantiation
     */
    private Refresh() {
        logger.info("Refresh singleton instance created");
        this.executionCount = 0;
        this.lastExecutionTime = 0;
    }
    
    /**
     * Get the singleton instance of Refresh (Double-Check Locking)
     * @return the singleton Refresh instance
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
    
    /**
     * Start a timer with the specified delay and period
     * @param delay the delay in milliseconds before first execution
     * @param period the period in milliseconds between successive executions
     */
    public void startTimer(long delay, long period) {
        if (isRunning.get()) {
            logger.warn("Timer is already running. Restarting with new parameters.");
            stopTimer();
        }
        
        timer = new Timer(true); // Set as daemon thread
        isRunning.set(true);
        executionCount = 0;
        lastExecutionTime = System.currentTimeMillis();
        
        logger.info("Starting timer: delay={}ms, period={}ms", delay, period);
        
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    executeRefreshTask();
                } catch (Exception e) {
                    logger.error("Error during refresh task execution: {}", e.getMessage(), e);
                }
            }
        }, delay, period);
    }
    
    /**
     * Start a timer with a custom TimerTask
     * @param task the TimerTask to execute
     * @param delay the delay in milliseconds before first execution
     * @param period the period in milliseconds between successive executions
     */
    public void startTimer(TimerTask task, long delay, long period) {
        if (isRunning.get()) {
            logger.warn("Timer is already running. Restarting with new task.");
            stopTimer();
        }
        
        timer = new Timer(true); // Set as daemon thread
        isRunning.set(true);
        executionCount = 0;
        lastExecutionTime = System.currentTimeMillis();
        
        logger.info("Starting custom timer task: delay={}ms, period={}ms", delay, period);
        
        timer.schedule(task, delay, period);
    }
    
    /**
     * Start a timer with a specific task for terminal data refresh
     * @param terminalLogic the TerminalDataLogic instance to refresh
     */
    public void startTerminalDataRefresh(TerminalDataLogic terminalLogic, long period) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    logger.debug("Refreshing terminal data for: {}", 
                        terminalLogic.getTerminalInfo());
                    
                    // Check if terminal is operational
                    if (!terminalLogic.isOperational()) {
                        logger.warn("Terminal is not operational, attempting recovery...");
                        // Attempt to update status or perform recovery
                    }
                    
                    // Get recent transactions
                    var transactions = terminalLogic.getRecentTransactions(10);
                    logger.info("Retrieved {} recent transactions", transactions.size());
                    
                } catch (Exception e) {
                    logger.error("Failed to refresh terminal data: {}", e.getMessage(), e);
                }
            }
        };
        
        startTimer(task, 0, period);
    }
    
    /**
     * Start a timer for product line data refresh
     * @param period the period in milliseconds between refreshes
     */
    public void startProductLineRefresh(long period) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    // Refresh product line data
                    var results = DbUtils.executeQuery("SELECT COUNT(*) as count FROM product_line");
                    if (!results.isEmpty()) {
                        long count = (Long) results.get(0).get("count");
                        logger.info("Product line count: {}", count);
                    }
                    
                    // Check table existence
                    boolean exists = DbUtils.tableExists("product_line");
                    if (!exists) {
                        logger.error("Product line table does not exist!");
                    }
                    
                } catch (Exception e) {
                    logger.error("Failed to refresh product line data: {}", e.getMessage(), e);
                }
            }
        };
        
        startTimer(task, 0, period);
    }
    
    /**
     * Execute the default refresh task
     */
    private void executeRefreshTask() {
        executionCount++;
        lastExecutionTime = System.currentTimeMillis();
        
        logger.debug("Refresh task executed. Count: {}, Time: {}", 
            executionCount, lastExecutionTime);
        
        // Perform refresh operations
        try {
            // Example: Check database connectivity
            if (SessionFactory.getInstance().testConnection()) {
                logger.trace("Database connection verified during refresh");
            } else {
                logger.warn("Database connection failed during refresh");
            }
        } catch (Exception e) {
            logger.error("Error during refresh: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Stop the timer and cleanup resources
     */
    public void stopTimer() {
        if (timer != null) {
            logger.info("Stopping timer...");
            timer.purge(); // Clean up cancelled tasks
            timer.cancel(); // Cancel the timer
            timer = null;
            isRunning.set(false);
            logger.info("Timer stopped. Total executions: {}", executionCount);
        }
    }
    
    /**
     * Check if the timer is currently running
     * @return true if timer is running, false otherwise
     */
    public boolean isRunning() {
        return isRunning.get();
    }
    
    /**
     * Get the number of times the refresh task has been executed
     * @return execution count
     */
    public int getExecutionCount() {
        return executionCount;
    }
    
    /**
     * Get the timestamp of the last execution
     * @return last execution time in milliseconds since epoch
     */
    public long getLastExecutionTime() {
        return lastExecutionTime;
    }
    
    /**
     * Schedule a one-time task
     * @param task the TimerTask to execute
     * @param delay the delay in milliseconds before execution
     */
    public void scheduleOnce(TimerTask task, long delay) {
        logger.info("Scheduling one-time task with delay={}ms", delay);
        
        if (timer == null) {
            timer = new Timer(true);
        }
        
        timer.schedule(task, delay);
    }
    
    /**
     * Schedule a fixed-rate task
     * @param task the TimerTask to execute
     * @param delay the delay in milliseconds before first execution
     * @param period the period in milliseconds between successive executions
     */
    public void scheduleFixedRate(TimerTask task, long delay, long period) {
        logger.info("Scheduling fixed-rate task: delay={}ms, period={}ms", delay, period);
        
        if (isRunning.get()) {
            logger.warn("Timer is already running. Restarting.");
            stopTimer();
        }
        
        timer = new Timer(true);
        isRunning.set(true);
        
        timer.scheduleAtFixedRate(task, delay, period);
    }
    
    /**
     * Reset the refresh singleton (for testing purposes)
     */
    public static void reset() {
        if (INSTANCE != null) {
            INSTANCE.stopTimer();
            INSTANCE = null;
            logger.info("Refresh singleton reset");
        }
    }
    
    /**
     * Cleanup method to be called on application shutdown
     */
    public void shutdown() {
        logger.info("Shutting down Refresh scheduler...");
        stopTimer();
        logger.info("Refresh scheduler shutdown complete");
    }
    
    @Override
    public String toString() {
        return String.format("Refresh[running=%s, executions=%d, lastExec=%d]",
            isRunning.get(), executionCount, lastExecutionTime);
    }
}
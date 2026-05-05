package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * TerminalDataLogic manages terminal-specific data operations for the KALCPOS system.
 * Handles database operations, resource cleanup, and graceful shutdown procedures.
 * Implements AutoCloseable for proper resource management.
 */
public class TerminalDataLogic implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(TerminalDataLogic.class);
    
    // Terminal configuration constants
    private static final String INSERT_TERMINAL_SQL = 
        "INSERT INTO terminals (terminal_id, terminal_name, location, status) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_TERMINAL_STATUS_SQL = 
        "UPDATE terminals SET status = ?, last_updated = CURRENT_TIMESTAMP WHERE terminal_id = ?";
    private static final String SELECT_TERMINAL_SQL = 
        "SELECT * FROM terminals WHERE terminal_id = ?";
    private static final String SELECT_ACTIVE_TERMINALS_SQL = 
        "SELECT * FROM terminals WHERE status = 'ACTIVE'";
    private static final String SELECT_TERMINAL_TRANSACTIONS_SQL = 
        "SELECT * FROM transactions WHERE terminal_id = ? ORDER BY transaction_date DESC LIMIT ?";
    
    // Application state tracking
    private final AtomicBoolean isShutdown = new AtomicBoolean(false);
    private final AtomicBoolean isOperational = new AtomicBoolean(true);
    private final String terminalId;
    private final String terminalName;
    private final String location;
    
    // Background services
    private final ScheduledExecutorService scheduler;
    private final SessionFactory sessionFactory;
    
    /**
     * Constructs a TerminalDataLogic instance for the specified terminal.
     * @param terminalId unique identifier for the terminal
     * @param terminalName display name for the terminal
     * @param location physical location of the terminal
     */
    public TerminalDataLogic(String terminalId, String terminalName, String location) {
        this.terminalId = terminalId;
        this.terminalName = terminalName;
        this.location = location;
        this.sessionFactory = SessionFactory.getInstance();
        this.scheduler = Executors.newScheduledThreadPool(2, r -> {
            Thread t = new Thread(r);
            t.setName("TerminalDataLogic-Worker-" + terminalId);
            t.setDaemon(true);
            return t;
        });
        
        logger.info("TerminalDataLogic initialized for terminal: {} ({})", terminalId, terminalName);
        
        // Register shutdown hook for graceful termination
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutdown hook triggered for terminal: {}", terminalId);
            shutdown();
        }));
    }
    
    /**
     * Registers the terminal in the database.
     * @return true if registration was successful, false otherwise
     */
    public boolean registerTerminal() {
        if (isShutdown.get()) {
            logger.warn("Cannot register terminal: system is shutting down");
            return false;
        }
        
        try (Connection connection = sessionFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_TERMINAL_SQL)) {
            
            preparedStatement.setString(1, terminalId);
            preparedStatement.setString(2, terminalName);
            preparedStatement.setString(3, location);
            preparedStatement.setString(4, "ACTIVE");
            
            int rowsAffected = preparedStatement.executeUpdate();
            
            if (rowsAffected > 0) {
                logger.info("Terminal registered successfully: {} - {}", terminalId, terminalName);
                return true;
            } else {
                logger.error("Failed to register terminal: {} - {}", terminalId, terminalName);
                return false;
            }
            
        } catch (DatabaseConnectionException e) {
            logger.error("Failed to obtain database connection for terminal registration: {}", 
                terminalId, e);
            return false;
        } catch (SQLException e) {
            logger.error("SQL error while registering terminal {}: {}", terminalId, e.getMessage(), e);
            logger.error("SQL State: {}, Error Code: {}", getSqlState(e), e.getErrorCode());
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error while registering terminal {}: {}", terminalId, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Updates the terminal status in the database.
     * @param status new status (e.g., ACTIVE, INACTIVE, MAINTENANCE)
     * @return true if update was successful, false otherwise
     */
    public boolean updateTerminalStatus(String status) {
        if (isShutdown.get()) {
            logger.warn("Cannot update terminal status: system is shutting down");
            return false;
        }
        
        try (Connection connection = sessionFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_TERMINAL_STATUS_SQL)) {
            
            preparedStatement.setString(1, status);
            preparedStatement.setString(2, terminalId);
            
            int rowsAffected = preparedStatement.executeUpdate();
            
            if (rowsAffected > 0) {
                logger.info("Terminal status updated: {} -> {} for terminal: {}", 
                    status, terminalId);
                isOperational.set("ACTIVE".equals(status));
                return true;
            } else {
                logger.warn("No terminal found with ID: {}", terminalId);
                return false;
            }
            
        } catch (DatabaseConnectionException e) {
            logger.error("Failed to obtain database connection for status update: {}", 
                terminalId, e);
            return false;
        } catch (SQLException e) {
            logger.error("SQL error while updating terminal status {}: {}", 
                terminalId, e.getMessage(), e);
            logger.error("SQL State: {}, Error Code: {}", getSqlState(e), e.getErrorCode());
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error while updating terminal status {}: {}", 
                terminalId, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Retrieves terminal information from the database.
     * @return TerminalInfo object containing terminal details, or null if not found
     */
    public TerminalInfo getTerminalInfo() {
        if (isShutdown.get()) {
            logger.warn("Cannot retrieve terminal info: system is shutting down");
            return null;
        }
        
        try (Connection connection = sessionFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_TERMINAL_SQL)) {
            
            preparedStatement.setString(1, terminalId);
            
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    TerminalInfo info = new TerminalInfo(
                        resultSet.getString("terminal_id"),
                        resultSet.getString("terminal_name"),
                        resultSet.getString("location"),
                        resultSet.getString("status"),
                        resultSet.getTimestamp("last_updated")
                    );
                    logger.debug("Retrieved terminal info for: {}", terminalId);
                    return info;
                }
            }
            
        } catch (DatabaseConnectionException e) {
            logger.error("Failed to obtain database connection for terminal info: {}", 
                terminalId, e);
        } catch (SQLException e) {
            logger.error("SQL error while retrieving terminal info {}: {}", 
                terminalId, e.getMessage(), e);
            logger.error("SQL State: {}, Error Code: {}", getSqlState(e), e.getErrorCode());
        } catch (Exception e) {
            logger.error("Unexpected error while retrieving terminal info {}: {}", 
                terminalId, e.getMessage(), e);
        }
        
        return null;
    }
    
    /**
     * Retrieves all active terminals from the database.
     * @return list of active TerminalInfo objects
     */
    public List<TerminalInfo> getActiveTerminals() {
        List<TerminalInfo> terminals = new ArrayList<>();
        
        if (isShutdown.get()) {
            logger.warn("Cannot retrieve active terminals: system is shutting down");
            return terminals;
        }
        
        try (Connection connection = sessionFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ACTIVE_TERMINALS_SQL);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            
            while (resultSet.next()) {
                TerminalInfo info = new TerminalInfo(
                    resultSet.getString("terminal_id"),
                    resultSet.getString("terminal_name"),
                    resultSet.getString("location"),
                    resultSet.getString("status"),
                    resultSet.getTimestamp("last_updated")
                );
                terminals.add(info);
            }
            
            logger.debug("Retrieved {} active terminals", terminals.size());
            
        } catch (DatabaseConnectionException e) {
            logger.error("Failed to obtain database connection for active terminals", e);
        } catch (SQLException e) {
            logger.error("SQL error while retrieving active terminals: {}", e.getMessage(), e);
            logger.error("SQL State: {}, Error Code: {}", getSqlState(e), e.getErrorCode());
        } catch (Exception e) {
            logger.error("Unexpected error while retrieving active terminals: {}", e.getMessage(), e);
        }
        
        return terminals;
    }
    
    /**
     * Retrieves recent transactions for this terminal.
     * @param limit maximum number of transactions to retrieve
     * @return list of transaction records
     */
    public List<TransactionRecord> getRecentTransactions(int limit) {
        List<TransactionRecord> transactions = new ArrayList<>();
        
        if (isShutdown.get()) {
            logger.warn("Cannot retrieve transactions: system is shutting down");
            return transactions;
        }
        
        try (Connection connection = sessionFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_TERMINAL_TRANSACTIONS_SQL)) {
            
            preparedStatement.setString(1, terminalId);
            preparedStatement.setInt(2, limit);
            
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    TransactionRecord record = new TransactionRecord(
                        resultSet.getLong("transaction_id"),
                        resultSet.getString("terminal_id"),
                        resultSet.getString("transaction_type"),
                        resultSet.getBigDecimal("amount"),
                        resultSet.getTimestamp("transaction_date")
                    );
                    transactions.add(record);
                }
            }
            
            logger.debug("Retrieved {} recent transactions for terminal: {}", 
                transactions.size(), terminalId);
            
        } catch (DatabaseConnectionException e) {
            logger.error("Failed to obtain database connection for transactions: {}", 
                terminalId, e);
        } catch (SQLException e) {
            logger.error("SQL error while retrieving transactions for {}: {}", 
                terminalId, e.getMessage(), e);
            logger.error("SQL State: {}, Error Code: {}", getSqlState(e), e.getErrorCode());
        } catch (Exception e) {
            logger.error("Unexpected error while retrieving transactions for {}: {}", 
                terminalId, e.getMessage(), e);
        }
        
        return transactions;
    }
    
    /**
     * Processes a transaction for this terminal.
     * @param transactionType type of transaction (e.g., SALE, RETURN, VOID)
     * @param amount transaction amount
     * @return true if transaction was processed successfully
     */
    public boolean processTransaction(String transactionType, java.math.BigDecimal amount) {
        if (isShutdown.get()) {
            logger.warn("Cannot process transaction: system is shutting down");
            return false;
        }
        
        if (!isOperational.get()) {
            logger.warn("Cannot process transaction: terminal {} is not operational", terminalId);
            return false;
        }
        
        String insertTransactionSQL = 
            "INSERT INTO transactions (terminal_id, transaction_type, amount, status) " +
            "VALUES (?, ?, ?, 'COMPLETED')";
        
        try (Connection connection = sessionFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertTransactionSQL)) {
            
            preparedStatement.setString(1, terminalId);
            preparedStatement.setString(2, transactionType);
            preparedStatement.setBigDecimal(3, amount);
            
            int rowsAffected = preparedStatement.executeUpdate();
            
            if (rowsAffected > 0) {
                logger.info("Transaction processed successfully: {} {} for terminal {}", 
                    transactionType, amount, terminalId);
                return true;
            } else {
                logger.error("Failed to process transaction for terminal: {}", terminalId);
                return false;
            }
            
        } catch (DatabaseConnectionException e) {
            logger.error("Failed to obtain database connection for transaction: {}", 
                terminalId, e);
            return false;
        } catch (SQLException e) {
            logger.error("SQL error while processing transaction for {}: {}", 
                terminalId, e.getMessage(), e);
            logger.error("SQL State: {}, Error Code: {}", getSqlState(e), e.getErrorCode());
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error while processing transaction for {}: {}", 
                terminalId, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Schedules a periodic task for terminal health checks.
     * @param initialDelay delay before first execution
     * @param period period between executions
     * @param unit time unit
     */
    public void scheduleHealthCheck(long initialDelay, long period, TimeUnit unit) {
        scheduler.scheduleAtFixedRate(() -> {
            if (!isShutdown.get()) {
                performHealthCheck();
            } else {
                logger.info("Health check skipped: system is shutting down");
            }
        }, initialDelay, period, unit);
        
        logger.info("Health check scheduled for terminal: {} (every {} {})", 
            terminalId, period, unit);
    }
    
    /**
     * Performs a health check for the terminal.
     */
    private void performHealthCheck() {
        try {
            logger.debug("Performing health check for terminal: {}", terminalId);
            
            // Test database connection
            if (sessionFactory.testConnection()) {
                logger.debug("Health check passed for terminal: {}", terminalId);
            } else {
                logger.warn("Health check failed for terminal: {} - database connection issue", terminalId);
                isOperational.set(false);
            }
            
        } catch (Exception e) {
            logger.error("Health check failed for terminal {}: {}", terminalId, e.getMessage(), e);
            isOperational.set(false);
        }
    }
    
    /**
     * Returns the operational status of the terminal.
     * @return true if terminal is operational, false otherwise
     */
    public boolean isOperational() {
        return isOperational.get() && !isShutdown.get();
    }
    
    /**
     * Returns the shutdown status of the terminal.
     * @return true if terminal is shutting down or has shut down
     */
    public boolean isShutdown() {
        return isShutdown.get();
    }
    
    /**
     * Initiates shutdown sequence for the terminal.
     * Performs cleanup operations including:
     * - Closing database connections
     * - Stopping background services
     * - Releasing resources
     * - Updating terminal status to INACTIVE
     */
    public void shutdown() {
        if (isShutdown.compareAndSet(false, true)) {
            logger.info("Initiating shutdown sequence for terminal: {}...", terminalId);
            
            // Update terminal status to INACTIVE
            try {
                updateTerminalStatus("INACTIVE");
            } catch (Exception e) {
                logger.warn("Failed to update terminal status during shutdown: {}", e.getMessage());
            }
            
            // Stop background scheduler
            if (!scheduler.isShutdown()) {
                scheduler.shutdown();
                try {
                    if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                        scheduler.shutdownNow();
                        if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                            logger.error("Scheduler did not terminate properly");
                        }
                    }
                } catch (InterruptedException e) {
                    scheduler.shutdownNow();
                    Thread.currentThread().interrupt();
                    logger.warn("Scheduler shutdown interrupted: {}", e.getMessage());
                }
                logger.info("Background scheduler stopped for terminal: {}", terminalId);
            }
            
            // Set operational flag to false
            isOperational.set(false);
            
            logger.info("Shutdown completed successfully for terminal: {}", terminalId);
        } else {
            logger.debug("Shutdown already in progress or completed for terminal: {}", terminalId);
        }
    }
    
    /**
     * AutoCloseable implementation for try-with-resources support.
     */
    @Override
    public void close() {
        logger.info("Closing TerminalDataLogic for terminal: {}", terminalId);
        shutdown();
    }
    
    /**
     * Example method demonstrating proper SQLException handling.
     * Replaces abrupt termination with graceful error handling.
     */
    public void someMethod() {
        try {
            // Some operation that might throw SQLException
            if (!isOperational.get()) {
                throw new SQLException("Terminal is not operational");
            }
            
            // Simulated database operation
            logger.debug("Performing database operation for terminal: {}", terminalId);
            
        } catch (SQLException e) {
            logger.error("SQLException occurred: {}", e.getMessage(), e);
            isOperational.set(false);
            // Consider rethrowing as custom exception or handling appropriately
            // For example: throw new TerminalOperationException("Failed to perform operation", e);
        }
    }
    
    /**
     * Helper method to get SQL state from SQLException.
     */
    private String getSqlState(SQLException e) {
        return e.getSQLState() != null ? e.getSQLState() : "N/A";
    }
    
    /**
     * TerminalInfo - Data class for terminal information.
     */
    public static class TerminalInfo {
        private final String terminalId;
        private final String terminalName;
        private final String location;
        private final String status;
        private final java.sql.Timestamp lastUpdated;
        
        public TerminalInfo(String terminalId, String terminalName, String location, 
                          String status, java.sql.Timestamp lastUpdated) {
            this.terminalId = terminalId;
            this.terminalName = terminalName;
            this.location = location;
            this.status = status;
            this.lastUpdated = lastUpdated;
        }
        
        // Getters
        public String getTerminalId() { return terminalId; }
        public String getTerminalName() { return terminalName; }
        public String getLocation() { return location; }
        public String getStatus() { return status; }
        public java.sql.Timestamp getLastUpdated() { return lastUpdated; }
        
        @Override
        public String toString() {
            return String.format("TerminalInfo{id=%s, name=%s, location=%s, status=%s, lastUpdated=%s}",
                terminalId, terminalName, location, status, lastUpdated);
        }
    }
    
    /**
     * TransactionRecord - Data class for transaction records.
     */
    public static class TransactionRecord {
        private final long transactionId;
        private final String terminalId;
        private final String transactionType;
        private final java.math.BigDecimal amount;
        private final java.sql.Timestamp transactionDate;
        
        public TransactionRecord(long transactionId, String terminalId, String transactionType,
                                java.math.BigDecimal amount, java.sql.Timestamp transactionDate) {
            this.transactionId = transactionId;
            this.terminalId = terminalId;
            this.transactionType = transactionType;
            this.amount = amount;
            this.transactionDate = transactionDate;
        }
        
        // Getters
        public long getTransactionId() { return transactionId; }
        public String getTerminalId() { return terminalId; }
        public String getTransactionType() { return transactionType; }
        public java.math.BigDecimal getAmount() { return amount; }
        public java.sql.Timestamp getTransactionDate() { return transactionDate; }
        
        @Override
        public String toString() {
            return String.format("TransactionRecord{id=%d, terminalId=%s, type=%s, amount=%s, date=%s}",
                transactionId, terminalId, transactionType, amount, transactionDate);
        }
    }
}
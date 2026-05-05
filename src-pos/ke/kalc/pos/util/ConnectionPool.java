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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionPool {
    private static ConnectionPool instance;
    private javax.sql.DataSource dataSource;
    private Properties properties;

    private ConnectionPool(Properties props) {
        this.properties = props;
        initializeDataSource();
    }

    public static synchronized ConnectionPool getInstance(Properties props) {
        if (instance == null) {
            instance = new ConnectionPool(props);
        }
        return instance;
    }

    public static synchronized ConnectionPool getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ConnectionPool not initialized. Call getInstance(Properties) first.");
        }
        return instance;
    }

    private void initializeDataSource() {
        // Simple connection pool implementation using a lightweight approach
        // In production, consider using HikariCP or Apache DBCP
        String jdbcUrl = properties.getProperty("db.url");
        String username = properties.getProperty("db.user");
        String password = properties.getProperty("db.password");
        int maxPoolSize = Integer.parseInt(properties.getProperty("db.pool.maxSize", "20"));
        int minPoolSize = Integer.parseInt(properties.getProperty("db.pool.minSize", "5"));

        // Create a basic DataSource wrapper
        dataSource = new BasicDataSourceWrapper(jdbcUrl, username, password, maxPoolSize, minPoolSize);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void close() {
        if (dataSource != null && dataSource instanceof BasicDataSourceWrapper) {
            ((BasicDataSourceWrapper) dataSource).close();
        }
    }

    // Inner class for basic DataSource functionality
    private static class BasicDataSourceWrapper implements javax.sql.DataSource {
        private final java.util.Queue<Connection> connectionPool;
        private final int maxPoolSize;
        private final int minPoolSize;
        private final String jdbcUrl;
        private final String username;
        private final String password;
        private boolean closed = false;

        public BasicDataSourceWrapper(String jdbcUrl, String username, String password, 
                                    int maxPoolSize, int minPoolSize) {
            this.jdbcUrl = jdbcUrl;
            this.username = username;
            this.password = password;
            this.maxPoolSize = maxPoolSize;
            this.minPoolSize = minPoolSize;
            this.connectionPool = new java.util.concurrent.ConcurrentLinkedQueue<>();
            
            // Pre-populate with minimum connections
            for (int i = 0; i < minPoolSize; i++) {
                try {
                    connectionPool.offer(createNewConnection());
                } catch (SQLException e) {
                    // Log error but continue
                }
            }
        }

        @Override
        public Connection getConnection() throws SQLException {
            if (closed) {
                throw new SQLException("Connection pool is closed");
            }
            
            Connection conn = connectionPool.poll();
            if (conn == null || conn.isClosed()) {
                if (connectionPool.size() < maxPoolSize) {
                    conn = createNewConnection();
                } else {
                    throw new SQLException("Connection pool exhausted");
                }
            }
            return new PooledConnectionWrapper(conn, this);
        }

        @Override
        public Connection getConnection(String username, String password) throws SQLException {
            return getConnection(); // Use configured credentials
        }

        @Override
        public java.io.PrintWriter getLogWriter() { return null; }

        @Override
        public void setLogWriter(java.io.PrintWriter out) {}

        @Override
        public void setLoginTimeout(int seconds) {}

        @Override
        public int getLoginTimeout() { return 0; }

        @Override
        public java.util.logging.Logger getParentLogger() { return null; }

        @Override
        public <T> T unwrap(Class<T> iface) { return null; }

        @Override
        public boolean isWrapperFor(Class<?> iface) { return false; }

        private Connection createNewConnection() throws SQLException {
            try {
                Class.forName("org.postgresql.Driver");
            } catch (ClassNotFoundException e) {
                throw new SQLException("Database driver not found", e);
            }
            return java.sql.DriverManager.getConnection(jdbcUrl, username, password);
        }

        public void releaseConnection(Connection conn) {
            if (conn != null && !closed) {
                try {
                    if (!conn.isClosed() && connectionPool.size() < maxPoolSize) {
                        connectionPool.offer(conn);
                    } else {
                        conn.close();
                    }
                } catch (SQLException e) {
                    // Connection is broken, don't return to pool
                }
            }
        }

        public void close() {
            closed = true;
            Connection conn;
            while ((conn = connectionPool.poll()) != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    // Ignore
                }
            }
        }
    }

    // Wrapper that returns connections to pool on close
    private static class PooledConnectionWrapper implements Connection {
        private final Connection delegate;
        private final BasicDataSourceWrapper pool;
        private boolean closed = false;

        public PooledConnectionWrapper(Connection delegate, BasicDataSourceWrapper pool) {
            this.delegate = delegate;
            this.pool = pool;
        }

        @Override
        public void close() throws SQLException {
            if (!closed) {
                closed = true;
                pool.releaseConnection(delegate);
            }
        }

        @Override
        public boolean isClosed() throws SQLException {
            return closed || delegate.isClosed();
        }

        // Delegate all other methods to the wrapped connection
        @Override public java.sql.Statement createStatement() throws SQLException { return delegate.createStatement(); }
        @Override public java.sql.PreparedStatement prepareStatement(String sql) throws SQLException { return delegate.prepareStatement(sql); }
        @Override public java.sql.CallableStatement prepareCall(String sql) throws SQLException { return delegate.prepareCall(sql); }
        @Override public String nativeSQL(String sql) throws SQLException { return delegate.nativeSQL(sql); }
        @Override public void setAutoCommit(boolean autoCommit) throws SQLException { delegate.setAutoCommit(autoCommit); }
        @Override public boolean getAutoCommit() throws SQLException { return delegate.getAutoCommit(); }
        @Override public void commit() throws SQLException { delegate.commit(); }
        @Override public void rollback() throws SQLException { delegate.rollback(); }
        @Override public java.sql.Savepoint setSavepoint() throws SQLException { return delegate.setSavepoint(); }
        @Override public java.sql.Savepoint setSavepoint(String name) throws SQLException { return delegate.setSavepoint(name); }
        @Override public void rollback(java.sql.Savepoint savepoint) throws SQLException { delegate.rollback(savepoint); }
        @Override public void releaseSavepoint(java.sql.Savepoint savepoint) throws SQLException { delegate.releaseSavepoint(savepoint); }
        @Override public Clob createClob() throws SQLException { return delegate.createClob(); }
        @Override public Blob createBlob() throws SQLException { return delegate.createBlob(); }
        @Override public NClob createNClob() throws SQLException { return delegate.createNClob(); }
        @Override public SQLXML createSQLXML() throws SQLException { return delegate.createSQLXML(); }
        @Override public boolean isValid(int timeout) throws SQLException { return delegate.isValid(timeout); }
        @Override public void setClientInfo(String name, String value) throws SQLException { delegate.setClientInfo(name, value); }
        @Override public void setClientInfo(Properties properties) throws SQLException { delegate.setClientInfo(properties); }
        @Override public String getClientInfo(String name) throws SQLException { return delegate.getClientInfo(name); }
        @Override public Properties getClientInfo() throws SQLException { return delegate.getClientInfo(); }
        @Override public Array createArrayOf(String typeName, Object[] elements) throws SQLException { return delegate.createArrayOf(typeName, elements); }
        @Override public Struct createStruct(String typeName, Object[] attributes) throws SQLException { return delegate.createStruct(typeName, attributes); }
        @Override public void setHoldability(int holdability) throws SQLException { delegate.setHoldability(holdability); }
        @Override public int getHoldability() throws SQLException { return delegate.getHoldability(); }
        @Override public java.sql.CacheResultSet createCachedRowSet() throws SQLException { return delegate.createCachedRowSet(); }
        @Override public Map<String, Class<?>> getTypeMap() throws SQLException { return delegate.getTypeMap(); }
        @Override public void setTypeMap(Map<String, Class<?>> map) throws SQLException { delegate.setTypeMap(map); }
        @Override public String getCatalog() throws SQLException { return delegate.getCatalog(); }
        @Override public void setCatalog(String catalog) throws SQLException { delegate.setCatalog(catalog); }
        @Override public Connection getConnection() throws SQLException { return this; }
        @Override public int getTransactionIsolation() throws SQLException { return delegate.getTransactionIsolation(); }
        @Override public void setTransactionIsolation(int level) throws SQLException { delegate.setTransactionIsolation(level); }
        @Override public java.sql.Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException { return delegate.createStatement(resultSetType, resultSetConcurrency); }
        @Override public java.sql.PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException { return delegate.prepareStatement(sql, resultSetType, resultSetConcurrency); }
        @Override public java.sql.CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException { return delegate.prepareCall(sql, resultSetType, resultSetConcurrency); }
        @Override public java.sql.Map<String, Class<?>> getClientProperties() throws SQLException { return delegate.getClientProperties(); }
        @Override public void setSchema(String schema) throws SQLException { delegate.setSchema(schema); }
        @Override public String getSchema() throws SQLException { return delegate.getSchema(); }
        @Override public void abort(java.util.concurrent.Executor executor) throws SQLException { delegate.abort(executor); }
        @Override public void clearWarnings() throws SQLException { delegate.clearWarnings(); }
        @Override public java.sql.Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldality) throws SQLException { return delegate.createStatement(resultSetType, resultSetConcurrency, resultSetHoldality); }
        @Override public java.sql.PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException { return delegate.prepareStatement(sql, autoGeneratedKeys); }
        @Override public java.sql.PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException { return delegate.prepareStatement(sql, columnIndexes); }
        @Override public java.sql.PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException { return delegate.prepareStatement(sql, columnNames); }
        @Override public java.sql.Clob createClob() throws SQLException { return delegate.createClob(); }
        @Override public java.sql.Blob createBlob() throws SQLException { return delegate.createBlob(); }
        @Override public java.sql.NClob createNClob() throws SQLException { return delegate.createNClob(); }
        @Override public java.sql.SQLXML createSQLXML() throws SQLException { return delegate.createSQLXML(); }
        @Override public boolean isValid(int timeout) throws SQLException { return delegate.isValid(timeout); }
        @Override public void setClientInfo(String name, String value) throws SQLException { delegate.setClientInfo(name, value); }
        @Override public void setClientInfo(Properties properties) throws SQLException { delegate.setClientInfo(properties); }
        @Override public String getClientInfo(String name) throws SQLException { return delegate.getClientInfo(name); }
        @Override public Properties getClientInfo() throws SQLException { return delegate.getClientInfo(); }
        @Override public java.sql.Array createArrayOf(String typeName, Object[] elements) throws SQLException { return delegate.createArrayOf(typeName, elements); }
        @Override public java.sql.Struct createStruct(String typeName, Object[] attributes) throws SQLException { return delegate.createStruct(typeName, attributes); }
        @Override public void setHoldability(int holdability) throws SQLException { delegate.setHoldability(holdability); }
        @Override public int getHoldability() throws SQLException { return delegate.getHoldability(); }
        @Override public java.sql.Savepoint setSavepoint() throws SQLException { return delegate.setSavepoint(); }
        @Override public java.sql.Savepoint setSavepoint(String name) throws SQLException { return delegate.setSavepoint(name); }
        @Override public void rollback(java.sql.Savepoint savepoint) throws SQLException { delegate.rollback(savepoint); }
        @Override public void releaseSavepoint(java.sql.Savepoint savepoint) throws SQLException { delegate.releaseSavepoint(savepoint); }
        @Override public Clob createClob() throws SQLException { return delegate.createClob(); }
        @Override public Blob createBlob() throws SQLException { return delegate.createBlob(); }
        @Override public NClob createNClob() throws SQLException { return delegate.createNClob(); }
        @Override public SQLXML createSQLXML() throws SQLException { return delegate.createSQLXML(); }
        @Override public boolean isValid(int timeout) throws SQLException { return delegate.isValid(timeout); }
        @Override public void setClientInfo(String name, String value) throws SQLException { delegate.setClientInfo(name, value); }
        @Override public void setClientInfo(Properties properties) throws SQLException { delegate.setClientInfo(properties); }
        @Override public String getClientInfo(String name) throws SQLException { return delegate.getClientInfo(name); }
        @Override public Properties getClientInfo() throws SQLException { return delegate.getClientInfo(); }
        @Override public Array createArrayOf(String typeName, Object[] elements) throws SQLException { return delegate.createArrayOf(typeName, elements); }
        @Override public Struct createStruct(String typeName, Object[] attributes) throws SQLException { return delegate.createStruct(typeName, attributes); }
    }
}
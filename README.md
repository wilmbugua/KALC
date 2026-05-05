# KALCPOS - Point of Sale System

## Overview
KALCPOS is a Point of Sale system with database integration for managing product lines.

## Database Setup
1. Create a MySQL database named `kalcpo_db`
2. Create the `product_line` table:
```sql
CREATE TABLE product_line (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## Configuration
Update the database connection settings in `src/main/java/com/example/KALCPOSApplication.java`:
- `DB_URL` - Your database connection URL
- `DB_USER` - Your database username
- `DB_PASSWORD` - Your database password

## Building
```bash
mvn clean compile
```

## Running
```bash
mvn exec:java -Dexec.mainClass="com.example.KALCPOSApplication"
```

## Project Structure
```
KALCPOS/
├── pom.xml                    # Maven configuration
├── README.md                  # Project documentation
└── src/main/java/com/example/
    └── KALCPOSApplication.java # Main application with database operations
```

## Features
- Database connectivity using JDBC
- Prepared statements for secure SQL operations
- Proper resource management with try-with-resources
- Error handling and logging
# Security Guidelines for KALC POS

## ⚠️ Critical: Never Commit Credentials

**KALC POS contains sensitive information that should NEVER be committed to version control:**

- Database usernames and passwords
- Encrypted credentials (still vulnerable)
- API keys and tokens
- Configuration with hardcoded secrets
- Private encryption keys
- Authentication tokens

---

## 🔒 Configuration Security

### Step 1: Use the Template File

```bash
# Copy the example configuration
cp kalcconfig.properties.example kalcconfig.properties

# Edit with YOUR credentials (NEVER commit this file!)
nano kalcconfig.properties
```

### Step 2: Never Commit kalcconfig.properties

This file contains database credentials and should be added to `.gitignore`:
```properties
# Database credentials - NEVER commit
database.user=your_username
database.password=your_password
```

---

## 🛡️ Best Practices

### 1. Use Environment Variables (Recommended)
For production deployments, consider using environment variables:
```bash
export DB_USER=your_username
export DB_PASSWORD=your_password
```

### 2. Use Strong Passwords
- Minimum 12 characters
- Mix of uppercase, lowercase, numbers, and symbols
- Avoid common words and patterns

### 3. Regular Rotation
Change database credentials periodically:
- Every 90 days for standard security
- Every 30 days for high-security environments

### 4. Principle of Least Privilege
Database user should have only necessary permissions:
- SELECT, INSERT, UPDATE, DELETE on KALC POS tables
- No DROP, ALTER, or admin privileges unless absolutely required

### 5. Network Security
- Restrict database access to application servers only
- Use firewall rules to limit access
- Consider SSL/TLS for database connections

---

## 🚨 If You Accidentally Commit Credentials

1. **IMMEDIATELY** change the compromised credentials
2. Remove the commit from history using `git filter-branch` or BFG Repo-Cleaner
3. Force push the cleaned history
4. Audit logs for any suspicious activity

---

## 📋 Security Checklist

Before committing any changes, verify:

- [ ] No passwords in kalcconfig.properties
- [ ] No API keys in source code
- [ ] No sensitive data in comments
- [ ] No private keys in repository
- [ ] .gitignore is properly configured

---

## 🔐 Encryption Note

While KALC POS encrypts stored passwords using reversible encryption, this is **obfuscation**, not strong security. Anyone with access to the code can decrypt these values. Never treat encrypted credentials as safe to commit.

---
*Last updated: April 2026*
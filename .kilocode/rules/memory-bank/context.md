# Memory Bank Context

## Recently Completed
- [x] Added waiter commission feature for sales made
- [x] Created CommissionUtils utility class for calculating commissions
- [x] Kitchen commission: Fixed 2% of food sales
- [x] Bar commission: Tiered structure (1%-11% based on sales amount)
- [x] Documented bar commission brackets explicitly in CommissionUtils Javadoc (0-100K:1%, 100K-200K:2%, ..., >1M:11%)
- [x] Currency formatted as Ksh (Kenyan Shilling)
- [x] Added login.image.path property to kalcconfig.properties
- [x] Modified LoginDialog to remove username field and use 8-digit PIN only (fixed duplicate code)
- [x] Added restaurant image display to login page loaded from kalcconfig.properties
- [x] Created PropertyUtil utility class to read properties from kalcconfig.properties
- [x] Fixed LoginSettingsDialog to actually save logo path via AppConfig
- [x] Completely redesigned login page: PIN keypad (numeric 0-9, Clear, Enter) on right side
- [x] Added digital clock display on left side of login page (updates every 250ms)
- [x] Restaurant logo configurable via admin panel and displayed on login page
- [x] Modified JRootApp to use new PIN-only login UI (removed old user list login)
- [x] Added findPeopleByPIN method in DataLogicSystem for PIN lookup (reuses apppassword column as PIN hash)
- [x] Fixed PropertyUtils.getDBPassword to return actual password from properties (was placeholder)
- [x] Fixed empty catch blocks in hardware scale and scanner classes (ScaleSamsungEsp, ScaleCasioPD1, ScaleComm, ScaleCASPDII, DeviceScannerComm) to printStackTrace
- [x] Fixed ConnectionPoolFactory.getConnection to throw RuntimeException instead of returning null on error
- [x] Created test class for CommissionUtils to verify calculations
- [x] Updated memory bank with waiter commission feature details
- [x] Added recall, modify, merge and split bill functionality
- [x] Added Mpesa payment method alongside cash and card (removed other payment methods per requirements)
- [x] Created super waiter role with ability to confirm Mpesa and card payments
- [x] Added UI buttons for Recall, Modify, Merge in restaurant interface
- [x] Implemented pending payment status for Mpesa and card transactions requiring manager/super waiter confirmation
- [x] Added locale properties for new features and buttons
- [x] Limited payment methods to cash, mpesa, and card only as requested
- [x] Card payments now require manager/super waiter approval (pending status)
- [x] Updated all images in src-beans/ke/kalc/fixedimages/ to KALC POS branding (replaced existing image files with new branded images)
- [x] SECURITY: Fixed SQL injection in DataLogicSystem.getNoSales() by using PreparedSentence with parameter binding
- [x] SECURITY: Added XXE protection to IncludeFile.java XML parser (disallow DOCTYPE, external entities, secure processing)
- [x] SECURITY: Added path traversal validation in IconFactory.cacheIconsFromZip() using canonical path checks
- [x] SECURITY: PIN input already validated to exactly 8 digits in login UI

## Current State
- All features implemented and committed to repository
- Login page uses 8-digit PIN authentication without username, featuring numeric keypad (right) and digital clock + restaurant logo (left)
- Logo path configurable via admin Login Settings dialog, saved in kalcconfig.properties
- PIN hashing uses SHA-1 via Hashcypher; PIN stored in apppassword column
- DataLogicSystem provides findPeopleByPIN for user lookup
- Fixed critical bugs: PropertyUtils DB password retrieval, empty catch blocks, connection pool error handling
- Waiter commission feature, Mpesa payments, super waiter role, bill management all functional
- Repository is clean with all changes committed
- Ready for production use

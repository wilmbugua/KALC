# KALC POS

> **KALC POS** - Professional Point of Sale System

[![License: Proprietary](https://img.shields.io/badge/license-Proprietary-blue.svg)](LICENSE)

KALC POS is a comprehensive, production-ready point-of-sale system designed for retail, hospitality, and restaurant operations. Built with Java SE, it provides a complete solution for sales management, inventory control, customer management, and business analytics.

## Features

### Core POS Operations
- **Sales Processing** - Fast and intuitive ticket creation with product search, categories, and modifiers
- **Multiple Payment Types** - Cash, credit/debit card, gift cards, vouchers, and custom payment methods
- **Receipt Printing** - Thermal printer support with customizable receipt templates
- **Customer Management** - Complete CRM with loyalty program support
- **Barcode Scanning** - Integrated barcode scanner support
- **Scale Integration** - Connect weight scales for produce/meat departments

### Restaurant & Hospitality
- **Table Management** - Visual floor plan with table status tracking
- **Split Bills** - Divide checks by customer or item
- **Kitchen Display** - Order routing to kitchen screens or printers
- **Bar Mode** - Separate bar orders and printing
- **Reservations** - Table booking and management

### Inventory & Products
- **Product Catalog** - Hierarchical categories with images and pricing
- **Stock Management** - Real-time inventory tracking and updates
- **Supplier Management** - Purchase orders and supplier relationships
- **Stock Takes** - Physical inventory counting and reconciliation

### Business Intelligence
- **Sales Reports** - Detailed sales analytics by period, product, category, employee
- **Audit Trail** - Complete transaction history and user activity logging
- **Commission Tracking** - Staff performance and commission calculations (if applicable)

### Technical Features
- **Database Support** - MySQL / PostgreSQL backends
- **Multi-currency** - Support for multiple currencies and tax regimes
- **Multi-terminal** - networked operations across multiplePOS stations
- **User Roles** - Granular permissions for staff (cashier, supervisor, manager, admin)
- **Offline Mode** - Continue operations during network outages

## System Requirements

### Minimum Requirements
- **OS**: Windows 10 / 11, Linux, or macOS
- **RAM**: 4 GB (8 GB recommended)
- **Storage**: 2 GB free disk space
- **Java**: JRE 11 or later (included JRE in distribution)

### Supported Peripherals
- Receipt printers (ESC/POS compatible)
- Customer displays (pole displays)
- Barcode scanners (USB or serial)
- Weight scales (serial/USB)
- Cash drawers (printer-driven or serial)
- Magnetic stripe card readers

## Quick Start

### Installation

1. **Download** the latest `kalc.jar` from the releases page

2. **Run** the application:
   ```bash
   java -jar kalc.jar
   ```

   Or on Windows, double-click the JAR file

3. **First-time Setup** - The application will guide you through:
   - Database configuration
   - Company information
   - Initial user accounts
   - Printer setup

### Default Login

After initial setup:

- **Username**: `admin`
- **Password**: `admin`

⚠️ **Change the default password immediately after first login**

## Configuration

### Database Setup

KALC POS uses MySQL or PostgreSQL. Create an empty database and configure credentials in the application settings or via the configuration file.

Configuration options are stored in `kalcconfig.properties`:

```properties
database.user=eposuser
database.server=localhost
database.port=3306
database.name=KALCpos
```

### Printers

Configure thermal receipt printers through the settings menu:
- Select printer type (ESC/POS, JavaPOS)
- Set paper size (80mm or 58mm)
- Configure kitchen/bar printers

## Directory Structure

```
KALC-POS/
├── kalc.jar                 # Main application executable
├── kalcconfig.properties     # Configuration file
├── files.txt                 # Module dependencies for jlink
├── lib/                      # Third-party JAR dependencies
├── jre/                      # Java Runtime (Windows/Linux bundles)
├── src-beans/ke/kalc/        # JavaBeans and shared utilities
├── src-data/ke/kalc/         # Data access layer and loaders
├── src-pos/ke/kalc/          # POS application source code
├── cssStyles/               # Look and feel CSS styling
├── locales/                 # Translation files
├── images/                  # Icon assets
├── licensing/               # License and third-party notices
└── uk/methods/              # Reflection utilities
```

## Development

KALC POS is built using NetBeans and standard Java build tools.

### Building from Source

```bash
# Clone the repository
git clone https://github.com/wilmbugua/KALCPOS.git
cd KALCPOS

# Compile with javac or use NetBeans IDE
# The project uses Ant-based build structure typical of NetBeans projects
```

### Dependencies

All dependencies are included in the `lib/` directory:
- MySQL Connector/J
- JavaFX components
- Barcode4J
- C3P0 connection pooling
- FlatLaf modern look and feel
- And many more...

View complete list in `files.txt`.

## Customization

KALC POS is designed to be extensible:

- **Plugins** - Add custom payment gateways or device drivers
- **Reports** - Modify report templates (Velocity-based)
- **UI Themes** - Customize appearance via CSS files
- **Promotions** - Create custom promotion scripts
- **Integrations** - Connect to external systems via REST/DB

## Support & Documentation

- **Issues**: Report bugs and request features on GitHub Issues
- **Wiki**: Check the repository wiki for detailed documentation
- **Community**: Join discussions in repository Discussions

## License

KALC POS is proprietary software. See the [LICENSE](LICENSE) file for details.

> **Note**: KALC POS is a commercial product. The source code is available for review, customization, and self-hosting under the terms of the KALC POS License. Redistribution and commercial deployment require appropriate licensing.

## Credits

Originally forked from the Chromis POS project, KALC POS has been extensively rebranded, modified, and maintained as an independent POS solution. KALC POS is not affiliated with the original Chromis or Openbravo projects.

---

**KALC POS** - The New Face of Modern Point of Sale

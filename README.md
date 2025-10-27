# SwagLabs Test Automation Framework

A comprehensive enterprise-grade test automation framework for SwagLabs (Sauce Demo) e-commerce website featuring TestNG testing with Selenide, advanced reporting, comprehensive test coverage, and Docker containerization.

## 🌐 Live Report Links

Access the latest test reports directly:

📊 **Allure Report**: [View Latest Interactive Dashboard](https://emmanuelarhu.github.io/SwagLabsWithSelenide/)
🔍 **Test Results**: [View GitHub Actions Runs](../../actions)
📈 **Code Quality**: [View Qodana Analysis](../../actions/workflows/qodana_code_quality.yml)

---

## 🚀 Framework Features

### Core Technologies

- **Selenide 6.19.1** - Simplified WebDriver automation with built-in waits and fluent API
- **Java 21** - Latest LTS Java version with enhanced performance and modern language features
- **Maven** - Build automation and dependency management
- **TestNG 7.8.0** - Powerful testing framework with data providers and parallel execution

### Testing Architecture

- **TestNG Framework** - Comprehensive unit/integration testing with data-driven approach
- **Page Object Model** - Clean separation of page logic and test scenarios
- **Data-Driven Testing** - JSON-based test data with parameterized tests
- **Multi-User Testing** - Support for standard, locked, problem, and performance glitch users
- **Test Categorization** - Tests organized by priority (critical, validation, ui, performance, edge-cases, security)

### Reporting & Analysis

- **Allure 2.24.0** - Rich interactive reports with screenshots, trends, and test history
- **Screenshot on Failure** - Automatic screenshot capture with Allure integration
- **Test Categorization** - @Story, @Description, @Severity annotations for detailed reporting
- **Test History Tracking** - Historical trends and test execution analytics
- **Environment Information** - Detailed test environment and configuration tracking

### DevOps & CI/CD

- **Docker Support** - Complete containerization with Chrome browser and Xvfb display
- **GitHub Actions Pipeline** - Comprehensive CI/CD with scheduled runs and manual triggers
- **GitHub Pages Deployment** - Automatic report publishing with historical data
- **Multi-browser Testing** - Chrome and Firefox support with headless execution
- **Email Notifications** - Automatic email notifications with test results and report links
- **Qodana Integration** - Code quality analysis and inspection

### Advanced Features

- **Comprehensive Logging** - SLF4J with detailed test execution logs
- **Configuration Management** - Centralized configuration with properties file
- **Test Data Management** - JSON-based test data with multiple user types and scenarios
- **Utility Helpers** - AlertHandler, PopupHandler, ScreenshotUtils, ConfigManager
- **Makefile Automation** - 40+ commands for various test execution scenarios
- **WebDriverManager** - Automatic driver management

---

## 📁 Project Structure

```
SwagLabsWithSelenide/
├── src/
│   ├── main/
│   │   └── java/
│   └── test/
│       ├── java/com/swaglabs/
│       │   ├── base/
│       │   │   └── BaseTest.java                    # Base test configuration
│       │   ├── data/
│       │   │   ├── model/
│       │   │   │   └── TestDataModel.java           # Test data models
│       │   │   └── TestDataProvider.java            # TestNG data providers
│       │   ├── pages/                               # Page Object Model classes
│       │   │   ├── LoginPage.java
│       │   │   ├── ProductsPage.java
│       │   │   ├── ProductDetailsPage.java
│       │   │   ├── CartPage.java
│       │   │   ├── CheckoutPage.java
│       │   │   ├── CheckoutInformationPage.java
│       │   │   ├── CheckoutOverviewPage.java
│       │   │   └── CheckoutCompletePage.java
│       │   ├── tests/                               # TestNG test classes
│       │   │   ├── LoginTest.java                   # 7 parameterized login tests
│       │   │   ├── ProductsTest.java                # 8 comprehensive product tests
│       │   │   ├── CartTest.java                    # 9 cart management tests
│       │   │   ├── CheckoutTest.java                # 12 validation tests
│       │   │   ├── LogoutTest.java                  # 9 session tests
│       │   │   ├── SmokeTests.java                  # Critical path smoke tests
│       │   │   └── RegressionTests.java             # Comprehensive regression suite
│       │   ├── runner/
│       │   │   └── QuickTestRunner.java             # Quick test execution runner
│       │   └── utils/                               # Utility classes
│       │       ├── AlertHandler.java                # Alert handling utilities
│       │       ├── PopupHandler.java                # Popup management
│       │       ├── ScreenshotUtils.java             # Screenshot capture utilities
│       │       ├── ConfigManager.java               # Configuration management
│       │       ├── DriverManager.java               # WebDriver management
│       │       ├── TestData.java                    # Test data constants
│       │       └── TestDataReader.java              # JSON test data reader
│       └── resources/
│           ├── config.properties                    # Application configuration
│           ├── testdata.json                        # JSON test data
│           ├── testng.xml                           # Default TestNG suite
│           ├── smoke-suite.xml                      # Smoke test suite
│           ├── regression-suite.xml                 # Regression test suite
│           └── allure-categories.json               # Allure categories config
├── .github/
│   └── workflows/
│       ├── ci.yml                                   # Main CI/CD pipeline
│       └── qodana_code_quality.yml                  # Code quality workflow
├── Dockerfile                                        # Docker containerization
├── entrypoint.sh                                     # Docker entrypoint script
├── Makefile                                          # Comprehensive test automation
├── pom.xml                                           # Maven configuration
└── qodana.yaml                                       # Qodana configuration
```

---

## 🏁 Getting Started

### Prerequisites

- **Java 21 (LTS)** - [Download](https://adoptium.net/)
- **Maven 3.6+** - [Download](https://maven.apache.org/download.cgi)
- **Chrome browser** (or Firefox)
- **Docker** (optional, for containerized execution)
- **Git** (for version control)

### Installation

```bash
# Clone the repository
git clone https://github.com/yourusername/SwagLabsWithSelenide.git
cd SwagLabsWithSelenide

# Install dependencies
mvn clean install -DskipTests

# Or use Makefile
make install
```

---

## 🧪 Running Tests

### TestNG Test Execution

#### Run All Tests

```bash
# Run all tests with default configuration
mvn clean test

# Run all tests with Makefile
make all-tests
```

#### Run Individual Test Classes

```bash
# Login tests (7 tests - parameterized)
mvn test -Dtest=LoginTest
make login-test

# Products tests (8 tests)
mvn test -Dtest=ProductsTest
make products-test

# Cart tests (9 tests)
mvn test -Dtest=CartTest
make cart-test

# Checkout tests (12 tests)
mvn test -Dtest=CheckoutTest
make checkout-test

# Logout tests (9 tests)
mvn test -Dtest=LogoutTest
make logout-test
```

#### Run Test Suites

```bash
# Smoke tests (Login + Products critical path)
mvn test -DsuiteXmlFile=src/test/resources/smoke-suite.xml
make smoke

# Regression tests (Cart + Checkout comprehensive coverage)
mvn test -DsuiteXmlFile=src/test/resources/regression-suite.xml
make regression
```

#### Run Tests by Categories

```bash
# Critical priority tests
mvn test -Dgroups="critical"
make test-critical

# Validation tests
mvn test -Dgroups="validation"
make test-validation

# UI tests
mvn test -Dgroups="ui"
make test-ui

# Performance tests
mvn test -Dgroups="performance"
make test-performance

# Edge case tests
mvn test -Dgroups="edge-cases"
make test-edge-cases

# Security tests
mvn test -Dgroups="security"
make test-security
```

#### Run Specific Test Methods

```bash
# Specific login validation tests
mvn test -Dtest=LoginTest#testInvalidLogin
mvn test -Dtest=LoginTest#testLoginBoundaryValues
make test-login-validation

# Checkout validation scenarios
mvn test -Dtest=CheckoutTest#testCheckoutValidationEmptyForm
make test-checkout-validation

# Cart operations
mvn test -Dtest=CartTest#testBasicCartOperations
make test-cart-operations

# End-to-end flow
mvn test -Dtest=CheckoutTest#testCompleteE2ECheckoutFlow
make test-e2e-flow
```

### Browser Configuration

```bash
# Chrome (default)
mvn test -Dbrowser=chrome
make chrome-tests

# Firefox
mvn test -Dbrowser=firefox
make firefox-tests

# Headless mode
mvn test -Dbrowser.headless=true
make headless-tests

# Custom browser with specific test
mvn test -Dtest=LoginTest -Dbrowser=firefox -Dbrowser.headless=true
```

### Quick Test Commands

```bash
# Quick smoke test (fastest critical path)
make quick-smoke

# Compile only
make compile

# Clean and rebuild
make clean compile
```

---

## 🐳 Docker Execution

### Build and Run Tests in Docker

```bash
# Build Docker image
docker build -t swaglabs-tests .

# Run smoke tests
docker run --rm \
  -e TEST_TYPE=smoke \
  -e BROWSER=chrome \
  -v $(pwd)/target:/app/target \
  --shm-size=2g \
  swaglabs-tests

# Run regression tests
docker run --rm \
  -e TEST_TYPE=regression \
  -e BROWSER=chrome \
  -v $(pwd)/target:/app/target \
  --shm-size=2g \
  swaglabs-tests

# Run all tests
docker run --rm \
  -e TEST_TYPE=all \
  -e BROWSER=chrome \
  -v $(pwd)/target:/app/target \
  --shm-size=2g \
  swaglabs-tests
```

### Docker with Makefile

```bash
# Run Docker smoke tests
make docker-smoke

# Run Docker regression tests
make docker-regression
```

### Docker Test Parameters

- **TEST_TYPE**: `smoke`, `regression`, `all` (default: `smoke`)
- **BROWSER**: `chrome`, `firefox` (default: `chrome`)

---

## 📊 Generate Reports

### Allure Reports

```bash
# Generate and open Allure report
mvn allure:serve
make report

# Generate report only (without opening)
mvn allure:report
make report-generate

# View generated report
open target/allure-report/index.html
```

### Report Location

- **Allure Results**: `target/allure-results/`
- **Allure Report**: `target/allure-report/`
- **Screenshots**: `target/screenshots/`
- **Surefire Reports**: `target/surefire-reports/`
- **Logs**: `logs/`

---

## 📋 Test Data Management

All test data is centralized and managed through multiple sources:

### Configuration (`config.properties`)

```properties
# Application URL
app.url=https://www.saucedemo.com

# User credentials (6 user types)
user.standard.username=standard_user
user.locked.username=locked_out_user
user.problem.username=problem_user
user.performance.username=performance_glitch_user
user.error.username=error_user
user.visual.username=visual_user

# All users share the same password
*.password=secret_sauce

# Test data
test.firstName=Emmanuel
test.lastName=Arhu
test.postalCode=233
```

### JSON Test Data (`testdata.json`)

- **Users**: Standard, Locked, Problem, Performance glitch users
- **Products**: 6 products with names, prices, descriptions
- **Checkout Data**: Multiple checkout information sets
- **Invalid Login Data**: Various invalid login scenarios with expected errors
- **Sorting Options**: All product sorting combinations

### Data Providers

The framework uses TestNG Data Providers for parameterized testing:

- `TestDataProvider.validLoginData()` - Valid user credentials
- `TestDataProvider.invalidLoginData()` - Invalid login scenarios
- `TestDataProvider.productData()` - Product information
- `TestDataProvider.checkoutData()` - Checkout form data
- `TestDataProvider.sortingData()` - Sorting options

---

## ✅ Features Tested

### Login Functionality (7 Tests)

✅ Valid login with multiple user types 

✅ Invalid login with various error scenarios

✅ Login UI elements and visibility 

✅ Login security validations

✅ Boundary value testing

✅ Locked user scenarios

✅ Empty field validations

### Products Page (8 Tests)

✅ Product catalog display and layout

✅ Product validation (name, price, description)

✅ Product sorting (A-Z, Z-A, price low-high, high-low)

✅ Product navigation and details

✅ Add to cart from products page

✅ Cart badge updates

✅ Product image validation

✅ Inventory persistence

### Shopping Cart (9 Tests)

✅ Basic cart operations (add, remove, continue shopping)

✅ Multiple products cart management

✅ Cart persistence across sessions

✅ Cart validation and item counts

✅ Cart UI elements and layout

✅ Remove items functionality

✅ Continue shopping workflow

✅ Checkout initiation from cart 

✅ Empty cart scenarios

### Checkout Process (12 Tests)

✅ Form validation (empty form, missing fields)

✅ Information page validation

✅ Overview page pricing and tax calculations

✅ Order completion flow

✅ Complete E2E checkout workflow

✅ First name only validation

✅ Last name only validation

✅ Missing postal code validation

✅ Successful order placement

✅ Order confirmation details

✅ Back to products after checkout

✅ Checkout cancellation

### Logout Functionality (9 Tests)

✅ Basic logout functionality

✅ Session cleanup verification

✅ Multi-user logout scenarios

✅ Logout security validations

✅ Logout integration testing

✅ Session expiration handling

✅ Logout from different pages

✅ Re-login after logout

✅ Burger menu navigation

---

## 🤖 CI/CD Integration

### GitHub Actions Workflow

The framework includes a comprehensive CI/CD pipeline with:

#### Features

✅ **Auto-trigger on push** - Pipeline automatically starts on repository changes to main/develop

✅ **Pull request validation** - Automatic test execution on PRs to main

✅ **Scheduled runs** - Daily automated test execution at 2 AM UTC

✅ **Manual triggers** - Workflow dispatch with test type and browser selection

✅ **Parameterized builds** - Choose test type (smoke/regression/all) and browser (chrome/firefox)

✅ **Docker containerized execution** - Isolated test environment with Xvfb display

✅ **Allure reporting** - Comprehensive test reports with screenshots and trends

✅ **GitHub Pages deployment** - Automatic report publishing with historical data

✅ **Email notifications** - Test results sent to configured email address

✅ **Test history tracking** - Historical trends and analytics across builds

✅ **Multi-browser support** - Matrix strategy for browser testing


#### Workflow Triggers

```yaml
# Automatic on push
on:
  push:
    branches: [ main, develop ]

  # Pull request validation
  pull_request:
    branches: [ main ]

  # Scheduled daily runs
  schedule:
    - cron: '0 2 * * *'  # 2 AM UTC daily

  # Manual execution
  workflow_dispatch:
    inputs:
      test_type: [smoke, regression, all]
      browser: [chrome, firefox]
```

#### Pipeline Parameters

- **TEST_TYPE**: `smoke`, `regression`, `all` (default: `smoke`)
- **BROWSER**: `chrome`, `firefox` (default: `chrome`)

#### Notification Configuration

To enable email notifications, configure these GitHub secrets:

- `EMAIL_USERNAME`: SMTP username (Gmail address)
- `EMAIL_PASSWORD`: SMTP password (Gmail app password)
- `NOTIFICATION_EMAIL`: Recipient email address

### Qodana Code Quality

The project includes Qodana integration for static code analysis:

```bash
# Run Qodana locally
docker run --rm -v $(pwd):/data/project jetbrains/qodana-jvm --show-report
```

---

## 📈 Reporting

### Multiple Report Formats

The framework generates comprehensive reports in multiple formats:

#### 1. Allure Reports (Primary)

- **Interactive dashboards** with test execution trends
- **Test categorization** by feature, story, severity
- **Screenshots** on failure with automatic capture
- **Test history** and trends across builds
- **Environment information** and test configuration
- **Duration analysis** and performance metrics
- **Flaky test detection** and analytics
- **Test categories** (passed, failed, broken, skipped)

#### 2. TestNG Reports

- **HTML reports** with test results summary
- **Surefire XML reports** for CI/CD integration
- **Standard test output** with detailed logs

#### 3. Screenshots

- **Automatic capture** on test failure
- **Timestamped filenames** for easy identification
- **Integrated with Allure** for inline viewing

#### 4. Comprehensive Logs

- **Test execution logs** with detailed step information
- **Page action logs** for debugging
- **Console output** for real-time monitoring

---

## 🔧 Configuration

### Browser Configuration

Edit `src/test/resources/config.properties`:

```properties
# Browser settings
browser=chrome
browser.headless=false
browser.size=1920x1080
```

### Application Configuration

```properties
# Application URL
app.url=https://www.saucedemo.com
app.timeout=10000
```

### Maven Configuration

System properties can be passed via command line:

```bash
mvn test -Dbrowser=firefox -Dbrowser.headless=true -Dapp.url=https://www.saucedemo.com
```

### Docker Environment Variables

```bash
docker run --rm \
  -e TEST_TYPE=smoke \
  -e BROWSER=chrome \
  -v $(pwd)/target:/app/target \
  --shm-size=2g \
  swaglabs-tests
```

---

## 🛠 Development and Debugging

### Debug Individual Tests

```bash
# Debug login test with verbose output
mvn test -X -Dtest=LoginTest#testValidLogin
make debug-login

# Debug checkout test
mvn test -X -Dtest=CheckoutTest#testCheckoutValidationEmptyForm
make debug-checkout
```

### Test Verification

```bash
# Verify all test files exist
make verify-tests

# Count tests in each class
make test-count

# Verify test assertions
make verify-assertions

# Verify test structure
make verify-test-structure
```

### Performance Testing

```bash
# Measure login test performance
make perf-login

# Measure checkout test performance
make perf-checkout
```

### CI/CD Simulation

```bash
# Simulate CI smoke pipeline
make ci-smoke

# Simulate CI regression pipeline
make ci-regression

# Simulate full CI pipeline
make ci-full
```

---

## 📊 Test Statistics

### Test Coverage Summary

| Test Class | Test Count | Focus Area | Priority |
|-----------|-----------|-----------|----------|
| **LoginTest** | 7 | Authentication, Security | Critical |
| **ProductsTest** | 8 | Product Catalog, Validation | High |
| **CartTest** | 9 | Cart Management, Persistence | High |
| **CheckoutTest** | 12 | Checkout Flow, Validation | Critical |
| **LogoutTest** | 9 | Session Management | Medium |
| **SmokeTests** | Suite | Critical Path | Critical |
| **RegressionTests** | Suite | Comprehensive Coverage | High |

**Total Tests**: 45+ individual tests across 5 test classes

### Test Categories

- **Critical**: 15+ tests
- **Validation**: 18+ tests
- **UI**: 12+ tests
- **Performance**: 5+ tests
- **Edge Cases**: 8+ tests
- **Security**: 6+ tests

### Test Execution Time (Approximate)

- **Smoke Suite**: ~2-3 minutes
- **Regression Suite**: ~5-7 minutes
- **Full Suite**: ~8-12 minutes
- **Individual Test**: ~10-30 seconds

---

## 🎯 Quick Start Commands Summary

### Local Execution

```bash
# Setup
make install              # Install dependencies
make compile              # Compile test code
make clean               # Clean target directory

# Individual Test Classes
make login-test          # Run LoginTest (7 tests)
make products-test       # Run ProductsTest (8 tests)
make cart-test          # Run CartTest (9 tests)
make checkout-test      # Run CheckoutTest (12 tests)
make logout-test        # Run LogoutTest (9 tests)

# Test Suites
make smoke              # Run smoke tests (critical path)
make regression         # Run regression tests (comprehensive)
make all-tests          # Run all tests

# Test Categories
make test-validation    # All validation tests
make test-critical      # All critical tests
make test-ui           # All UI tests
make test-performance  # All performance tests

# Browser Specific
make chrome-tests       # Run with Chrome
make firefox-tests      # Run with Firefox
make headless-tests    # Run in headless mode

# Reporting
make report            # Generate and open Allure report
make report-generate   # Generate report only

# Quick Commands
make quick-smoke       # Fastest smoke test
make comprehensive     # All individual classes sequentially
```

### Docker Execution

```bash
# Build and run
docker build -t swaglabs-tests .
docker run --rm -e TEST_TYPE=smoke -v $(pwd)/target:/app/target --shm-size=2g swaglabs-tests

# Makefile Docker commands
make docker-smoke
make docker-regression
```

### Maven Direct Commands

```bash
# Basic execution
mvn clean test                                    # All tests
mvn test -Dtest=LoginTest                        # Specific class
mvn test -Dtest=LoginTest#testValidLogin         # Specific method

# With parameters
mvn test -Dbrowser=firefox                       # Firefox browser
mvn test -Dbrowser.headless=true                 # Headless mode
mvn test -Dgroups="critical"                     # Test groups

# Suites
mvn test -DsuiteXmlFile=src/test/resources/smoke-suite.xml
mvn test -DsuiteXmlFile=src/test/resources/regression-suite.xml

# Reports
mvn allure:serve                                 # Generate and open report
mvn allure:report                                # Generate report only
```

---

## 🏗 Architecture Highlights

### Page Object Model

The framework implements a robust Page Object Model with:

- **8 Page Objects**: LoginPage, ProductsPage, ProductDetailsPage, CartPage, CheckoutPage, CheckoutInformationPage, CheckoutOverviewPage, CheckoutCompletePage
- **Fluent API**: Method chaining for readable test code
- **Element encapsulation**: All locators defined in page objects
- **Reusable actions**: Common page actions abstracted

### Base Test Configuration

- **BaseTest.java**: Centralized test setup and teardown
- **Browser management**: Automatic browser initialization
- **Screenshot handling**: Failure screenshots with Allure
- **Configuration loading**: Properties and environment setup

### Test Data Architecture

- **Properties-based**: Configuration in `config.properties`
- **JSON-based**: Complex test data in `testdata.json`
- **Data Providers**: TestNG data providers for parameterization
- **Model classes**: Structured test data models

### Utility Framework

- **ConfigManager**: Centralized configuration management
- **DriverManager**: WebDriver lifecycle management
- **ScreenshotUtils**: Screenshot capture and management
- **AlertHandler**: Alert and dialog handling
- **PopupHandler**: Popup window management
- **TestDataReader**: JSON test data reader

---

## 🔒 Security & Best Practices

### Security Features

✅ No hardcoded credentials in code

✅ Configuration externalized

✅ Secrets management via environment variables

✅ Secure password handling

✅ Session management testing

✅ Security test category

### Best Practices

✅ Page Object Model for maintainability

✅ DRY principle (Don't Repeat Yourself)

✅ Comprehensive test documentation

✅ Meaningful test names and descriptions

✅ Test independence and isolation

✅ Proper exception handling

✅ Resource cleanup in teardown

✅ Version control integration

✅ CI/CD pipeline integration

✅ Code quality analysis with Qodana

---

## 📚 Additional Resources

### Documentation

- [Selenide Documentation](https://selenide.org/)
- [TestNG Documentation](https://testng.org/doc/)
- [Allure Framework](https://docs.qameta.io/allure/)
- [Maven Documentation](https://maven.apache.org/guides/)

### Project Links

- **Application Under Test**: [https://www.saucedemo.com](https://www.saucedemo.com)
- **GitHub Repository**: [Your Repository URL]
- **Test Reports**: [Your GitHub Pages URL]
- **CI/CD Pipeline**: [Your GitHub Actions URL]

---

## 🤝 Contributing

Contributions are welcome! Please follow these guidelines:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## 👤 Author

**Emmanuel Arhu**

- GitHub: [@emmanuelarhu](https://github.com/emmanuelarhu)
- LinkedIn: [Emmanuel Arhu](https://linkedin.com/in/emmanuelarhu)

---

## 🙏 Acknowledgments

- Selenide for the excellent WebDriver wrapper
- TestNG for the powerful testing framework
- Allure for comprehensive reporting
- SwagLabs/Sauce Demo for the test application
- Docker for containerization support

---

## 📞 Support

For issues, questions, or suggestions:

- **Create an Issue**: [GitHub Issues](../../issues)
- **Email**: your.email@example.com
- **Documentation**: Check the [Wiki](../../wiki) for detailed guides

---

**Happy Testing! 🚀**

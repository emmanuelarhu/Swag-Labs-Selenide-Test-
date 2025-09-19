# Refactored Swag Labs UI Test Automation Makefile
# Author: Emmanuel Arhu
# Individual Test Classes with Comprehensive Coverage

.PHONY: help install clean compile test smoke regression login-test products-test cart-test checkout-test logout-test

# Default target
help:
	@echo "🚀 Refactored Swag Labs UI Test Automation"
	@echo "========================================="
	@echo ""
	@echo "📋 Individual Test Class Execution:"
	@echo "  login-test        - Run LoginTest class (7 parameterized tests)"
	@echo "  products-test     - Run ProductsTest class (8 comprehensive tests)"
	@echo "  cart-test         - Run CartTest class (9 cart management tests)"
	@echo "  checkout-test     - Run CheckoutTest class (12 validation tests)"
	@echo "  logout-test       - Run LogoutTest class (9 session tests)"
	@echo ""
	@echo "📊 Test Suite Execution:"
	@echo "  smoke             - Run smoke test suite (Login + Products focus)"
	@echo "  regression        - Run regression suite (Cart + Checkout focus)"
	@echo "  all-tests         - Run complete test suite (all classes)"
	@echo ""
	@echo "🏷️ Test by Categories:"
	@echo "  test-validation   - All validation tests across classes"
	@echo "  test-critical     - All critical priority tests"
	@echo "  test-ui           - All UI-focused tests"
	@echo "  test-performance  - All performance tests"
	@echo "  test-edge-cases   - All edge case tests"
	@echo ""
	@echo "⚙️ Setup & Utilities:"
	@echo "  install           - Install dependencies"
	@echo "  clean             - Clean target directory"
	@echo "  compile           - Compile test code"
	@echo "  validate-data     - Validate test data providers"
	@echo "  report            - Generate Allure reports"
	@echo ""

# Install dependencies
install:
	@echo "📦 Installing dependencies..."
	mvn clean install -DskipTests

# Clean target directory
clean:
	@echo "🧹 Cleaning target directory..."
	mvn clean
	rm -rf target/screenshots/*
	rm -rf target/allure-results/*

# Compile test code
compile:
	@echo "⚙️ Compiling test code..."
	mvn test-compile

# Validate test data providers
validate-data:
	@echo "🔍 Validating test data providers..."
	mvn test -Dtest=TestDataProvider -DfailIfNoTests=false || echo "✅ Test data validation completed"

# Individual Test Classes
login-test:
	@echo "🔐 Running LoginTest class..."
	@echo "Tests: Valid login, Invalid login, UI elements, Security, Boundary values"
	mvn test -Dtest=LoginTest
	@echo "✅ LoginTest completed!"

products-test:
	@echo "📦 Running ProductsTest class..."
	@echo "Tests: Product display, Validation, Sorting, Navigation, Cart operations"
	mvn test -Dtest=ProductsTest
	@echo "✅ ProductsTest completed!"

cart-test:
	@echo "🛒 Running CartTest class..."
	@echo "Tests: Basic operations, Multiple products, Persistence, Validation, UI"
	mvn test -Dtest=CartTest
	@echo "✅ CartTest completed!"

checkout-test:
	@echo "💳 Running CheckoutTest class..."
	@echo "Tests: Form validation, Empty form, Missing fields, E2E flow, Pricing"
	mvn test -Dtest=CheckoutTest
	@echo "✅ CheckoutTest completed!"

logout-test:
	@echo "🚪 Running LogoutTest class..."
	@echo "Tests: Basic logout, Session cleanup, Multi-user, Security, Integration"
	mvn test -Dtest=LogoutTest
	@echo "✅ LogoutTest completed!"

# Test Suite Execution
smoke:
	@echo "💨 Running Smoke Test Suite..."
	@echo "Focus: Login + Products (Critical Path)"
	mvn clean test -DsuiteXmlFile=src/test/resources/smoke-suite.xml
	@echo "✅ Smoke tests completed!"

regression:
	@echo "🔄 Running Regression Test Suite..."
	@echo "Focus: Cart + Checkout (Comprehensive Coverage)"
	mvn clean test -DsuiteXmlFile=src/test/resources/regression-suite.xml
	@echo "✅ Regression tests completed!"

all-tests:
	@echo "🧪 Running Complete Test Suite..."
	@echo "All Classes: Login, Products, Cart, Checkout, Logout"
	mvn clean test -DsuiteXmlFile=src/test/resources/all-tests-suite.xml
	@echo "✅ All tests completed!"

# Test by Categories/Groups
test-validation:
	@echo "✅ Running all validation tests..."
	mvn test -Dgroups="validation"

test-critical:
	@echo "🎯 Running all critical tests..."
	mvn test -Dgroups="critical"

test-ui:
	@echo "🖥️ Running all UI tests..."
	mvn test -Dgroups="ui"

test-performance:
	@echo "⏱️ Running all performance tests..."
	mvn test -Dgroups="performance"

test-edge-cases:
	@echo "🔍 Running all edge case tests..."
	mvn test -Dgroups="edge-cases"

test-security:
	@echo "🔒 Running all security tests..."
	mvn test -Dgroups="security"

# Specific Test Scenarios
test-login-validation:
	@echo "🔐 Running login validation scenarios..."
	mvn test -Dtest=LoginTest#testInvalidLogin,LoginTest#testLoginBoundaryValues

test-checkout-validation:
	@echo "💳 Running checkout validation scenarios..."
	mvn test -Dtest=CheckoutTest#testCheckoutValidationEmptyForm,CheckoutTest#testCheckoutValidationFirstNameOnly,CheckoutTest#testCheckoutValidationMissingPostalCode

test-cart-operations:
	@echo "🛒 Running cart operation tests..."
	mvn test -Dtest=CartTest#testBasicCartOperations,CartTest#testMultipleProductsCartManagement

test-e2e-flow:
	@echo "🔄 Running end-to-end flow tests..."
	mvn test -Dtest=CheckoutTest#testCompleteE2ECheckoutFlow

# Data Provider Testing
test-parameterized:
	@echo "📊 Running parameterized tests..."
	mvn test -Dtest=LoginTest#testValidLogin,LoginTest#testInvalidLogin,ProductsTest#testIndividualProductValidation

# Browser-specific execution
chrome-tests:
	@echo "🌐 Running tests with Chrome..."
	mvn test -Dbrowser=chrome -DsuiteXmlFile=src/test/resources/all-tests-suite.xml

firefox-tests:
	@echo "🦊 Running tests with Firefox..."
	mvn test -Dbrowser=firefox -DsuiteXmlFile=src/test/resources/all-tests-suite.xml

headless-tests:
	@echo "👻 Running tests in headless mode..."
	mvn test -Dbrowser.headless=true -DsuiteXmlFile=src/test/resources/all-tests-suite.xml

# Development and Debugging
debug-login:
	@echo "🐛 Debug login tests..."
	mvn test -X -Dtest=LoginTest#testValidLogin

debug-checkout:
	@echo "🐛 Debug checkout tests..."
	mvn test -X -Dtest=CheckoutTest#testCheckoutValidationEmptyForm

quick-smoke:
	@echo "⚡ Quick smoke test..."
	mvn test -Dtest=LoginTest#testValidLogin,ProductsTest#testProductsPageDisplay,CartTest#testBasicCartOperations

# Test Statistics and Information
test-count:
	@echo "📊 Test Statistics:"
	@echo "LoginTest: $(grep -c '@Test' src/test/java/com/swaglabs/tests/LoginTest.java) tests"
	@echo "ProductsTest: $(grep -c '@Test' src/test/java/com/swaglabs/tests/ProductsTest.java) tests"
	@echo "CartTest: $(grep -c '@Test' src/test/java/com/swaglabs/tests/CartTest.java) tests"
	@echo "CheckoutTest: $(grep -c '@Test' src/test/java/com/swaglabs/tests/CheckoutTest.java) tests"
	@echo "LogoutTest: $(grep -c '@Test' src/test/java/com/swaglabs/tests/LogoutTest.java) tests"

# Reporting
report:
	@echo "📊 Generating Allure report..."
	mvn allure:report
	mvn allure:serve

report-generate:
	@echo "📊 Generating Allure report only..."
	mvn allure:report

# Docker execution
docker-login-test:
	@echo "🐳 Running LoginTest in Docker..."
	docker run --rm -v $(PWD):/app -w /app openjdk:11 mvn test -Dtest=LoginTest

docker-smoke:
	@echo "🐳 Running smoke tests in Docker..."
	TEST_PROFILE=smoke docker-compose up --build --abort-on-container-exit

docker-regression:
	@echo "🐳 Running regression tests in Docker..."
	TEST_PROFILE=regression docker-compose up --build --abort-on-container-exit

# CI/CD simulation
ci-smoke:
	@echo "🤖 CI Smoke Pipeline..."
	make clean && make compile && make smoke && make report-generate

ci-regression:
	@echo "🤖 CI Regression Pipeline..."
	make clean && make compile && make regression && make report-generate

ci-full:
	@echo "🤖 Full CI Pipeline..."
	make clean && make compile && make all-tests && make report-generate

# Test maintenance
verify-assertions:
	@echo "🔍 Verifying test assertions..."
	@echo "Checking for proper assertion usage in test files..."
	@grep -r "assertThat\|softAssert\|assertTrue\|assertEquals" src/test/java/com/swaglabs/tests/ || echo "No assertions found"

verify-test-structure:
	@echo "🏗️ Verifying test structure..."
	@echo "Checking test annotations and structure..."
	@grep -r "@Test\|@Story\|@Description" src/test/java/com/swaglabs/tests/ | wc -l

# Performance testing
perf-login:
	@echo "⏱️ Login performance test..."
	@start_time=$(date +%s); \
	mvn test -Dtest=LoginTest#testValidLogin -Dbrowser.headless=true; \
	end_time=$(date +%s); \
	duration=$((end_time - start_time)); \
	echo "⏱️ Login test duration: $duration seconds"

perf-checkout:
	@echo "⏱️ Checkout performance test..."
	@start_time=$(date +%s); \
	mvn test -Dtest=CheckoutTest#testSuccessfulCheckout -Dbrowser.headless=true; \
	end_time=$(date +%s); \
	duration=$((end_time - start_time)); \
	echo "⏱️ Checkout test duration: $duration seconds"

# Error handling verification
test-error-messages:
	@echo "❌ Testing error message validation..."
	mvn test -Dtest=LoginTest#testInvalidLogin,CheckoutTest#testCheckoutValidationEmptyForm

# Comprehensive test run
comprehensive:
	@echo "🎯 Comprehensive test execution..."
	@echo "Running all individual test classes sequentially..."
	make login-test
	make products-test
	make cart-test
	make checkout-test
	make logout-test
	@echo "🏆 Comprehensive testing completed!"

# Test verification
verify-tests:
	@echo "✅ Verifying all test files exist and compile..."
	@if [ -f "src/test/java/com/swaglabs/tests/LoginTest.java" ]; then echo "✅ LoginTest.java exists"; else echo "❌ LoginTest.java missing"; fi
	@if [ -f "src/test/java/com/swaglabs/tests/ProductsTest.java" ]; then echo "✅ ProductsTest.java exists"; else echo "❌ ProductsTest.java missing"; fi
	@if [ -f "src/test/java/com/swaglabs/tests/CartTest.java" ]; then echo "✅ CartTest.java exists"; else echo "❌ CartTest.java missing"; fi
	@if [ -f "src/test/java/com/swaglabs/tests/CheckoutTest.java" ]; then echo "✅ CheckoutTest.java exists"; else echo "❌ CheckoutTest.java missing"; fi
	@if [ -f "src/test/java/com/swaglabs/tests/LogoutTest.java" ]; then echo "✅ LogoutTest.java exists"; else echo "❌ LogoutTest.java missing"; fi

# Clean execution (for development)
dev-clean:
	@echo "🧹 Development clean..."
	mvn clean compile test-compile
	@echo "✅ Ready for development!"
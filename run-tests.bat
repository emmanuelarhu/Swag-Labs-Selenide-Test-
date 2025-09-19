@echo off
REM SwagLabs Test Runner Script for Windows
REM Usage: run-tests.bat [test-type] [browser] [headless]

REM Default values
if "%1"=="" (set TEST_TYPE=smoke) else (set TEST_TYPE=%1)
if "%2"=="" (set BROWSER=chrome) else (set BROWSER=%2)
if "%3"=="" (set HEADLESS=false) else (set HEADLESS=%3)

echo.
echo 🚀 Starting SwagLabs Test Execution
echo ==================================
echo Test Type: %TEST_TYPE%
echo Browser: %BROWSER%
echo Headless: %HEADLESS%
echo ==================================
echo.

REM Clean previous results
echo 🧹 Cleaning previous test results...
call mvn clean -q

REM Validate configuration
echo 🔍 Validating configuration...
if not exist "src\test\resources\config.properties" (
    echo ❌ ERROR: config.properties not found!
    exit /b 1
)

if not exist "src\test\resources\testng.xml" (
    echo ❌ ERROR: testng.xml not found!
    exit /b 1
)

echo ✅ Configuration validated

REM Set Maven options
set MAVEN_OPTS=-Xmx2g -XX:+UseG1GC

REM Execute tests based on type
if /i "%TEST_TYPE%"=="smoke" (
    echo 🔥 Running Smoke Tests...
    call mvn test -Dtest=SmokeTests -Dbrowser=%BROWSER% -Dheadless=%HEADLESS%
) else if /i "%TEST_TYPE%"=="regression" (
    echo 🔄 Running Regression Tests...
    call mvn test -Dtest=RegressionTests -Dbrowser=%BROWSER% -Dheadless=%HEADLESS%
) else if /i "%TEST_TYPE%"=="login" (
    echo 🔐 Running Login Tests...
    call mvn test -Dtest=LoginTest -Dbrowser=%BROWSER% -Dheadless=%HEADLESS%
) else if /i "%TEST_TYPE%"=="cart" (
    echo 🛒 Running Cart Tests...
    call mvn test -Dtest=CartTest -Dbrowser=%BROWSER% -Dheadless=%HEADLESS%
) else if /i "%TEST_TYPE%"=="checkout" (
    echo 💳 Running Checkout Tests...
    call mvn test -Dtest=CheckoutTest -Dbrowser=%BROWSER% -Dheadless=%HEADLESS%
) else if /i "%TEST_TYPE%"=="all" (
    echo 🎯 Running All Tests...
    call mvn test -Dbrowser=%BROWSER% -Dheadless=%HEADLESS%
) else (
    echo ❌ Unknown test type: %TEST_TYPE%
    echo Valid options: smoke, regression, login, cart, checkout, all
    exit /b 1
)

REM Check test result
set TEST_RESULT=%ERRORLEVEL%

if %TEST_RESULT% equ 0 (
    echo ✅ Tests completed successfully!
) else (
    echo ❌ Tests failed with exit code: %TEST_RESULT%
)

REM Generate Allure report
if exist "target\allure-results" (
    echo 📊 Generating Allure report...
    call mvn allure:report -q

    if %ERRORLEVEL% equ 0 (
        echo 📈 Allure report generated successfully!
        echo 🌐 To view report, run: mvn allure:serve
    ) else (
        echo ⚠️  Failed to generate Allure report
    )
) else (
    echo ⚠️  No test results found for report generation
)

echo.
echo ==================================
echo 🏁 Test execution completed
echo Exit code: %TEST_RESULT%

exit /b %TEST_RESULT%
# Yatra Fare Calendar — Selenium + TestNG Automation Framework

End-to-end UI test automation for [Yatra](https://www.yatra.com)'s fare calendar widget, built with **Selenium WebDriver 4** and **TestNG** using the **Page Object Model**.

[![CI](https://github.com/heiparth07/YatraCalenderAutomation/actions/workflows/ci.yml/badge.svg)](https://github.com/heiparth07/YatraCalenderAutomation/actions)
[![Java](https://img.shields.io/badge/Java-11-blue)]()
[![Selenium](https://img.shields.io/badge/Selenium-4.43-green)]()
[![TestNG](https://img.shields.io/badge/TestNG-7.10-orange)]()

## What it tests

Yatra's flight search exposes a two-month fare calendar widget. This suite validates:

| Test | Assertion |
|------|-----------|
| `fareCalendarShouldDisplayCurrentAndNextMonth` | Both month panels render with distinct labels |
| `eachMonthShouldExposeAtLeastOneFare` | Every month surfaces ≥ 1 parseable fare |
| `allFaresShouldBePositiveIntegers` | No corrupt, negative, or non-numeric fares |
| `lowestFareShouldBeMinOfDisplayedFares` | Lowest-fare extraction matches `min()` of displayed values |
| `crossMonthComparisonShouldIdentifyCheaperMonth` | Cross-month comparison surfaces the cheaper option |

## Framework design

```
src/
├── main/java/com/yatra/automation/pages/
│   └── YatraHomePage.java          # Page Object: locators + interactions
└── test/java/com/yatra/automation/
    ├── base/
    │   └── BaseTest.java           # @BeforeMethod / @AfterMethod driver lifecycle
    └── tests/
        └── FareCalendarTest.java   # @Test methods with TestNG assertions
testng.xml                          # Suite config (parameterized for browser/headless)
pom.xml                             # Maven build with surefire-plugin
.github/workflows/ci.yml            # Headless CI on every push
```

**Key design choices:**

- **Page Object Model** keeps locators in one place; tests describe intent, not DOM detail.
- **Explicit waits only** (`WebDriverWait` + `ExpectedConditions`) — no `Thread.sleep` in test code, so the suite handles network jitter without flakiness or wasted runtime.
- **Independent tests**: every `@Test` gets a fresh browser via `@BeforeMethod`, so failures isolate cleanly and tests can run in any order or in parallel.
- **Parameterized browser/headless** through `testng.xml` so the same suite runs locally with a visible browser and in CI headlessly.
- **CI-ready**: GitHub Actions runs the full suite headless on every push and uploads Surefire reports as artifacts.

## Running locally

```bash
# Run the full suite (Chrome, visible)
mvn test

# Run headless (mirrors CI)
mvn test -Dheadless=true
```

## Tech stack

Java 11 · Selenium WebDriver 4.43 · TestNG 7.10 · Maven · Page Object Model · GitHub Actions

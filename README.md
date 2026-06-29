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
.github/workflows/ci.yml            # Build check on push; live UI suite on demand
```

**Key design choices:**

- **Page Object Model** keeps locators in one place; tests describe intent, not DOM detail.
- **Explicit waits only** (`WebDriverWait` + `ExpectedConditions`) — no `Thread.sleep` in test code, so the suite handles network jitter without flakiness or wasted runtime.
- **Independent tests**: every `@Test` gets a fresh browser via `@BeforeMethod`, so failures isolate cleanly and tests can run in any order or in parallel.
- **Parameterized browser/headless** through `testng.xml` so the same suite runs locally with a visible browser and in CI headlessly.
- **Pragmatic CI design**: every push runs a fast build/compile check; the live UI suite runs on demand (see below).

## Running locally

```bash
# Run the full suite (Chrome, visible)
mvn test

# Run headless (mirrors CI)
mvn test -Dheadless=true
```

## Continuous integration

CI is split into two jobs to keep the pipeline reliable:

- **Build & compile check** — runs on every push and pull request. It compiles
  the main and test sources (`mvn clean test-compile`), so a broken build or
  invalid test code fails fast. Deterministic, no browser, no network.
- **Live UI regression (on-demand)** — the full Selenium suite drives the
  **live yatra.com** site. Because that site applies bot protection and
  region-dependent rendering, the suite is reliable from a local machine but
  flaky on hosted CI runners (datacenter IPs, headless). Rather than gate every
  commit on a third-party site we don't control, this job runs **on demand** via
  the "Run workflow" button and uploads Surefire reports as artifacts.

This mirrors how teams handle end-to-end tests against external sites: keep the
fast, deterministic checks in the commit gate, and run the live, inherently
non-deterministic tests deliberately. The natural next step for full CI
coverage would be to run the suite against a captured snapshot of the page so it
becomes hermetic.

## Tech stack

Java 11 · Selenium WebDriver 4.43 · TestNG 7.10 · Maven · Page Object Model · GitHub Actions

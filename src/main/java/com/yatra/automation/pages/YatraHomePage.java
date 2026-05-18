package com.yatra.automation.pages;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page Object representing the Yatra homepage and its fare calendar widget.
 *
 * The Page Object Model decouples test logic from UI locators: tests describe
 * WHAT to verify, while page objects describe HOW to interact. When Yatra
 * changes a class name or aria-label, only this file needs an update.
 */
public class YatraHomePage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // --- Locators (single source of truth) ---
    private final By departureDateButton =
            By.xpath("//div[@aria-label=\"Departure Date inputbox\" and @role=\"button\"]");

    private final By monthContainers =
            By.xpath("//div[@class=\"react-datepicker__month-container\"]");

    private final By dayPriceSpans =
            By.xpath(".//span[contains(@class, \"custom-day-content\")]");

    private final By currentMonthLabel =
            By.xpath(".//span[@class='react-datepicker__current-month']");

    // Matches a fare span that has actually been populated with text (₹X,XXX).
    // Used to wait out the async fare-data API call after opening the calendar.
    private final By populatedFareSpan =
            By.xpath("//span[contains(@class, 'custom-day-content') and string-length(normalize-space(.)) > 0]");

    public YatraHomePage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    /**
     * Opens the fare calendar and waits until fare data has been fetched.
     *
     * Yatra renders the calendar skeleton synchronously but loads fares via
     * an async API call. We must wait for at least one populated fare cell
     * before any test attempts to read prices — otherwise we race the API.
     */
    public YatraHomePage openFareCalendar() {
        wait.until(ExpectedConditions.elementToBeClickable(departureDateButton)).click();
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(monthContainers, 1));
        wait.until(ExpectedConditions.presenceOfElementLocated(populatedFareSpan));
        return this;
    }

    /** Returns the WebElement for a given month panel (0 = current, 1 = next). */
    public WebElement getMonthPanel(int index) {
        List<WebElement> months =
                wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(monthContainers));
        if (index < 0 || index >= months.size()) {
            throw new IllegalArgumentException(
                    "Month index " + index + " out of range (found " + months.size() + " panels)");
        }
        return months.get(index);
    }

    /** Returns the displayed month name (e.g. "December 2026") for a panel. */
    public String getMonthName(WebElement monthPanel) {
        return wait.until(ExpectedConditions.visibilityOf(
                monthPanel.findElement(currentMonthLabel))).getText();
    }

    /**
     * Extracts every parseable fare value displayed in a month panel.
     * Skips empty cells (days outside the current month or sold-out days).
     */
    public List<Integer> getAllPricesForMonth(WebElement monthPanel) {
        List<Integer> prices = new ArrayList<>();
        List<WebElement> priceCells = monthPanel.findElements(dayPriceSpans);

        for (WebElement cell : priceCells) {
            String raw = cell.getText().trim();
            if (raw.isEmpty()) continue;

            String normalized = raw.replace("₹", "").replace(",", "").trim();
            try {
                prices.add(Integer.parseInt(normalized));
            } catch (NumberFormatException e) {
                // Defensive: skip any cell that doesn't contain a clean numeric fare
            }
        }
        return prices;
    }

    /** Returns the minimum fare in a month panel, or -1 if none found. */
    public int getLowestPrice(WebElement monthPanel) {
        return getAllPricesForMonth(monthPanel).stream()
                .min(Integer::compareTo)
                .orElse(-1);
    }
}
package com.yatra.automation.tests;

import java.util.List;

import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.yatra.automation.base.BaseTest;
import com.yatra.automation.pages.YatraHomePage;

/**
 * Functional regression suite for Yatra's fare calendar.
 *
 * Each test is independent (clean browser per @BeforeMethod) and asserts
 * one specific aspect of the calendar so failures point to a single root cause.
 */
public class FareCalendarTest extends BaseTest {

    @Test(description = "Fare calendar opens and renders two month panels")
    public void fareCalendarShouldDisplayCurrentAndNextMonth() {
        YatraHomePage home = new YatraHomePage(driver, wait).openFareCalendar();

        WebElement currentMonth = home.getMonthPanel(0);
        WebElement nextMonth = home.getMonthPanel(1);

        Assert.assertNotNull(currentMonth, "Current month panel should render");
        Assert.assertNotNull(nextMonth, "Next month panel should render");

        String currentName = home.getMonthName(currentMonth);
        String nextName = home.getMonthName(nextMonth);

        Assert.assertFalse(currentName.isBlank(), "Current month label should not be empty");
        Assert.assertFalse(nextName.isBlank(), "Next month label should not be empty");
        Assert.assertNotEquals(currentName, nextName,
                "Current and next month labels must differ");
    }

    @Test(description = "Each month panel surfaces at least one valid fare")
    public void eachMonthShouldExposeAtLeastOneFare() {
        YatraHomePage home = new YatraHomePage(driver, wait).openFareCalendar();

        for (int i = 0; i <= 1; i++) {
            WebElement panel = home.getMonthPanel(i);
            List<Integer> prices = home.getAllPricesForMonth(panel);
            Assert.assertFalse(prices.isEmpty(),
                    "Month panel index " + i + " should display fares");
        }
    }

    @Test(description = "All displayed fares are positive integers")
    public void allFaresShouldBePositiveIntegers() {
        YatraHomePage home = new YatraHomePage(driver, wait).openFareCalendar();

        for (int i = 0; i <= 1; i++) {
            List<Integer> prices = home.getAllPricesForMonth(home.getMonthPanel(i));
            for (Integer price : prices) {
                Assert.assertTrue(price > 0,
                        "Fare values must be positive (found " + price + ")");
            }
        }
    }

    @Test(description = "Lowest fare extraction returns a value within the month's range")
    public void lowestFareShouldBeMinOfDisplayedFares() {
        YatraHomePage home = new YatraHomePage(driver, wait).openFareCalendar();

        WebElement currentMonth = home.getMonthPanel(0);
        List<Integer> prices = home.getAllPricesForMonth(currentMonth);
        int lowest = home.getLowestPrice(currentMonth);

        Assert.assertNotEquals(lowest, -1, "Lowest price should be found");
        Assert.assertEquals(lowest, (int) prices.stream().min(Integer::compareTo).get(),
                "getLowestPrice() must equal min of all displayed fares");
    }

    @Test(description = "Cross-month comparison surfaces the cheaper of the two months")
    public void crossMonthComparisonShouldIdentifyCheaperMonth() {
        YatraHomePage home = new YatraHomePage(driver, wait).openFareCalendar();

        int currentLowest = home.getLowestPrice(home.getMonthPanel(0));
        int nextLowest = home.getLowestPrice(home.getMonthPanel(1));

        Assert.assertTrue(currentLowest > 0 && nextLowest > 0,
                "Both months must yield a valid lowest fare");

        int overallMin = Math.min(currentLowest, nextLowest);
        System.out.println("Lowest fare across both months: Rs " + overallMin);
    }
}

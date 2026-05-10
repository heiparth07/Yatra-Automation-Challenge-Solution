package com.yatra.automation;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class YatraAutomationScript {
	public static void main(String[] args) throws InterruptedException {
		// Manage your ChromeDriver using ChromeOptions
		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.addArguments("--disable-notifications", "--start-maximized");

		// Launch the Browser!
		WebDriver wd = new ChromeDriver(chromeOptions);
		WebDriverWait wait = new WebDriverWait(wd, Duration.ofSeconds(10)); // Synchronizing the WebDriver!!

		wd.get("https://www.yatra.com");

		// Maximize the window
//		wd.manage().window().maximize(); // Concept of Method-Chaining

		clickOnDepartureDate(wait);

		WebElement currentMonthWebElement = selectTheMonthFromCalender(wait, 0);
		WebElement nextMonthWebElement = selectTheMonthFromCalender(wait, 1);

		Thread.sleep(3000);
		
		String currentMonthLowestPrice = getLowestPriceofMonth(currentMonthWebElement, 0, wait);
		String nextMonthLowestPrice = getLowestPriceofMonth(nextMonthWebElement, 1, wait);
		
		System.out.println(currentMonthLowestPrice);
		System.out.println(nextMonthLowestPrice);
		
		System.out.println();
		
		compareTwoMonthsPrices(currentMonthLowestPrice, nextMonthLowestPrice);
	}

	public static void clickOnDepartureDate(WebDriverWait wait) {
		By departureDateButtonLocator = By.xpath("//div[@aria-label=\"Departure Date inputbox\" and @role=\"button\"]");
		WebElement departureDateButton = wait
				.until(ExpectedConditions.elementToBeClickable(departureDateButtonLocator));
		departureDateButton.click();
	}

	public static String getLowestPriceofMonth(WebElement monthWebElement, int index, WebDriverWait wait) {
		By priceLocator = By.xpath(".//span[contains(@class , \"custom-day-content\")]");
		List<WebElement> monthPricesList = monthWebElement.findElements(priceLocator);
		WebElement monthNameElement = wait.until(
				ExpectedConditions.visibilityOf(
						monthWebElement.findElement(
								By.xpath(".//span[@class='react-datepicker__current-month']"))));

		String monthName = monthNameElement.getText();
		
		String monthType = (index==0) ? "current month" : "next month";
		int lowestPrice = Integer.MAX_VALUE;
		WebElement priceElement = null;
		
		for (WebElement price : monthPricesList) {
			String priceString = price.getText();
			
			if (priceString.length() > 0) {
				priceString = priceString.replace("₹", "").replace(",", "");

				int priceInt = Integer.parseInt(priceString);
				if (priceInt < lowestPrice) {
					lowestPrice = priceInt;
					priceElement = price;
				}
			}
		}

		WebElement dateElement = priceElement.findElement(By.xpath(".//../.."));
		String result = dateElement.getAttribute("aria-label") + " as it has the lowest Price for the " + monthType
				+ "(" + monthName + "): Rs" + lowestPrice;
		return result;
	}

	public static WebElement selectTheMonthFromCalender(WebDriverWait wait, int index) {
		By calenderMonthsLocator = By.xpath("//div[@class = \"react-datepicker__month-container\"]");
		List<WebElement> calenderMonthsList = wait
				.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(calenderMonthsLocator));
		WebElement monthCalenderWebElement = calenderMonthsList.get(index);
		return monthCalenderWebElement;
	}
	
	public static void compareTwoMonthsPrices(String currentMonthPrice, String nextMonthPrice) {
		int currentMonthPriceRsIndex = currentMonthPrice.indexOf("Rs");
		int nextMonthPriceRsIndex = nextMonthPrice.indexOf("Rs");
		
		String currentMonthLowestPrice = currentMonthPrice.substring(currentMonthPriceRsIndex+2);
		String nextMonthLowestPrice = nextMonthPrice.substring(nextMonthPriceRsIndex+2);
		
		int intCurrentMonthLowestPrice = Integer.parseInt(currentMonthLowestPrice);
		int intNextMonthLowestPrice = Integer.parseInt(nextMonthLowestPrice);
		
		if (intCurrentMonthLowestPrice < intNextMonthLowestPrice) {
			System.out.println("The current month has the lowest price of the 2 months: Rs" + intCurrentMonthLowestPrice);
		}
		else if (intCurrentMonthLowestPrice == intNextMonthLowestPrice) {
			System.out.println("Both the current as well as the next month has the same lowest price: Rs" + intCurrentMonthLowestPrice);
		}
		else {
			System.out.println("The next month has the lowest price of the 2 months: Rs" + nextMonthLowestPrice);
		}
	}
}

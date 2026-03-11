package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * ═══════════════════════════════════════════════════════════════ LAYER 1:
 * UTILITY LAYER (Generic Layer)
 * ═══════════════════════════════════════════════════════════════
 *
 * This layer contains all customized and reusable common methods. All methods
 * are INDEPENDENT and wrapped in try-catch to: → Handle runtime exceptions →
 * Improve script stability → Make debugging easier → Increase traceability when
 * a failure occurs
 *
 * OOP Concepts used: → Encapsulation : driver is private, accessed via
 * getDriver() → Polymorphism : method overloading for frames, waits, Actions
 */
public class WebUtil {

	// Encapsulation — driver is private, accessed via getDriver()
	private WebDriver driver;
	private ExtentTest et;

	public WebUtil(ExtentTest et) {
		this.et = et;
	}

	/**
	 * Data Hiding — controlled access to private driver variable
	 */
	public WebDriver getDriver() {
		return driver;
	}

	// =========================================================================
	// BROWSER MANAGEMENT METHODS
	// =========================================================================

	public WebDriver launchBrowser(String browserName, int timeInSeconds) {
		try {
			switch (browserName.toLowerCase()) {
			case "chrome":
				WebDriverManager.chromedriver().setup();
				ChromeOptions chromeOptions = new ChromeOptions();
				chromeOptions.addArguments("--start-maximized");
				chromeOptions.addArguments("--disable-notifications");
				driver = new ChromeDriver(chromeOptions);
				break;
			case "firefox":
				WebDriverManager.firefoxdriver().setup();
				driver = new FirefoxDriver();
				break;
			case "edge":
				WebDriverManager.edgedriver().setup();
				driver = new EdgeDriver();
				break;
			default:
				et.log(Status.FAIL, "Invalid browser name: " + browserName);
				throw new IllegalArgumentException("Invalid browser name: " + browserName);
			}
			driver.manage().window().maximize();
			driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(timeInSeconds));
			driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
			et.log(Status.INFO, browserName + " browser launched successfully");
		} catch (Exception e) {
			et.log(Status.FAIL, "Failed to launch browser: " + e.getMessage());
			throw e;
		}
		return driver;
	}

	public void closeBrowser() {
		try {
			if (driver != null) {
				driver.close();
				et.log(Status.INFO, "Current browser tab closed successfully.");
			}
		} catch (WebDriverException e) {
			et.log(Status.FAIL, "WebDriverException while closing browser: " + e.getMessage());
		} catch (Exception e) {
			et.log(Status.FAIL, "Unexpected exception while closing browser: " + e.getMessage());
		}
	}

	public void quitBrowser() {
		try {
			if (driver != null) {
				driver.quit();
				et.log(Status.INFO, "Browser session quit successfully.");
			}
		} catch (WebDriverException e) {
			et.log(Status.FAIL, "WebDriverException while quitting browser: " + e.getMessage());
		} catch (Exception e) {
			et.log(Status.FAIL, "Unexpected exception while quitting browser: " + e.getMessage());
		}
	}

	// =========================================================================
	// NAVIGATION METHODS
	// =========================================================================

	public void openURL(String url) {
		try {
			driver.get(url);
			et.log(Status.INFO, "URL opened successfully: " + url);
		} catch (Exception e) {
			et.log(Status.FAIL, "Failed to open URL: " + url + ". Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public String getPageTitle() {
		try {
			String title = driver.getTitle();
			et.log(Status.INFO, "Page title retrieved: " + title);
			return title;
		} catch (Exception e) {
			et.log(Status.FAIL, "Failed to retrieve page title: " + e.getMessage());
			return null;
		}
	}

	public String getPageURL() {
		try {
			String url = driver.getCurrentUrl();
			et.log(Status.INFO, "Current page URL: " + url);
			return url;
		} catch (Exception e) {
			et.log(Status.FAIL, "Failed to retrieve page URL: " + e.getMessage());
			return null;
		}
	}

	// =========================================================================
	// ELEMENT LOCATION METHODS
	// =========================================================================

	public WebElement searchElement(String xpath, String element) {
		WebElement we = null;
		try {
			we = driver.findElement(By.xpath(xpath));
			et.log(Status.PASS, element + " found successfully");
		} catch (org.openqa.selenium.NoSuchElementException e) {
			et.log(Status.WARNING, element + " not found on first attempt, retrying after wait...");
			try {
				Thread.sleep(3000);
				we = driver.findElement(By.xpath(xpath));
				et.log(Status.PASS, element + " found successfully after retry");
			} catch (org.openqa.selenium.NoSuchElementException e2) {
				et.log(Status.FAIL, element + " not found after retry");
				e2.printStackTrace();
				throw e2;
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
				ie.printStackTrace();
			}
		} catch (InvalidSelectorException e) {
			et.log(Status.FAIL, element + " has invalid XPath syntax: " + e.getMessage());
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			et.log(Status.FAIL, "Exception while searching for " + element + ": " + e.getMessage());
			e.printStackTrace();
			throw e;
		}
		return we;
	}

	public boolean isElementPresent(By locator) {
		try {
			driver.findElement(locator);
			return true;
		} catch (org.openqa.selenium.NoSuchElementException e) {
			return false;
		}
	}

	// =========================================================================
	// ELEMENT INTERACTION METHODS
	// =========================================================================

	public void type(WebElement we, String value, String element) {
		try {
			we.sendKeys(value);
			et.log(Status.INFO, element + " - entered '" + value + "' successfully");
		} catch (ElementNotInteractableException e) {
			try {
				JavascriptExecutor jse = (JavascriptExecutor) driver;
				jse.executeScript("arguments[0].value=arguments[1];", we, value);
				et.log(Status.INFO, element + " - entered '" + value + "' via JavaScriptExecutor");
			} catch (Exception jsEx) {
				et.log(Status.FAIL, "JS typing failed on " + element + ". Error: " + jsEx.getMessage());
				jsEx.printStackTrace();
				throw jsEx;
			}
		} catch (Exception e) {
			et.log(Status.FAIL, "Typing failed on " + element + ". Error: " + e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}

	public void clearAndType(WebElement we, String value, String element) {
		try {
			we.clear();
			we.sendKeys(value);
			et.log(Status.INFO, element + " - cleared and entered '" + value + "' successfully");
		} catch (Exception e) {
			et.log(Status.FAIL, "clearAndType failed on " + element + ". Error: " + e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}

	public void click(WebElement we, String element) {
		try {
			we.click();
			et.log(Status.INFO, element + " clicked successfully");
		} catch (ElementClickInterceptedException e) {
			try {
				WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
				wait.until(ExpectedConditions.elementToBeClickable(we));
				((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", we);
				we.click();
				et.log(Status.INFO, element + " clicked successfully after scroll");
			} catch (Exception retryEx) {
				et.log(Status.FAIL, "Click retry failed on " + element + ": " + retryEx.getMessage());
				throw retryEx;
			}
		} catch (ElementNotInteractableException e) {
			try {
				JavascriptExecutor jse = (JavascriptExecutor) driver;
				jse.executeScript("arguments[0].click();", we);
				et.log(Status.INFO, element + " clicked via JavaScriptExecutor");
			} catch (Exception jsEx) {
				et.log(Status.FAIL, "JS click failed on " + element + ". Error: " + jsEx.getMessage());
				jsEx.printStackTrace();
				throw jsEx;
			}
		} catch (Exception e) {
			et.log(Status.FAIL, "Click failed on " + element + ". Error: " + e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}

	// =========================================================================
	// JAVASCRIPT METHODS
	// =========================================================================

	public void jsClick(WebElement we, String element) {
		try {
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", we);
			et.log(Status.INFO, element + " clicked successfully by JavaScriptExecutor");
		} catch (Exception e) {
			et.log(Status.FAIL, "Failed to JS click " + element + ". Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void jsType(WebElement we, String value, String element) {
		try {
			((JavascriptExecutor) driver).executeScript("arguments[0].value=arguments[1];", we, value);
			et.log(Status.INFO, element + " typed '" + value + "' successfully by JavaScriptExecutor");
		} catch (Exception e) {
			et.log(Status.FAIL, "Failed to JS type on " + element + ". Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void jsScrollToBottom() {
		try {
			((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
			et.log(Status.INFO, "Scrolled to bottom of page successfully.");
		} catch (Exception e) {
			et.log(Status.FAIL, "Failed to scroll to bottom. Error: " + e.getMessage());
		}
	}

	public void jsScrollByAmount(int x, int y) {
		try {
			((JavascriptExecutor) driver).executeScript("window.scrollBy(arguments[0], arguments[1]);", x, y);
			et.log(Status.INFO, "JS scrolled by X:" + x + ", Y:" + y);
		} catch (Exception e) {
			et.log(Status.FAIL, "Failed to JS scroll by amount. Error: " + e.getMessage());
		}
	}

	public void jsScrollToElement(WebElement we, String elementName) {
		try {
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", we);
			et.log(Status.INFO, "JS scrolled to element: " + elementName);
		} catch (Exception e) {
			et.log(Status.FAIL, "Failed to JS scroll to " + elementName + ". Error: " + e.getMessage());
		}
	}

	// =========================================================================
	// SELECT DROPDOWN METHODS
	// =========================================================================

	public void selectTextFromListBox(WebElement we, String selectText, String elementName) {
		try {
			new Select(we).selectByVisibleText(selectText);
			et.log(Status.INFO, elementName + " selected by text: '" + selectText + "' successfully");
		} catch (Exception e) {
			et.log(Status.FAIL, "Failed to select by text on " + elementName + ". Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void selectTextFromListBoxByIndex(WebElement we, int index, String elementName) {
		try {
			new Select(we).selectByIndex(index);
			et.log(Status.INFO, elementName + " selected by index: " + index + " successfully");
		} catch (Exception e) {
			et.log(Status.FAIL, "Failed to select by index on " + elementName + ". Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void selectTextByValue(WebElement we, String value, String elementName) {
		try {
			new Select(we).selectByValue(value);
			et.log(Status.INFO, elementName + " selected by value: '" + value + "' successfully");
		} catch (Exception e) {
			et.log(Status.FAIL, "Failed to select by value on " + elementName + ". Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public String getSelectedDropdownText(WebElement we, String elementName) {
		try {
			String selected = new Select(we).getFirstSelectedOption().getText();
			et.log(Status.INFO, elementName + " currently selected: '" + selected + "'");
			return selected;
		} catch (Exception e) {
			et.log(Status.FAIL, "Failed to get selected text from " + elementName + ". Error: " + e.getMessage());
			return null;
		}
	}

	// =========================================================================
	// ACTIONS CLASS METHODS
	// Method Overloading used here (Polymorphism)
	// =========================================================================

	public void rightClick(WebElement we, String elementName) {
		try {
			new Actions(driver).contextClick(we).build().perform();
			et.log(Status.PASS, "Right-clicked on: " + elementName);
		} catch (Exception e) {
			et.log(Status.FAIL, "Right-click failed on " + elementName + ". Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void doubleClick(WebElement we, String elementName) {
		try {
			new Actions(driver).doubleClick(we).build().perform();
			et.log(Status.INFO, "Double-clicked on: " + elementName);
		} catch (Exception e) {
			et.log(Status.FAIL, "Double-click failed on " + elementName + ". Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void moveToElement(WebElement we, String elementName) {
		try {
			new Actions(driver).moveToElement(we).build().perform();
			et.log(Status.PASS, "Hovered over: " + elementName);
		} catch (Exception e) {
			et.log(Status.FAIL, "Hover failed on " + elementName + ". Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void dragAndDrop(WebElement source, WebElement target, String sourceName, String targetName) {
		try {
			new Actions(driver).dragAndDrop(source, target).build().perform();
			et.log(Status.INFO, "Dragged '" + sourceName + "' to '" + targetName + "' successfully");
		} catch (Exception e) {
			et.log(Status.FAIL, "Drag and drop failed. Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	// Overloaded — scroll by pixel amount
	public void scrollByAmount(int x, int y) {
		try {
			new Actions(driver).scrollByAmount(x, y).build().perform();
			et.log(Status.PASS, "Scrolled by X:" + x + ", Y:" + y);
		} catch (Exception e) {
			et.log(Status.FAIL, "Scroll by amount failed. Error: " + e.getMessage());
		}
	}

	// Overloaded — scroll to a specific element
	public void scrollToElement(WebElement we, String elementName) {
		try {
			new Actions(driver).scrollToElement(we).build().perform();
			et.log(Status.INFO, "Scrolled to element: " + elementName);
		} catch (Exception e) {
			et.log(Status.FAIL, "Scroll to element failed for " + elementName + ". Error: " + e.getMessage());
		}
	}

	// =========================================================================
	// TEXT AND ATTRIBUTE METHODS
	// =========================================================================

	public String getInnerText(WebElement we, String elementName) {
		try {
			String text = we.getText().trim();
			et.log(Status.INFO, elementName + " inner text: '" + text + "'");
			return text;
		} catch (Exception e) {
			et.log(Status.FAIL, "Failed to get text from " + elementName + ". Error: " + e.getMessage());
			return null;
		}
	}

	public String getAttributeValue(WebElement we, String attributeName, String elementName) {
		try {
			String value = we.getDomAttribute(attributeName);
			et.log(Status.INFO, elementName + " [" + attributeName + "] = '" + value + "'");
			return value;
		} catch (Exception e) {
			et.log(Status.FAIL, "Failed to get attribute from " + elementName + ". Error: " + e.getMessage());
			return null;
		}
	}

	public List<String> getAllElementsText(String xpath) {
		List<String> textList = new ArrayList<>();
		try {
			List<WebElement> elements = driver.findElements(By.xpath(xpath));
			if (elements.isEmpty()) {
				et.log(Status.WARNING, "No elements found for XPath: " + xpath);
			} else {
				for (WebElement el : elements)
					textList.add(el.getText().trim());
				et.log(Status.PASS, "Found " + elements.size() + " elements. Texts extracted.");
			}
		} catch (Exception e) {
			et.log(Status.FAIL, "getAllElementsText failed. Error: " + e.getMessage());
		}
		return textList;
	}

	public void clickAllElements(String xpath) {
		try {
			List<WebElement> list = driver.findElements(By.xpath(xpath));
			for (WebElement we : list)
				we.click();
			et.log(Status.INFO, "Clicked all " + list.size() + " elements at XPath: " + xpath);
		} catch (Exception e) {
			et.log(Status.FAIL, "clickAllElements failed. Error: " + e.getMessage());
		}
	}

	// =========================================================================
	// WINDOW HANDLING METHODS
	// =========================================================================

	public void switchToWindowByUrl(String expectedURL) {
		boolean isSwitched = false;
		for (String handle : driver.getWindowHandles()) {
			driver.switchTo().window(handle);
			if (driver.getCurrentUrl().equalsIgnoreCase(expectedURL)) {
				et.log(Status.INFO, "Switched to window with URL: " + expectedURL);
				isSwitched = true;
				break;
			}
		}
		if (!isSwitched)
			et.log(Status.FAIL, "No window found with URL: " + expectedURL);
	}

	public void switchToWindowByTitle(String expectedTitle) {
		boolean isSwitched = false;
		for (String handle : driver.getWindowHandles()) {
			driver.switchTo().window(handle);
			if (driver.getTitle().equalsIgnoreCase(expectedTitle)) {
				et.log(Status.INFO, "Switched to window with title: " + expectedTitle);
				isSwitched = true;
				break;
			}
		}
		if (!isSwitched)
			et.log(Status.FAIL, "No window found with title: " + expectedTitle);
	}

	public void switchToWindowByIndex(int index) {
		List<String> handles = new ArrayList<>(driver.getWindowHandles());
		if (index < 0 || index >= handles.size()) {
			et.log(Status.FAIL, "Invalid window index: " + index + " (total: " + handles.size() + ")");
			return;
		}
		driver.switchTo().window(handles.get(index));
		et.log(Status.INFO, "Switched to window at index: " + index);
	}

	public void closeAllChildWindowsAndSwitchToParent() {
		String parentHandle = driver.getWindowHandle();
		Set<String> handles = driver.getWindowHandles();
		for (String handle : handles) {
			if (!handle.equals(parentHandle)) {
				driver.switchTo().window(handle);
				driver.close();
				et.log(Status.INFO, "Closed child window: " + handle);
			}
		}
		driver.switchTo().window(parentHandle);
		et.log(Status.INFO, "Switched back to parent window.");
	}

	// =========================================================================
	// FRAME HANDLING METHODS
	// Method Overloading (Polymorphism) — 3 overloaded versions
	// =========================================================================

	// Overload 1: switch by index
	public void switchToFrame(int index) {
		try {
			driver.switchTo().frame(index);
			et.log(Status.INFO, "Switched to frame at index: " + index);
		} catch (NoSuchFrameException e) {
			et.log(Status.FAIL, "No frame found at index: " + index + ". Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	// Overload 2: switch by name or id
	public void switchToFrame(String nameOrId) {
		try {
			driver.switchTo().frame(nameOrId);
			et.log(Status.INFO, "Switched to frame: " + nameOrId);
		} catch (Exception e) {
			et.log(Status.FAIL, "Failed to switch to frame '" + nameOrId + "'. Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	// Overload 3: switch by WebElement
	public void switchToFrame(WebElement frameElement) {
		try {
			driver.switchTo().frame(frameElement);
			et.log(Status.INFO, "Switched to frame via WebElement");
		} catch (NoSuchFrameException e) {
			et.log(Status.FAIL, "Frame not found via WebElement. Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void switchToDefaultContent() {
		try {
			driver.switchTo().defaultContent();
			et.log(Status.INFO, "Switched back to default content.");
		} catch (Exception e) {
			et.log(Status.FAIL, "Failed to switch to default content. Error: " + e.getMessage());
		}
	}

	// =========================================================================
	// WAIT METHODS
	// Method Overloading (Polymorphism) — multiple overloaded versions
	// =========================================================================

	public void staticWait(int timeInSeconds) {
		try {
			Thread.sleep(timeInSeconds * 1000L);
			et.log(Status.INFO, "Static wait applied for " + timeInSeconds + " seconds.");
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			et.log(Status.FAIL, "Static wait interrupted. Error: " + e.getMessage());
		}
	}

	// Overload 1: wait for visibility of WebElement
	public void waitForVisibility(WebElement we, int timeoutSeconds) {
		try {
			new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds)).until(ExpectedConditions.visibilityOf(we));
			et.log(Status.INFO, "Element visible within " + timeoutSeconds + "s");
		} catch (Exception e) {
			et.log(Status.FAIL, "Element not visible within " + timeoutSeconds + "s. Error: " + e.getMessage());
			throw e;
		}
	}

	// Overload 2: wait for visibility by locator
	public void waitForVisibility(By locator, int timeoutSeconds) {
		try {
			new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
					.until(ExpectedConditions.visibilityOfElementLocated(locator));
			et.log(Status.INFO, "Element (by locator) visible within " + timeoutSeconds + "s");
		} catch (Exception e) {
			et.log(Status.FAIL, "Element not visible within " + timeoutSeconds + "s. Error: " + e.getMessage());
			throw e;
		}
	}

	// Overload 1: wait for clickability of WebElement
	public void waitForClickability(WebElement we, int timeoutSeconds) {
		try {
			new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
					.until(ExpectedConditions.elementToBeClickable(we));
			et.log(Status.INFO, "Element clickable within " + timeoutSeconds + "s");
		} catch (Exception e) {
			et.log(Status.FAIL, "Element not clickable within " + timeoutSeconds + "s. Error: " + e.getMessage());
			throw e;
		}
	}

	// Overload 2: wait for clickability by locator
	public void waitForClickability(By locator, int timeoutSeconds) {
		try {
			new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
					.until(ExpectedConditions.elementToBeClickable(locator));
			et.log(Status.INFO, "Element (by locator) clickable within " + timeoutSeconds + "s");
		} catch (Exception e) {
			et.log(Status.FAIL, "Element not clickable within " + timeoutSeconds + "s. Error: " + e.getMessage());
			throw e;
		}
	}

	public void waitForText(WebElement we, String expectedText, int timeoutSeconds) {
		try {
			new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
					.until(ExpectedConditions.textToBePresentInElement(we, expectedText));
			et.log(Status.INFO, "Text '" + expectedText + "' present within " + timeoutSeconds + "s");
		} catch (Exception e) {
			et.log(Status.FAIL, "Text '" + expectedText + "' not found within " + timeoutSeconds + "s");
			throw e;
		}
	}

	public void waitForInvisibility(WebElement we, int timeoutSeconds) {
		try {
			new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds)).until(ExpectedConditions.invisibilityOf(we));
			et.log(Status.INFO, "Element invisible within " + timeoutSeconds + "s");
		} catch (Exception e) {
			et.log(Status.FAIL, "Element still visible after " + timeoutSeconds + "s. Error: " + e.getMessage());
			throw e;
		}
	}

	public void changePageLoadTimeout(int timeoutSeconds) {
		try {
			driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(timeoutSeconds));
			et.log(Status.INFO, "Page load timeout changed to " + timeoutSeconds + "s");
		} catch (Exception e) {
			et.log(Status.FAIL, "Failed to set page load timeout. Error: " + e.getMessage());
		}
	}

	// =========================================================================
	// WINDOW SIZE METHODS
	// =========================================================================

	public void maximizeWindow() {
		try {
			driver.manage().window().maximize();
			et.log(Status.INFO, "Browser window maximized.");
		} catch (Exception e) {
			et.log(Status.FAIL, "Failed to maximize window. Error: " + e.getMessage());
		}
	}

	public void setWindowSize(int width, int height) {
		try {
			driver.manage().window().setSize(new Dimension(width, height));
			et.log(Status.INFO, "Window size set to " + width + "x" + height);
		} catch (Exception e) {
			et.log(Status.FAIL, "Failed to set window size. Error: " + e.getMessage());
		}
	}

	// =========================================================================
	// ELEMENT STATUS METHODS
	// =========================================================================

	public boolean isElementDisplayed(WebElement we, String elementName) {
		try {
			boolean displayed = we.isDisplayed();
			if (displayed) {
				et.log(Status.PASS, elementName + " is displayed ✔");
			} else {
				et.log(Status.FAIL, elementName + " is NOT displayed ✘");
			}
			return displayed;
		} catch (Exception e) {
			et.log(Status.FAIL, "Exception checking display of " + elementName + ": " + e.getMessage());
			return false;
		}
	}

	public boolean isElementEnabled(WebElement we, String elementName) {
		try {
			boolean enabled = we.isEnabled();
			if (enabled) {
				et.log(Status.PASS, elementName + " is enabled ✔");
			} else {
				et.log(Status.FAIL, elementName + " is NOT enabled ✘");
			}
			return enabled;
		} catch (Exception e) {
			et.log(Status.FAIL, "Exception checking enabled state of " + elementName + ": " + e.getMessage());
			return false;
		}
	}

	// =========================================================================
	// VALIDATION METHODS
	// =========================================================================

	public void validateInnerText(WebElement we, String expectedText, String elementName) {
		try {
			String actualText = we.getText().trim();
			if (actualText.equalsIgnoreCase(expectedText)) {
				et.log(Status.PASS, "✔ " + elementName + " text validated | Expected: '" + expectedText
						+ "' | Actual: '" + actualText + "'");
			} else {
				et.log(Status.FAIL, "✘ " + elementName + " text mismatch | Expected: '" + expectedText + "' | Actual: '"
						+ actualText + "'");
			}
		} catch (Exception e) {
			et.log(Status.FAIL, "Exception validating text of " + elementName + ": " + e.getMessage());
		}
	}

	public void validateAttribute(WebElement we, String attributeName, String expectedValue, String elementName) {
		try {
			String actualValue = we.getDomAttribute(attributeName);
			if (actualValue != null && actualValue.equalsIgnoreCase(expectedValue)) {
				et.log(Status.PASS,
						"✔ " + elementName + " [" + attributeName + "] validated | Expected: '" + expectedValue + "'");
			} else {
				et.log(Status.FAIL, "✘ " + elementName + " [" + attributeName + "] mismatch | Expected: '"
						+ expectedValue + "' | Actual: '" + actualValue + "'");
			}
		} catch (Exception e) {
			et.log(Status.FAIL, "Exception validating attribute of " + elementName + ": " + e.getMessage());
		}
	}

	public void validateElementVisible(WebElement we, String elementName) {
		try {
			if (we.isDisplayed()) {
				et.log(Status.PASS, "✔ " + elementName + " is visible on the page.");
			} else {
				et.log(Status.FAIL, "✘ " + elementName + " is NOT visible on the page.");
			}
		} catch (Exception e) {
			et.log(Status.FAIL, "Exception verifying visibility of " + elementName + ": " + e.getMessage());
		}
	}

	public void validateElementInvisible(WebElement we, String elementName) {
		try {
			if (!we.isDisplayed()) {
				et.log(Status.PASS, "✔ " + elementName + " is correctly not visible.");
			} else {
				et.log(Status.FAIL, "✘ " + elementName + " is still visible (expected hidden).");
			}
		} catch (org.openqa.selenium.NoSuchElementException e) {
			// Not in DOM = invisible = PASS
			et.log(Status.PASS, "✔ " + elementName + " not present in DOM (correctly invisible).");
		} catch (Exception e) {
			et.log(Status.FAIL, "Exception verifying invisibility of " + elementName + ": " + e.getMessage());
		}
	}

	public void validateElementEnabled(WebElement we, String elementName) {
		try {
			if (we.isEnabled()) {
				et.log(Status.PASS, "✔ " + elementName + " is enabled.");
			} else {
				et.log(Status.FAIL, "✘ " + elementName + " is NOT enabled (expected enabled).");
			}
		} catch (Exception e) {
			et.log(Status.FAIL, "Exception checking enabled state of " + elementName + ": " + e.getMessage());
		}
	}

	public void validateElementDisabled(WebElement we, String elementName) {
		try {
			if (!we.isEnabled()) {
				et.log(Status.PASS, "✔ " + elementName + " is correctly disabled.");
			} else {
				et.log(Status.FAIL, "✘ " + elementName + " is enabled (expected disabled).");
			}
		} catch (Exception e) {
			et.log(Status.FAIL, "Exception checking disabled state of " + elementName + ": " + e.getMessage());
		}
	}

	public void validatePageTitle(String expectedTitle) {
		try {
			String actualTitle = driver.getTitle();
			if (actualTitle.equalsIgnoreCase(expectedTitle)) {
				et.log(Status.PASS,
						"✔ Page title validated | Expected: '" + expectedTitle + "' | Actual: '" + actualTitle + "'");
			} else {
				et.log(Status.FAIL,
						"✘ Page title mismatch | Expected: '" + expectedTitle + "' | Actual: '" + actualTitle + "'");
			}
		} catch (Exception e) {
			et.log(Status.FAIL, "Exception validating page title: " + e.getMessage());
		}
	}

	public void validatePageURL(String expectedURL) {
		try {
			String actualURL = driver.getCurrentUrl();
			if (actualURL.contains(expectedURL)) {
				et.log(Status.PASS, "✔ Page URL validated | Expected contains: '" + expectedURL + "'");
			} else {
				et.log(Status.FAIL,
						"✘ Page URL mismatch | Expected: '" + expectedURL + "' | Actual: '" + actualURL + "'");
			}
		} catch (Exception e) {
			et.log(Status.FAIL, "Exception validating page URL: " + e.getMessage());
		}
	}

	public void validateDropdownSelectedText(WebElement we, String expectedText, String elementName) {
		try {
			String actualText = new Select(we).getFirstSelectedOption().getText();
			if (actualText.equalsIgnoreCase(expectedText)) {
				et.log(Status.PASS, "✔ " + elementName + " dropdown validated | Selected: '" + actualText + "'");
			} else {
				et.log(Status.FAIL, "✘ " + elementName + " dropdown mismatch | Expected: '" + expectedText
						+ "' | Actual: '" + actualText + "'");
			}
		} catch (Exception e) {
			et.log(Status.FAIL, "Exception validating dropdown of " + elementName + ": " + e.getMessage());
		}
	}

	// =========================================================================
	// ALERT HANDLING METHODS
	// =========================================================================

	public void alertAccept() {
		try {
			Alert alert = driver.switchTo().alert();
			et.log(Status.INFO, "Alert text: " + alert.getText());
			alert.accept();
			et.log(Status.PASS, "Alert accepted successfully ✔");
		} catch (NoAlertPresentException e) {
			et.log(Status.FAIL, "No alert present to accept.");
		} catch (Exception e) {
			et.log(Status.FAIL, "Failed to accept alert. Error: " + e.getMessage());
		}
	}

	public void alertDismiss() {
		try {
			driver.switchTo().alert().dismiss();
			et.log(Status.PASS, "Alert dismissed successfully ✔");
		} catch (NoAlertPresentException e) {
			et.log(Status.FAIL, "No alert present to dismiss.");
		} catch (Exception e) {
			et.log(Status.FAIL, "Failed to dismiss alert. Error: " + e.getMessage());
		}
	}

	public String getAlertText() {
		try {
			String text = driver.switchTo().alert().getText();
			et.log(Status.INFO, "Alert text: " + text);
			return text;
		} catch (NoAlertPresentException e) {
			et.log(Status.FAIL, "No alert present — cannot get text.");
			return null;
		} catch (Exception e) {
			et.log(Status.FAIL, "Failed to get alert text. Error: " + e.getMessage());
			return null;
		}
	}

	public void alertSendKeys(String text) {
		try {
			driver.switchTo().alert().sendKeys(text);
			et.log(Status.INFO, "Sent '" + text + "' to alert.");
		} catch (NoAlertPresentException e) {
			et.log(Status.FAIL, "No alert present to send keys.");
		} catch (Exception e) {
			et.log(Status.FAIL, "Failed to send keys to alert. Error: " + e.getMessage());
		}
	}

	// =========================================================================
	// SCREENSHOT METHODS
	// =========================================================================

	public String takeScreenshot(String screenshotName) {
		try {
			String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
			String fileName = screenshotName + "_" + timestamp + ".png";
			String filePath = "screenshots/" + fileName;

			File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			File destFile = new File(filePath);
			destFile.getParentFile().mkdirs();

			FileUtils.copyFile(srcFile, destFile);
			et.log(Status.INFO, "Screenshot saved: " + filePath);
			return filePath;
		} catch (IOException e) {
			et.log(Status.FAIL, "Screenshot failed. Error: " + e.getMessage());
			return null;
		}
	}

	// =========================================================================
	// EXCEL DATA READING METHODS (Apache POI — Data Driven Testing)
	// =========================================================================

	/**
	 * Find row number by TestCaseID (column 0 in Excel)
	 */
	public static int getRowNumberByTestCaseID(String excelPath, String sheetName, String testCaseID) {
		int rowNumber = -1;
		try (FileInputStream fis = new FileInputStream(excelPath); Workbook workbook = WorkbookFactory.create(fis)) {

			Sheet sheet = workbook.getSheet(sheetName);
			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);
				if (row == null)
					continue;
				Cell cell = row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
				if (cell.getStringCellValue().equalsIgnoreCase(testCaseID)) {
					rowNumber = i;
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rowNumber;
	}

	/**
	 * Read one test case's data as Map<Header, Value> First column must be
	 * TestCaseID
	 */
	public static Map<String, String> readDataAsKeyValue(String excelPath, String sheetName, String testCaseID) {
		Map<String, String> dataMap = new LinkedHashMap<>();
		try (FileInputStream fis = new FileInputStream(excelPath); Workbook workbook = WorkbookFactory.create(fis)) {

			Sheet sheet = workbook.getSheet(sheetName);
			Row headerRow = sheet.getRow(0);
			int targetRow = -1;

			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);
				if (row == null)
					continue;
				Cell cell = row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
				if (cell.getStringCellValue().equalsIgnoreCase(testCaseID)) {
					targetRow = i;
					break;
				}
			}

			if (targetRow == -1)
				return dataMap;

			Row dataRow = sheet.getRow(targetRow);
			int cellCount = headerRow.getLastCellNum();

			for (int i = 0; i < cellCount; i++) {
				String key = headerRow.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
				String value = dataRow.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
				dataMap.put(key, value);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataMap;
	}

	/**
	 * Read all rows from a sheet into a list of maps — for @DataProvider
	 */
	public static Object[][] readAllDataAsDataProvider(String excelPath, String sheetName) {
		List<Map<String, String>> rows = new ArrayList<>();
		try (FileInputStream fis = new FileInputStream(excelPath); Workbook workbook = WorkbookFactory.create(fis)) {

			Sheet sheet = workbook.getSheet(sheetName);
			Row headerRow = sheet.getRow(0);
			int colCount = headerRow.getLastCellNum();

			List<String> headers = new ArrayList<>();
			for (int c = 0; c < colCount; c++) {
				headers.add(headerRow.getCell(c, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
			}

			for (int r = 1; r <= sheet.getLastRowNum(); r++) {
				Row row = sheet.getRow(r);
				if (row == null)
					continue;
				Map<String, String> rowMap = new LinkedHashMap<>();
				for (int c = 0; c < colCount; c++) {
					rowMap.put(headers.get(c),
							row.getCell(c, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
				}
				rows.add(rowMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Object[][] result = new Object[rows.size()][1];
		for (int i = 0; i < rows.size(); i++)
			result[i][0] = rows.get(i);
		return result;
	}

	// =========================================================================
	// BROKEN LINK CHECKER
	// =========================================================================

	public void checkBrokenLinks() {
		List<WebElement> links = driver.findElements(By.tagName("a"));
		et.log(Status.INFO, "Total links found on page: " + links.size());

		for (WebElement link : links) {
			String url = link.getDomAttribute("href");
			if (url == null || url.isEmpty() || url.startsWith("javascript")) {
				et.log(Status.WARNING, "Skipped invalid href for: '" + link.getText() + "'");
				continue;
			}
			try {
				HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
				conn.setRequestMethod("HEAD");
				conn.setConnectTimeout(5000);
				conn.connect();
				int code = conn.getResponseCode();
				if (code >= 400) {
					et.log(Status.FAIL, "Broken link [" + code + "]: " + url);
				} else {
					et.log(Status.PASS, "Valid link [" + code + "]: " + url);
				}
			} catch (Exception e) {
				et.log(Status.FAIL, "Exception checking: " + url + " — " + e.getMessage());
			}
		}
	}
}
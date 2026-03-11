package tests;


import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import Pages.DashboardPage;
import Pages.LoginPage;
import utils.WebUtil;

/**
 * ═══════════════════════════════════════════════════════════════ LAYER 4: TEST
 * CASE LAYER ═══════════════════════════════════════════════════════════════
 *
 * In this layer we call our Page Wise layer to automate test cases. Each test
 * case = one separate @Test method. TestNG manages execution using annotations:
 * 
 * @BeforeMethod → runs before every @Test
 * @AfterMethod → runs after every @Test
 * @Test → each individual test case
 *
 *       Structure follows exactly as shown in PDF: 1. Create ExtentReport 2.
 *       Create ExtentTest (for logging) 3. Create WebUtil with ExtentTest 4.
 *       Launch Browser 5. Open URL 6. Initialise Page
 */
public class RunTest {

	// ─── Fields ───────────────────────────────────────────────────────────────

	private ExtentReports extent;
	private ExtentTest test;
	private WebUtil wu;
	private LoginPage loginPage;
	private DashboardPage dashboardPage;

	// Paths — update these to match your machine / project
	private static final String BASE_URL = "https://www.saucedemo.com/";
	private static final String EXCEL_PATH = "src/test/resources/testdata/LoginDataForAutomation.xlsx";
	private static final String SHEET_NAME = "LoginData";
	private static final String REPORT_PATH = System.getProperty("user.dir") + "/reports/ExtentReport.html";

	// =========================================================================
	// @BeforeMethod — Runs before EVERY @Test
	// Sets up: Report → ExtentTest → WebUtil → Browser → URL → Page
	// =========================================================================

	@BeforeMethod
	public void setUp() {
		// 1. Create Extent Report
		ExtentSparkReporter spark = new ExtentSparkReporter(REPORT_PATH);
		spark.config().setReportName("POM Hybrid Automation Framework Report");
		spark.config().setDocumentTitle("Test Execution Report");
		extent = new ExtentReports();
		extent.attachReporter(spark);
		extent.setSystemInfo("OS", System.getProperty("os.name"));
		extent.setSystemInfo("Browser", "Chrome");
		extent.setSystemInfo("Tester", "Sunil Bind");

		// 2. Create ExtentTest node (for step-level logging)
		test = extent.createTest("Login Test");

		// 3. Pass ExtentTest to WebUtil
		wu = new WebUtil(test);

		// 4. Launch Browser
		wu.launchBrowser("chrome", 10);

		// 5. Open URL
		wu.openURL(BASE_URL);

		// 6. Initialise Page
		loginPage = new LoginPage(wu);
		dashboardPage = new DashboardPage(wu);
	}

	// =========================================================================
	// @AfterMethod — Runs after EVERY @Test
	// Quits browser + flushes Extent Report
	// =========================================================================

	@AfterMethod
	public void tearDown() {
		wu.quitBrowser();
		extent.flush();
	}

	// =========================================================================
	// TC_001: Valid Login
	// =========================================================================

	@Test(priority = 1, description = "TC_001 - Valid login with correct credentials")
	public void TC_001_validLogin() {
		test = extent.createTest("TC_001 - Valid Login");
		wu = new WebUtil(test);

		String dataPath = EXCEL_PATH;
		String sheetName = SHEET_NAME;
		String testCaseID = "TC_001";

		// Read data from Excel
		Map<String, String> data = WebUtil.readDataAsKeyValue(dataPath, sheetName, testCaseID);
		String userName = data.get("UserName");
		String password = data.get("PassWord");

		// Call page method
		loginPage.validLogin(userName, password);

		// Validate dashboard is shown
		Assert.assertTrue(loginPage.isDashboardDisplayed(), "Dashboard should be visible after valid login");
		loginPage.validateDashboardDisplayed();
	}

	// =========================================================================
	// TC_002: Invalid Login — Wrong Password
	// =========================================================================

//	@Test(priority = 2, description = "TC_002 - Invalid login shows error message")
//	public void TC_002_invalidLogin() {
//		test = extent.createTest("TC_002 - Invalid Login");
//
//		String dataPath = EXCEL_PATH;
//		String sheetName = SHEET_NAME;
//		String testCaseID = "TC_001";
//
//		Map<String, String> data = WebUtil.readDataAsKeyValue(dataPath, sheetName, testCaseID);
//		String userName = data.get("UserName");
//		String password = data.get("Password");
//
//		loginPage.validLogin(userName, password);
//
//		// Validate error message is displayed
//		Assert.assertTrue(loginPage.isErrorMessageDisplayed(),
//				"Error message should be displayed for invalid credentials");
//		loginPage.validateErrorMessage("Invalid credentials");
//	}
//
//	// =========================================================================
//	// TC_003: Empty Credentials
//	// =========================================================================
//
//	@Test(priority = 3, description = "TC_003 - Empty credentials should not login")
//	public void TC_003_emptyCredentials() {
//		test = extent.createTest("TC_003 - Empty Credentials");
//
//		loginPage.clickLoginButton();
//
//		boolean stillOnLogin = wu.getPageURL().contains("login");
//		Assert.assertTrue(stillOnLogin, "Should remain on login page when credentials are empty");
//	}
//
//	// =========================================================================
//	// TC_004: Password Field is Masked
//	// =========================================================================
//
//	@Test(priority = 4, description = "TC_004 - Password field should be of type 'password'")
//	public void TC_004_passwordFieldMasked() {
//		test = extent.createTest("TC_004 - Password Field Masking");
//
//		String fieldType = loginPage.getPasswordFieldType();
//		Assert.assertEquals(fieldType, "password", "Password field type must be 'password'");
//	}
//
//	// =========================================================================
//	// TC_005: Validate Login Page Title
//	// =========================================================================
//
//	@Test(priority = 5, description = "TC_005 - Login page title validation")
//	public void TC_005_validatePageTitle() {
//		test = extent.createTest("TC_005 - Page Title Validation");
//
//		// Validates and logs directly to Extent Report
//		loginPage.validateLoginPageTitle("OrangeHRM");
//	}
//
//	// =========================================================================
//	// TC_006: Logout after Login
//	// =========================================================================
//
//	@Test(priority = 6, description = "TC_006 - Logout should return to login page")
//	public void TC_006_logout() {
//		test = extent.createTest("TC_006 - Logout");
//
//		String dataPath = EXCEL_PATH;
//		String sheetName = SHEET_NAME;
//		String testCaseID = "TC_001";
//
//		Map<String, String> data = WebUtil.readDataAsKeyValue(dataPath, sheetName, testCaseID);
//		loginPage.validLogin(data.get("UserName"), data.get("Password"));
//
//		Assert.assertTrue(dashboardPage.isDashboardDisplayed(), "Should be on dashboard after login");
//
//		dashboardPage.logout();
//
//		Assert.assertTrue(loginPage.isLoginPageDisplayed(), "Should be back on login page after logout");
//	}
//
//	// =========================================================================
//	// TC_007: Data Driven Login — runs for EVERY row in Excel
//	// =========================================================================
//
//	@Test(priority = 7, description = "TC_007 - Data-driven login test from Excel", dataProvider = "loginDataProvider")
//	public void TC_007_dataDrivenLogin(Map<String, String> data) {
//		test = extent.createTest("TC_007 DataDriven - user: " + data.get("UserName"));
//
//		String userName = data.get("UserName");
//		String password = data.get("Password");
//		String expectedResult = data.get("ExpectedResult"); // "Pass" or "Fail"
//
//		loginPage.validLogin(userName, password);
//
//		if ("Pass".equalsIgnoreCase(expectedResult)) {
//			Assert.assertTrue(loginPage.isDashboardDisplayed(), "Expected dashboard for valid user: " + userName);
//		} else {
//			Assert.assertTrue(loginPage.isErrorMessageDisplayed(), "Expected error for invalid user: " + userName);
//		}
//	}

	@DataProvider(name = "loginDataProvider")
	public Object[][] getLoginData() {
		return WebUtil.readAllDataAsDataProvider(EXCEL_PATH, SHEET_NAME);
	}
}
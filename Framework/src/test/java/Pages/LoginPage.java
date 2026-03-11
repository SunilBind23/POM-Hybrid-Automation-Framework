package Pages;


import OrLayer.LoginOr;
import utils.WebUtil;

/**
 * ═══════════════════════════════════════════════════════════════ LAYER 3: PAGE
 * WISE LAYER — Login Page
 * ═══════════════════════════════════════════════════════════════
 *
 * Each PAGE has its own class. Each FUNCTIONALITY on the page is a separate
 * method.
 *
 * OOP Concepts used: → Inheritance : extends LoginOr to access private
 * WebElements via getter methods (Encapsulation maintained) → Reusability :
 * calls WebUtil methods instead of raw Selenium
 *
 * How it works: LoginPage → extends LoginOr (gets WebElements) uses WebUtil
 * (calls generic Selenium methods)
 */
public class LoginPage extends LoginOr {

	// WebUtil — our generic utility layer
	private WebUtil wu;

	// ─── Constructor ──────────────────────────────────────────────────────────
	// Takes WebUtil, passes to OR layer (which initialises PageFactory)

	public LoginPage(WebUtil wu) {
		super(wu); // Calls LoginOr constructor → initialises @FindBy elements
		this.wu = wu; // Store wu to call generic methods
	}

	// =========================================================================
	// PAGE METHODS — each functionality = one method
	// =========================================================================

	/**
	 * Perform full login action
	 */
	public void validLogin(String userName, String password) {
		wu.type(getUserNameField(), userName, "Username Field");
		wu.type(getPasswordField(), password, "Password Field");
		wu.click(getLoginBtn(), "Login Button");
	}

	/**
	 * Enter only username (for partial input tests)
	 */
	public void enterUsername(String userName) {
		wu.type(getUserNameField(), userName, "Username Field");
	}

	/**
	 * Enter only password (for partial input tests)
	 */
	public void enterPassword(String password) {
		wu.type(getPasswordField(), password, "Password Field");
	}

	/**
	 * Click login button only
	 */
	public void clickLoginButton() {
		wu.click(getLoginBtn(), "Login Button");
	}

	/**
	 * Perform logout
	 */
	public void logout() {
		wu.click(getUserDropdown(), "User Dropdown");
		wu.click(getLogoutLink(), "Logout Link");
	}

	// ─── Validation Methods ───────────────────────────────────────────────────

	public void validateLoginPageTitle(String expectedTitle) {
		wu.validatePageTitle(expectedTitle);
	}

	public void validateErrorMessage(String expectedMsg) {
		wu.validateInnerText(getErrorMessageLabel(), expectedMsg, "Error Message");
	}

	public void validateDashboardDisplayed() {
		wu.validateElementVisible(getDashboardHeading(), "Dashboard Heading");
	}

	// ─── State Getter Methods ─────────────────────────────────────────────────

	public boolean isLoginPageDisplayed() {
		return wu.isElementDisplayed(getUserNameField(), "Username Field")
				&& wu.isElementDisplayed(getPasswordField(), "Password Field");
	}

	public boolean isErrorMessageDisplayed() {
		return wu.isElementDisplayed(getErrorMessageLabel(), "Error Message");
	}

	public String getErrorMessageText() {
		return wu.getInnerText(getErrorMessageLabel(), "Error Message");
	}

	public boolean isDashboardDisplayed() {
		return wu.isElementDisplayed(getDashboardHeading(), "Dashboard Heading");
	}

	public String getPasswordFieldType() {
		return wu.getAttributeValue(getPasswordField(), "type", "Password Field");
	}
}
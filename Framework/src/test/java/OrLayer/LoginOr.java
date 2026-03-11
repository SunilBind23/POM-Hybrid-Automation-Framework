package OrLayer;
 	

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import utils.WebUtil;

/**
 * ═══════════════════════════════════════════════════════════════
 * LAYER 2: OBJECT REPOSITORY (OR) LAYER — Login Page
 * ═══════════════════════════════════════════════════════════════
 *
 * This layer stores all WebElements as private variables.
 * It is a collection of WebElements for the Login Page.
 *
 * OOP Concepts used:
 *   → Encapsulation  : all WebElements declared as PRIVATE
 *   → Data Hiding    : elements not directly accessible outside
 *   → Getter Methods : controlled access to each WebElement
 *
 * PageFactory initialises all @FindBy elements via the constructor.
 *
 * 🔁 Update @FindBy locators to match your application's HTML
 */
public class LoginOr {

    // ─── Encapsulation: WebElements declared as PRIVATE ──────────────────────

    @FindBy(name = "user-name")
    private WebElement userNameField;

    @FindBy(name = "password")
    private WebElement passwordField;

    @FindBy(xpath = "//input[@type='submit']")
    private WebElement loginBtn;

    
    // error-button
    @FindBy(xpath = "//h3[@data-test='error']")
    private WebElement errorMessageLabel;

    @FindBy(xpath = "//div[@class='app_logo']")
    private WebElement dashboardHeading;

    @FindBy(xpath = "//select[@class='product_sort_container']")
    private WebElement userDropdown;

    @FindBy(xpath = "//a[text()='Logout']")
    private WebElement logoutLink;

    // ─── Constructor: PageFactory initialises @FindBy elements ───────────────

    public LoginOr(WebUtil wu) {
        PageFactory.initElements(wu.getDriver(), this);
    }

    // ─── Getter Methods: controlled access to private WebElements ─────────────

    public WebElement getUserNameField() {
        return userNameField;
    }

    public WebElement getPasswordField() {
        return passwordField;
    }

    public WebElement getLoginBtn() {
        return loginBtn;
    }

    public WebElement getErrorMessageLabel() {
        return errorMessageLabel;
    }

    public WebElement getDashboardHeading() {
        return dashboardHeading;
    }

    public WebElement getUserDropdown() {
        return userDropdown;
    }

    public WebElement getLogoutLink() {
        return logoutLink;
    }
}
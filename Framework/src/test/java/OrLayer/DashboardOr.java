package OrLayer;


import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import utils.WebUtil;

/**
 * ═══════════════════════════════════════════════════════════════
 * LAYER 2: OBJECT REPOSITORY (OR) LAYER — Dashboard Page
 * ═══════════════════════════════════════════════════════════════
 *
 * All WebElements for the Dashboard page, declared as PRIVATE.
 * Getter methods provide controlled access (Encapsulation).
 *
 * 🔁 Update @FindBy locators to match your application's HTML
 */
public class DashboardOr {

    @FindBy(xpath = "//h6[@class='oxd-text oxd-text--h6 oxd-topbar-header-breadcrumb-module']")
    private WebElement dashboardHeading;

    @FindBy(xpath = "//span[@class='oxd-userdropdown-tab']")
    private WebElement userProfileMenu;

    @FindBy(xpath = "//a[normalize-space()='Logout']")
    private WebElement logoutOption;

    @FindBy(xpath = "//p[@class='oxd-text oxd-text--p oxd-text--card-body']")
    private WebElement totalEmployeeCount;

    // ─── Constructor ──────────────────────────────────────────────────────────

    public DashboardOr(WebUtil wu) {
        PageFactory.initElements(wu.getDriver(), this);
    }

    // ─── Getters ──────────────────────────────────────────────────────────────

    public WebElement getDashboardHeading() {
        return dashboardHeading;
    }

    public WebElement getUserProfileMenu() {
        return userProfileMenu;
    }

    public WebElement getLogoutOption() {
        return logoutOption;
    }

    public WebElement getTotalEmployeeCount() {
        return totalEmployeeCount;
    }
}
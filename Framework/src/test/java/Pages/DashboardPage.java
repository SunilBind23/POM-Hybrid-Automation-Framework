package Pages;


import OrLayer.DashboardOr;
import utils.WebUtil;

/**
 * ═══════════════════════════════════════════════════════════════ LAYER 3: PAGE
 * WISE LAYER — Dashboard Page
 * ═══════════════════════════════════════════════════════════════
 *
 * Extends DashboardOr to access all WebElements. Uses WebUtil for all
 * interactions.
 *
 * 🔁 Add methods for each dashboard functionality
 */
public class DashboardPage extends DashboardOr {

	private WebUtil wu;

	public DashboardPage(WebUtil wu) {
		super(wu);
		this.wu = wu;
	}

	// =========================================================================
	// PAGE METHODS
	// =========================================================================

	public void logout() {
		wu.click(getUserProfileMenu(), "User Profile Menu");
		wu.waitForVisibility(getLogoutOption(), 5);
		wu.click(getLogoutOption(), "Logout Option");
	}

	public void validateDashboardHeading(String expectedHeading) {
		wu.validateInnerText(getDashboardHeading(), expectedHeading, "Dashboard Heading");
	}

	public void validateDashboardURL(String expectedUrlFragment) {
		wu.validatePageURL(expectedUrlFragment);
	}

	// ─── State Getters ────────────────────────────────────────────────────────

	public boolean isDashboardDisplayed() {
		return wu.isElementDisplayed(getDashboardHeading(), "Dashboard Heading");
	}

	public String getDashboardTitle() {
		return wu.getPageTitle();
	}
}

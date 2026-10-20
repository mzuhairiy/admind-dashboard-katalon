package components

import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import utils.TestObjectFactory

class NavigationComponent {
	boolean hasRevenueNavigation() {
		return WebUI.verifyElementPresent(TestObjectFactory.byTestId('sidebar-revenue'), 2,
				com.kms.katalon.core.model.FailureHandling.OPTIONAL)
	}
}

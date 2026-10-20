import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import components.NavigationComponent as NavigationComponent
import pages.DashboardPage as DashboardPage
import pages.LoginPage as LoginPage
import pages.RevenuePage as RevenuePage
import internal.GlobalVariable as GlobalVariable

try {
    WebUI.openBrowser(GlobalVariable.baseUrl)
	
	WebUI.maximizeWindow()

    new LoginPage().open().signInAsManager()

    new DashboardPage().waitUntilLoaded()

	assert new NavigationComponent().hasRevenueNavigation() : 'Manager should have Revenue navigation.'

    RevenuePage revenuePage = new RevenuePage()

    revenuePage.open()

    revenuePage.waitUntilLoaded()

    assert WebUI.getUrl().contains('/revenue')
}
finally { 
    WebUI.closeBrowser()
}


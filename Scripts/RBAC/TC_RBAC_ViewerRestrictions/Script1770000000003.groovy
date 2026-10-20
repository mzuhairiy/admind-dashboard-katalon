import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import components.NavigationComponent as NavigationComponent
import pages.DashboardPage as DashboardPage
import pages.LoginPage as LoginPage
import pages.RevenuePage as RevenuePage
import utils.TestObjectFactory as TestObjectFactory
import internal.GlobalVariable as GlobalVariable

try {
    WebUI.openBrowser(GlobalVariable.baseUrl)
	
	WebUI.maximizeWindow()

    new LoginPage().open().signInAsViewer()

    new DashboardPage().waitUntilLoaded()

    assert !(new NavigationComponent().hasRevenueNavigation()) : 'Viewer must not have Revenue navigation in the DOM.'

    assert !(WebUI.verifyElementPresent(TestObjectFactory.byTestId('export-csv-button'), 2, com.kms.katalon.core.model.FailureHandling.OPTIONAL)) : 'Viewer must not have an Export CSV button in the DOM.'

    RevenuePage revenuePage = new RevenuePage()

    revenuePage.open()

    revenuePage.waitUntilForbidden()

    assert WebUI.getUrl().contains('/forbidden')
}
finally { 
    WebUI.closeBrowser()
}


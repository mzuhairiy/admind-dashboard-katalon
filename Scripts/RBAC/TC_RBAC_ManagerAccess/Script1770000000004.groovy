import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import components.NavigationComponent as NavigationComponent
import pages.LoginPage as LoginPage
import pages.RevenuePage as RevenuePage
import internal.GlobalVariable as GlobalVariable
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

try {
    WebUI.openBrowser(GlobalVariable.baseUrl)
	
	WebUI.maximizeWindow()

    new LoginPage().open().signInAsManager()

    assert WebUI.waitForElementVisible(findTestObject('Shared/Dashboard_Page'), 10) :
        'Dashboard must be visible after successful Manager login.'

	assert new NavigationComponent().hasRevenueNavigation() : 'Manager should have Revenue navigation.'

    RevenuePage revenuePage = new RevenuePage()

    revenuePage.open()

    revenuePage.waitUntilLoaded()

    assert WebUI.getUrl().contains('/revenue')
}
finally { 
    WebUI.closeBrowser()
}


import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import components.NavigationComponent as NavigationComponent
import pages.LoginPage as LoginPage
import pages.RevenuePage as RevenuePage
import utils.TestObjectFactory as TestObjectFactory
import internal.GlobalVariable as GlobalVariable
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

try {
    WebUI.openBrowser(GlobalVariable.baseUrl)
	
	WebUI.maximizeWindow()

    new LoginPage().open().signInAsViewer()

    assert WebUI.waitForElementVisible(findTestObject('Shared/Dashboard_Page'), 10) :
        'Dashboard must be visible after successful Viewer login.'

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


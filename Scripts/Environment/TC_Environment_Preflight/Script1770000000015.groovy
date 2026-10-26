import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import pages.DashboardPage as DashboardPage
import pages.LoginPage as LoginPage
import utils.ApiClient as ApiClient
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

def healthResponse = new ApiClient().get('/api/health')
assert healthResponse.statusCode == 200 :
    "SUT health endpoint must return 200; actual status was ${healthResponse.statusCode}."

try {
    WebUI.openBrowser(GlobalVariable.baseUrl)
    WebUI.maximizeWindow()

    new LoginPage().open().signInAsViewer()
    assert WebUI.waitForElementVisible(findTestObject('Shared/Dashboard_Page'), 10) :
        'Dashboard must be visible after successful Viewer login.'

    DashboardPage dashboard = new DashboardPage()
    dashboard.openWithQuery('preset=30d&tz=UTC')
    dashboard.waitUntilAnalyticsLoaded()

    assert dashboard.kpiValue('revenue') == new BigDecimal('225641000') :
        'The deterministic UTC 30-day seed must have revenue 225641000.'
    assert dashboard.kpiValue('orders') == new BigDecimal('544') :
        'The deterministic UTC 30-day seed must have 544 orders.'
    assert dashboard.kpiValue('aov') == new BigDecimal('480087') :
        'The deterministic UTC 30-day seed must have AOV 480087.'
    assert dashboard.kpiValue('refund-rate') == new BigDecimal('6.1') :
        'The deterministic UTC 30-day seed must have a 6.1% refund rate.'
}
finally {
    WebUI.closeBrowser()
}

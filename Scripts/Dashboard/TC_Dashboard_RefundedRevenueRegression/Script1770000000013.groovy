import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import pages.DashboardPage as DashboardPage
import pages.LoginPage as LoginPage
import utils.TestObjectFactory as TestObjectFactory
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

try {
    WebUI.openBrowser(GlobalVariable.baseUrl)
    WebUI.maximizeWindow()
    new LoginPage().open().signInAsViewer()
    assert WebUI.waitForElementVisible(findTestObject('Shared/Dashboard_Page'), 10) :
        'Dashboard must be visible after successful Viewer login.'
    DashboardPage dashboard = new DashboardPage()
    dashboard.openWithQuery('preset=30d&tz=UTC&status=REFUNDED')
    dashboard.waitUntilLoaded()
    assert WebUI.waitForElementVisible(TestObjectFactory.byTestId('chart-order-status-slice'), 15) :
        'Refunded order distribution must render.'

    assert dashboard.kpiValue('revenue') == BigDecimal.ZERO : 'Refunded orders must not contribute paid revenue.'
    assert dashboard.kpiValue('orders') > BigDecimal.ZERO : 'Refunded orders must remain in the order count.'
    assert dashboard.kpiValue('refund-rate') == new BigDecimal('100.0') :
        'A refunded-only filter must have a 100% refund rate.'
    assert dashboard.sumChartValues('chart-order-status-slice') == dashboard.kpiValue('orders') :
        'Order-status slices must agree with the order KPI.'

    def revenuePoints = WebUI.findWebElements(TestObjectFactory.byTestId('chart-revenue-trend-point'), 2) ?: []
    assert revenuePoints.every { it.getAttribute('data-value') == '0' } :
        'Every refunded-only revenue trend point must be zero.'
    def categoryBars = WebUI.findWebElements(TestObjectFactory.byTestId('chart-top-categories-bar'), 2) ?: []
    assert categoryBars.every { it.getAttribute('data-value') == '0' } :
        'Refunded-only category revenue must be zero.'
}
finally {
    WebUI.closeBrowser()
}

import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import pages.DashboardPage as DashboardPage
import pages.LoginPage as LoginPage
import utils.UrlState as UrlState
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

String scenarioName = scenario.toString().trim()
String queryString = query.toString().trim()
String expectedPresetValue = expectedPreset.toString().trim()
String expectedTimezoneValue = expectedTimezone.toString().trim()
String expectedStatusValue = expectedStatus.toString().trim()
String behavior = expectedBehavior.toString().trim()

try {
    WebUI.openBrowser(GlobalVariable.baseUrl)
    WebUI.maximizeWindow()
    new LoginPage().open().signInAsViewer()
    assert WebUI.waitForElementVisible(findTestObject('Shared/Dashboard_Page'), 10) :
        "${scenarioName}: Dashboard must be visible after successful Viewer login."

    DashboardPage dashboard = new DashboardPage()
    dashboard.openWithQuery(queryString)
    dashboard.waitUntilLoaded()

    assert dashboard.isDatePresetSelected(expectedPresetValue) :
        "${scenarioName}: expected selected preset '${expectedPresetValue}'."
    assert UrlState.firstQueryValue(WebUI.getUrl(), 'tz') == expectedTimezoneValue :
        "${scenarioName}: expected first timezone query value '${expectedTimezoneValue}'."
    assert dashboard.filterText('filter-status') == expectedStatusValue :
        "${scenarioName}: expected status filter '${expectedStatusValue}'."

    switch (behavior) {
        case 'reference-kpis':
            assert dashboard.kpiValue('revenue') == new BigDecimal('225641000') :
                "${scenarioName}: UTC 30D revenue must match the fixed-seed oracle."
            assert dashboard.kpiValue('orders') == new BigDecimal('544') :
                "${scenarioName}: UTC 30D orders must match the fixed-seed oracle."
            assert dashboard.kpiValue('aov') == new BigDecimal('480087') :
                "${scenarioName}: UTC 30D AOV must match the fixed-seed oracle."
            assert dashboard.kpiValue('refund-rate') == new BigDecimal('6.1') :
                "${scenarioName}: UTC 30D refund rate must match the fixed-seed oracle."
            break
        case 'preserve-valid-filter':
            assert UrlState.firstQueryValue(WebUI.getUrl(), 'status') == 'PAID' :
                "${scenarioName}: a valid status filter must survive the invalid preset."
            assert dashboard.kpiValue('orders') > BigDecimal.ZERO :
                "${scenarioName}: the valid Paid filter must return matching orders."
            break
        case 'first-value-wins':
            assert UrlState.firstQueryValue(WebUI.getUrl(), 'preset') == '30d' :
                "${scenarioName}: the first preset value must win."
            assert UrlState.firstQueryValue(WebUI.getUrl(), 'status') == 'REFUNDED' :
                "${scenarioName}: the first status value must win."
            assert dashboard.kpiValue('revenue') == BigDecimal.ZERO :
                "${scenarioName}: refunded-only orders must contribute zero paid revenue."
            assert dashboard.kpiValue('orders') > BigDecimal.ZERO :
                "${scenarioName}: refunded orders must remain in the order count."
            break
        default:
            throw new AssertionError("${scenarioName}: unsupported expectedBehavior '${behavior}'.")
    }
}
finally {
    WebUI.closeBrowser()
}

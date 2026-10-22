import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import pages.LoginPage as LoginPage
import pages.RevenuePage as RevenuePage
import utils.TestObjectFactory as TestObjectFactory
import utils.UrlState as UrlState
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

try {
    WebUI.openBrowser(GlobalVariable.baseUrl)
    WebUI.maximizeWindow()
    new LoginPage().open().signInAsManager()
    assert WebUI.waitForElementVisible(findTestObject('Shared/Dashboard_Page'), 10) :
        'Dashboard must be visible after successful Manager login.'

    RevenuePage page = new RevenuePage()
    WebUI.navigateToUrl("${GlobalVariable.baseUrl}/revenue?tz=UTC")
    page.waitUntilLoaded()
    WebUI.click(TestObjectFactory.byTestId('date-preset-30d'))
    long deadline = System.currentTimeMillis() + 15_000
    while (UrlState.firstQueryValue(WebUI.getUrl(), 'preset') != '30d' && System.currentTimeMillis() < deadline) {
        Thread.sleep(200)
    }
    assert UrlState.firstQueryValue(WebUI.getUrl(), 'preset') == '30d' :
        'Selecting 30D on Revenue must write an explicit preset=30d.'
    WebUI.refresh()
    page.waitUntilLoaded()
    assert WebUI.getAttribute(TestObjectFactory.byTestId('date-preset-30d'), 'aria-pressed') == 'true' :
        'Revenue must keep 30D selected after refresh.'

    def monthRow = TestObjectFactory.css("[data-testid='revenue-month-row'][data-month='2025-12']", 'december_revenue')
    assert WebUI.waitForElementVisible(monthRow, 15) : 'December 2025 revenue row must load.'
    def ordersCell = TestObjectFactory.childByTestId('revenue-month-row', 'revenue-month-orders')
    def totalCell = TestObjectFactory.childByTestId('revenue-month-row', 'revenue-month-total')
    assert WebUI.getAttribute(ordersCell, 'data-value') == '544' : 'UTC 30D December order count must match the seed oracle.'
    assert WebUI.getAttribute(totalCell, 'data-value') == '225641000' : 'UTC 30D December paid revenue must match the seed oracle.'

    assert WebUI.waitForElementVisible(TestObjectFactory.byTestId('revenue-breakdown-segment'), 15) :
        'Revenue chart segments must render.'
    def chartSegments = WebUI.findWebElements(TestObjectFactory.byTestId('revenue-breakdown-segment'), 15)
    assert chartSegments.every { it.getAttribute('data-month') == '2025-12' } :
        'UTC 30D revenue chart must use the December local month.'
    long chartTotal = chartSegments.collect { Long.parseLong(it.getAttribute('data-value')) }.sum(0L)
    assert chartTotal == 225641000L : 'Monthly chart segments must total the independent revenue oracle.'

    def drilldown = TestObjectFactory.byTestId('revenue-drilldown-2025-12')
    WebUI.click(drilldown)
    assert WebUI.getAttribute(drilldown, 'aria-expanded') == 'true' : 'December drill-down must expand.'
    def categoryValues = TestObjectFactory.css(
        "[data-testid='revenue-category-row'][data-month='2025-12'] [data-testid='revenue-category-value']",
        'december_category_values'
    )
    assert WebUI.waitForElementVisible(categoryValues, 15) : 'December category rows must appear.'
    def values = WebUI.findWebElements(categoryValues, 15)
    assert values.size() == 10 : 'The seeded Revenue breakdown must contain ten categories.'
    long detailTotal = values.collect { Long.parseLong(it.getAttribute('data-value')) }.sum(0L)
    assert detailTotal == 225641000L : 'Category drill-down must total the independent revenue oracle.'
}
finally {
    WebUI.closeBrowser()
}

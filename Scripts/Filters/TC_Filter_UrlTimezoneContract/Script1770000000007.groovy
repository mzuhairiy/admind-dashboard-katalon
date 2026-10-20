import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import pages.DashboardPage as DashboardPage
import pages.LoginPage as LoginPage
import pages.TransactionsPage as TransactionsPage
import utils.TestObjectFactory as TestObjectFactory
import utils.UrlState as UrlState
import internal.GlobalVariable as GlobalVariable

try {
    WebUI.openBrowser(GlobalVariable.baseUrl)

    // A deep link supplied before authentication must survive sign-in.
    new LoginPage().open('/dashboard?preset=30d&tz=UTC&status=PAID').signInAsViewer()
    DashboardPage dashboardPage = new DashboardPage()
    dashboardPage.waitUntilAnalyticsLoaded()
    assert UrlState.firstQueryValue(WebUI.getUrl(), 'preset') == '30d' : 'The deep-link preset must survive sign-in.'
    assert UrlState.firstQueryValue(WebUI.getUrl(), 'tz') == 'UTC' : 'The deep-link timezone must survive sign-in.'
    assert UrlState.firstQueryValue(WebUI.getUrl(), 'status') == 'PAID' : 'The deep-link status filter must survive sign-in.'

    WebUI.refresh()
    dashboardPage.waitUntilAnalyticsLoaded()
    assert dashboardPage.isDatePresetSelected('30d') : 'Refresh must preserve the selected date preset.'
    assert dashboardPage.filterText('filter-status') == 'Paid' : 'Refresh must preserve the selected status filter.'

    dashboardPage.openWithQuery('preset=not-a-preset&tz=Asia%2FTokyo&status=PAID')
    dashboardPage.waitUntilAnalyticsLoaded()
    assert dashboardPage.isDatePresetSelected('30d') : 'An invalid preset must fall back to the dashboard default of 30d.'
    assert UrlState.firstQueryValue(WebUI.getUrl(), 'tz') == 'Asia/Tokyo' : 'An invalid preset must preserve a valid timezone.'
    assert dashboardPage.filterText('filter-status') == 'Paid' : 'An invalid preset must preserve a valid status filter.'

    dashboardPage.openWithQuery('preset=30d&preset=90d&tz=UTC&tz=Asia%2FTokyo&status=REFUNDED&status=PAID')
    dashboardPage.waitUntilLoaded()
    assert WebUI.waitForElementVisible(TestObjectFactory.byTestId('stats-value-orders'), 15) : 'Refunded dashboard order KPI must load.'
    assert dashboardPage.isDatePresetSelected('30d') : 'Repeated presets must use the first value.'
    assert UrlState.firstQueryValue(WebUI.getUrl(), 'tz') == 'UTC' : 'Repeated timezones must use the first value.'
    assert dashboardPage.filterText('filter-status') == 'Refunded' : 'Repeated statuses must use the first value.'
    assert dashboardPage.kpiValue('revenue') == BigDecimal.ZERO : 'Refunded-only transactions must not contribute revenue.'
    assert dashboardPage.kpiValue('orders') > BigDecimal.ZERO : 'Refunded-only transactions must still contribute to order count.'

    TransactionsPage transactionsPage = new TransactionsPage()
    transactionsPage.openWithQuery('preset=30d&tz=UTC')
    transactionsPage.waitUntilLoaded()
    def utcDate = transactionsPage.firstTransactionDate()

    transactionsPage.openWithQuery('preset=30d&tz=Asia%2FTokyo')
    transactionsPage.waitUntilLoaded()
    def tokyoDate = transactionsPage.firstTransactionDate()
    assert tokyoDate.isoUtc == utcDate.isoUtc : 'Timezone changes must not alter the stored UTC instant.'
    assert tokyoDate.visibleText != utcDate.visibleText : 'Timezone changes must alter the localized transaction date.'
}
finally {
    WebUI.closeBrowser()
}

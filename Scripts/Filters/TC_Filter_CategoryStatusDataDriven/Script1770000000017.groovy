import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.By
import pages.DashboardPage as DashboardPage
import pages.LoginPage as LoginPage
import pages.TransactionsPage as TransactionsPage
import utils.ApiClient as ApiClient
import utils.CsvParser as CsvParser
import utils.TestObjectFactory as TestObjectFactory
import utils.UrlState as UrlState
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

String scenarioName = scenario.toString().trim()
String queryString = query.toString().trim()
String expectedCategoryValue = expectedCategorySlugs.toString().trim()
String expectedStatusValue = expectedStatuses.toString().trim()
Set<String> expectedCategories = expectedCategoryValue.split('\\|').collect { it.trim() } as Set<String>
Set<String> expectedStatusesSet = expectedStatusValue.split('\\|').collect { it.trim() } as Set<String>
String behavior = expectedBehavior.toString().trim()

assert scenarioName : 'DDT data binding is missing: run this case through TS_Filter_DataDriven, not as a standalone test case.'
assert queryString : "${scenarioName}: DDT query value is empty; check the suite data binding."
assert expectedCategoryValue : "${scenarioName}: expectedCategorySlugs is empty; check the suite data binding."
assert expectedStatusValue : "${scenarioName}: expectedStatuses is empty; check the suite data binding."
assert behavior : "${scenarioName}: expectedBehavior is empty; check the suite data binding."

try {
    WebUI.openBrowser(GlobalVariable.baseUrl)
    WebUI.maximizeWindow()
    new LoginPage().open().signInAsManager()
    assert WebUI.waitForElementVisible(findTestObject('Shared/Dashboard_Page'), 10) :
        "${scenarioName}: Dashboard must be visible after successful Manager login."

    DashboardPage dashboard = new DashboardPage()
    dashboard.openWithQuery(queryString)
    dashboard.waitUntilLoaded()
    if (behavior == 'empty-result') {
        assert WebUI.waitForElementVisible(TestObjectFactory.byTestId('empty-state'), 15) :
            "${scenarioName}: dashboard empty state must load."
    } else {
        assert WebUI.waitForElementVisible(TestObjectFactory.byTestId('stats-value-orders'), 15) :
            "${scenarioName}: dashboard order KPI must load."
    }
    String actualCategoryQuery = UrlState.firstQueryValue(WebUI.getUrl(), 'category')
    if (expectedCategories.contains('*')) {
        assert actualCategoryQuery == null : "${scenarioName}: category must remain unfiltered."
    } else {
        assert actualCategoryQuery == expectedCategories.join(',') :
            "${scenarioName}: category filter must be reflected in the URL."
    }
    String rawStatusQuery = UrlState.firstQueryValue("${GlobalVariable.baseUrl}/dashboard?${queryString}", 'status')
    assert UrlState.firstQueryValue(WebUI.getUrl(), 'status') == rawStatusQuery :
        "${scenarioName}: the first raw status query value must be preserved in the URL."

    String expectedStatusLabel
    if (expectedStatusesSet.contains('*')) {
        expectedStatusLabel = 'All statuses'
    } else if (expectedStatusesSet.size() == 1) {
        expectedStatusLabel = [
            PAID: 'Paid',
            PENDING: 'Pending',
            REFUNDED: 'Refunded',
            CANCELLED: 'Cancelled'
        ][expectedStatusesSet.first()]
    } else {
        expectedStatusLabel = "${expectedStatusesSet.size()} statuses"
    }
    assert dashboard.filterText('filter-status') == expectedStatusLabel :
        "${scenarioName}: effective status filter must be '${expectedStatusLabel}'."

    ApiClient apiClient = new ApiClient()
    apiClient.signInAsManager()
    def exportResponse = apiClient.get("/api/export/csv?${queryString}")
    assert exportResponse.statusCode == 200 :
        "${scenarioName}: Manager CSV request must return 200; actual=${exportResponse.statusCode}."
    assert exportResponse.header('Content-Type')?.contains('text/csv') :
        "${scenarioName}: filtered export response must be CSV."

    List<List<String>> csvRows = CsvParser.parse(exportResponse.body)
    assert csvRows : "${scenarioName}: CSV response must contain a header row."
    assert csvRows[0] == ['Transaction ID', 'Date (ISO UTC)', 'Date (Local)', 'Customer', 'Category', 'Amount', 'Status'] :
        "${scenarioName}: CSV header must match the export contract."
    List<List<String>> dataRows = csvRows.drop(1)

    switch (behavior) {
        case 'matching-rows':
            assert !dataRows.isEmpty() : "${scenarioName}: expected matching CSV rows."
            assert dataRows.every { List<String> row ->
                (expectedCategories.contains('*') ||
                    expectedCategories.contains(row[4].toLowerCase(Locale.ROOT).replaceAll('[^a-z0-9]+', '-'))) &&
                    (expectedStatusesSet.contains('*') || expectedStatusesSet.contains(row[6]))
            } : "${scenarioName}: every CSV row must satisfy the expected category and status filters."

            BigDecimal expectedRevenueFromCsv = dataRows
                .findAll { List<String> row -> row[6] == 'PAID' }
                .collect { List<String> row -> new BigDecimal(row[5]) }
                .inject(BigDecimal.ZERO) { BigDecimal sum, BigDecimal amount -> sum + amount }
            assert dashboard.kpiValue('orders') == new BigDecimal(dataRows.size()) :
                "${scenarioName}: dashboard order count must equal filtered CSV rows (${dataRows.size()})."
            assert dashboard.kpiValue('revenue') == expectedRevenueFromCsv :
                "${scenarioName}: dashboard revenue must equal paid amounts in filtered CSV."
            assert dashboard.sumChartValues('chart-order-status-slice') == dashboard.kpiValue('orders') :
                "${scenarioName}: order-status chart must sum to filtered dashboard orders."
            if (expectedRevenueFromCsv == BigDecimal.ZERO) {
                List categoryBars = WebUI.findWebElements(
                    TestObjectFactory.allByTestId('chart-top-categories-bar'), 2
                ) ?: []
                assert categoryBars.isEmpty() || categoryBars.every { it.getAttribute('data-value') == '0' } :
                    "${scenarioName}: zero paid revenue must not render a nonzero category bar."
            } else {
                assert dashboard.sumChartValues('chart-top-categories-bar') == dashboard.kpiValue('revenue') :
                    "${scenarioName}: category revenue chart must sum to filtered dashboard revenue."
            }
            break
        case 'invalid-status-fallback-all':
            assert dataRows.size() == 544 :
                "${scenarioName}: invalid-only status filter must fall back to all 544 UTC 30D orders."
            assert dataRows.every { List<String> row -> row[6] in ['PAID', 'PENDING', 'REFUNDED', 'CANCELLED'] } :
                "${scenarioName}: fallback export must contain only known statuses."
            assert dashboard.filterText('filter-status') == 'All statuses' :
                "${scenarioName}: invalid-only status filter must resolve to all statuses in the UI."
            assert dashboard.kpiValue('orders') == new BigDecimal('544') :
                "${scenarioName}: dashboard must use the UTC 30D all-status order oracle."
            assert dashboard.kpiValue('revenue') == new BigDecimal('225641000') :
                "${scenarioName}: dashboard must use the UTC 30D all-status revenue oracle."
            BigDecimal paidRevenueFromFallbackCsv = dataRows.findAll { List<String> row -> row[6] == 'PAID' }
                .collect { List<String> row -> new BigDecimal(row[5]) }
                .inject(BigDecimal.ZERO) { BigDecimal sum, BigDecimal amount -> sum + amount }
            assert paidRevenueFromFallbackCsv == dashboard.kpiValue('revenue') :
                "${scenarioName}: fallback CSV paid total ${paidRevenueFromFallbackCsv} must equal dashboard revenue ${dashboard.kpiValue('revenue')}."
            break
        case 'empty-result':
            assert dataRows.isEmpty() : "${scenarioName}: unknown category must produce no CSV data rows."
            assert WebUI.verifyElementNotPresent(
                TestObjectFactory.byTestId('stats-value-orders'), 2, FailureHandling.OPTIONAL
            ) : "${scenarioName}: empty dashboard must not render KPI cards."
            break
        default:
            throw new AssertionError("${scenarioName}: unsupported expectedBehavior '${behavior}'.")
    }

    TransactionsPage transactionsPage = new TransactionsPage()
    WebUI.navigateToUrl("${GlobalVariable.baseUrl}/transactions?${queryString}&pageSize=100")
    def transactionRowsObject = TestObjectFactory.allByTestId('transaction-row')
    if (behavior == 'empty-result') {
        assert WebUI.waitForElementVisible(TestObjectFactory.byTestId('empty-state'), 10) :
            "${scenarioName}: empty transactions state must be visible."
        assert WebUI.verifyElementNotPresent(transactionRowsObject, 2, FailureHandling.OPTIONAL) :
            "${scenarioName}: empty result must not render transaction rows."
    } else {
        def paginationTotal = TestObjectFactory.byTestId('pagination-total')
        assert WebUI.waitForElementVisible(paginationTotal, 15) :
            "${scenarioName}: transactions total must load."
        String totalText = WebUI.getText(paginationTotal)
        String totalNumber = totalText.find(/\d+/)
        assert totalNumber != null : "${scenarioName}: could not read total count from '${totalText}'."
        assert Integer.parseInt(totalNumber) == dataRows.size() :
            "${scenarioName}: transactions total ${totalNumber} must equal CSV row count ${dataRows.size()}."

        transactionsPage.waitUntilLoaded()
        List visibleRows = WebUI.findWebElements(transactionRowsObject, 10)
        assert !visibleRows.isEmpty() : "${scenarioName}: filtered transactions table must render rows."
        assert visibleRows.every { row ->
            String category = row.findElement(By.cssSelector("[data-testid='column-category']")).getText().trim()
                .toLowerCase(Locale.ROOT).replaceAll('[^a-z0-9]+', '-')
            String status = row.findElement(By.cssSelector("[data-testid='column-status']")).getText().trim().toUpperCase(Locale.ROOT)
            return (expectedCategories.contains('*') || expectedCategories.contains(category)) &&
                (expectedStatusesSet.contains('*') || expectedStatusesSet.contains(status))
        } : "${scenarioName}: every rendered table row must satisfy the expected category and status filters."
    }
}
finally {
    WebUI.closeBrowser()
}

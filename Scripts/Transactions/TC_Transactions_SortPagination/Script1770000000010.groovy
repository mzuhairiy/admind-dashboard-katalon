import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.By
import pages.LoginPage as LoginPage
import pages.TransactionsPage as TransactionsPage
import utils.TestObjectFactory as TestObjectFactory
import utils.UrlState as UrlState

def waitForQuery = { String key, String expected ->
    long deadline = System.currentTimeMillis() + 15_000
    while (System.currentTimeMillis() < deadline) {
        if (UrlState.firstQueryValue(WebUI.getUrl(), key) == expected) return true
        Thread.sleep(200)
    }
    return false
}
def rows = { -> WebUI.findWebElements(TestObjectFactory.byTestId('transaction-row'), 15) }
def waitForRowCount = { int expected ->
    long deadline = System.currentTimeMillis() + 15_000
    while (System.currentTimeMillis() < deadline) {
        if ((WebUI.findWebElements(TestObjectFactory.byTestId('transaction-row'), 1) ?: []).size() == expected) return true
        Thread.sleep(200)
    }
    return false
}
def amounts = { -> rows().collect { row ->
    Long.parseLong(row.findElement(By.cssSelector("[data-testid='column-amount']")).getAttribute('data-value'))
} }
def ascending = { values -> values == values.sort(false) }
def descending = { values -> values == values.sort(false).reverse() }

try {
    WebUI.openBrowser(GlobalVariable.baseUrl)
    new LoginPage().open().signInAsViewer()

    TransactionsPage page = new TransactionsPage()
    page.openWithQuery('preset=30d&tz=UTC&pageSize=20&sort=amount&order=asc')
    page.waitUntilLoaded()
    assert WebUI.getText(TestObjectFactory.byTestId('pagination-total')).contains('544') : 'UTC 30D must contain 544 transactions.'
    assert rows().size() == 20 : 'The first page must show 20 rows.'
    assert ascending(amounts()) : 'Amount ascending must be sorted across the visible page.'

    WebUI.click(TestObjectFactory.byTestId('sort-amount'))
    assert waitForQuery('order', 'desc') : 'Clicking Amount must toggle to descending order.'
    page.waitUntilLoaded()
    assert WebUI.waitForElementAttributeValue(TestObjectFactory.byTestId('sort-amount'), 'aria-sort', 'descending', 15) :
        'The Amount column must display descending sort state.'
    List<Long> firstPageAmounts = amounts()
    assert descending(firstPageAmounts) : 'Amount descending must be sorted across the visible page.'
    String firstPageFirstId = page.firstTransaction().id

    WebUI.click(TestObjectFactory.byTestId('pagination-next'))
    assert waitForQuery('page', '2') : 'Next must navigate to page 2.'
    assert WebUI.waitForElementNotPresent(TestObjectFactory.transactionRow(firstPageFirstId), 15) :
        'Page 2 must replace the page 1 transaction rows.'
    page.waitUntilLoaded()
    List<Long> secondPageAmounts = amounts()
    assert secondPageAmounts.size() == 20
    assert descending(secondPageAmounts) && firstPageAmounts.last() >= secondPageAmounts.first() :
        'Page 2 must continue the descending sort after page 1.'

    WebUI.click(TestObjectFactory.byTestId('page-size-select'))
    WebUI.click(TestObjectFactory.byTestId('page-size-50'))
    assert waitForQuery('pageSize', '50') : 'Page-size control must update the URL to 50.'
    page.waitUntilLoaded()
    assert waitForRowCount(50) : 'The table must finish rendering the 50-row page.'
    assert UrlState.firstQueryValue(WebUI.getUrl(), 'page') == null : 'Changing page size must reset pagination.'
    assert rows().size() == 50 : 'The table must render 50 rows.'

    WebUI.click(TestObjectFactory.byTestId('page-size-select'))
    WebUI.click(TestObjectFactory.byTestId('page-size-100'))
    assert waitForQuery('pageSize', '100') : 'Page-size control must update the URL to 100.'
    page.waitUntilLoaded()
    assert waitForRowCount(100) : 'The table must finish rendering the 100-row page.'
    assert rows().size() == 100 : 'The table must render 100 rows.'

    page.openWithQuery('preset=30d&tz=UTC&page=999&pageSize=20&sort=amount&order=asc')
    page.waitUntilLoaded()
    assert WebUI.getText(TestObjectFactory.byTestId('pagination-info')).trim() == 'Page 28 of 28' :
        'An oversized page must display the last available page.'
    assert UrlState.firstQueryValue(WebUI.getUrl(), 'page') == '999' : 'The requested page number must remain in the URL.'
    assert rows().size() == 4 : 'The last page of 544 results at size 20 must contain four rows.'
    assert WebUI.getAttribute(TestObjectFactory.byTestId('pagination-next'), 'disabled') != null :
        'Next must be disabled on the last rendered page.'

    page.openWithQuery('preset=custom&from=2027-01-01&to=2027-01-31&tz=UTC')
    assert WebUI.waitForElementVisible(TestObjectFactory.byTestId('empty-state'), 15) :
        'A date range outside the fixed seed must show the empty state.'
    assert WebUI.verifyElementNotPresent(TestObjectFactory.byTestId('transaction-row'), 2, FailureHandling.OPTIONAL) :
        'The empty state must contain no transaction rows.'
}
finally {
    WebUI.closeBrowser()
}

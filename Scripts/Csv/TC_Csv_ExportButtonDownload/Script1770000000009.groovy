import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import pages.LoginPage as LoginPage
import pages.TransactionsPage as TransactionsPage
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import utils.BrowserDownload as BrowserDownload
import utils.CsvParser as CsvParser
import utils.TestObjectFactory as TestObjectFactory

String query = 'preset=30d&tz=UTC&status=PAID&sort=amount&order=desc'

String expectedFileName = 'transactions-2025-12-03-2026-01-01-UTC.csv'

try {
    WebUI.openBrowser(GlobalVariable.baseUrl)

    WebUI.maximizeWindow()

    new LoginPage().open().signInAsManager()

    assert WebUI.waitForElementVisible(findTestObject('Shared/Dashboard_Page'), 10) :
        'Dashboard must be visible after successful Manager login.'

    TransactionsPage transactionsPage = new TransactionsPage()

    transactionsPage.openWithQuery(query)

    transactionsPage.waitUntilLoaded()

    def downloadTarget = BrowserDownload.prepare(expectedFileName)

    def exportButton = TestObjectFactory.byTestId('export-csv-button')

    assert WebUI.waitForElementClickable(exportButton, 10) : 'The Manager Export CSV button must be clickable.'

    WebUI.click(exportButton)

    File downloadedFile = BrowserDownload.waitForCompleted(downloadTarget)

    List<List> rows = CsvParser.parse(downloadedFile.getText('UTF-8'))

    assert downloadedFile.name == expectedFileName : 'The Export button must download the deterministic filename.'

    assert (rows[0]) == ['Transaction ID', 'Date (ISO UTC)', 'Date (Local)', 'Customer', 'Category', 'Amount', 'Status'] : 'The downloaded file must have the CSV contract header.'

    assert rows.size() > 1 : 'The filtered Export button download must contain data rows.'

    assert rows.drop(1).every({ def row ->
            (row.size() == 7) && ((row[6]) == 'PAID')
        }) : 'The downloaded file must honor the active PAID filter.'
}
finally { 
    WebUI.closeBrowser()
}


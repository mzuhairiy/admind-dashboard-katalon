import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import pages.LoginPage as LoginPage
import pages.TransactionsPage as TransactionsPage
import utils.ApiClient as ApiClient
import utils.CsvParser as CsvParser
import internal.GlobalVariable as GlobalVariable
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint

String query = 'preset=30d&tz=UTC&status=PAID&page=2&pageSize=20&sort=amount&order=desc'

try {
    WebUI.openBrowser(GlobalVariable.baseUrl)
    WebUI.maximizeWindow()

    new LoginPage().open().signInAsManager()

    assert WebUI.waitForElementVisible(findTestObject('Shared/Dashboard_Page'), 10) :
        'Dashboard must be visible after successful Manager login.'

    TransactionsPage transactionsPage = new TransactionsPage()

    transactionsPage.openWithQuery(query)

    transactionsPage.waitUntilLoaded()

    def tableRow = transactionsPage.firstTransaction()

    ApiClient apiClient = new ApiClient()

    apiClient.signInAsManager()

    def response = apiClient.get("/api/export/csv?$query")

    assert response.statusCode == 200 : 'A Manager export request must succeed.'

    assert response.header('Content-Type').contains('text/csv') : 'The export response must be CSV.'

    assert response.header('Content-Disposition').contains('transactions-2025-12-03-2026-01-01-UTC.csv') : 'The export filename must reflect the active period and timezone.'

    List<List> rows = CsvParser.parse(response.body)

    assert (rows[0]) == ['Transaction ID', 'Date (ISO UTC)', 'Date (Local)', 'Customer', 'Category', 'Amount', 'Status'] : 'CSV headers must match the contract.'

    assert rows.size() > 21 : 'CSV export must contain all matching rows, not only the current table page.'

    assert rows.drop(1).every({ def row ->
            (row.size() == 7) && ((row[6]) == 'PAID')
        }) : 'Every exported row must honor the PAID status filter.'

    List<String> csvRowForFirstTableRow = rows[21 // Header + 20 records from table page 1.
    ]

    assert (csvRowForFirstTableRow[0]) == tableRow.id : 'CSV sort order must match the rendered table.'

    assert (csvRowForFirstTableRow[1]) == tableRow.isoUtc : 'CSV must retain the transaction UTC instant.'

    assert (csvRowForFirstTableRow[5]) == tableRow.amount : 'CSV must retain the raw transaction amount.'

    assert (csvRowForFirstTableRow[6]) == tableRow.status : 'CSV must retain the transaction status.'
}
finally { 
    WebUI.closeBrowser()
}


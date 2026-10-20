package pages

import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import utils.TestObjectFactory

class TransactionsPage extends BasePage {
    private static final TestObject PAGE = TestObjectFactory.byTestId('transactions-page')
    private static final TestObject TABLE = TestObjectFactory.byTestId('transactions-table')
    private static final TestObject FIRST_ROW_DATE = TestObjectFactory.css(
        "[data-testid='transaction-row']:first-of-type [data-testid='column-date']",
        'first_transaction_date'
    )

    void openWithQuery(String query) {
        openPath("/transactions?${query}")
    }

    void waitUntilLoaded() {
        waitFor(PAGE, 'the transactions page')
        waitFor(TABLE, 'the transactions table')
        waitFor(FIRST_ROW_DATE, 'the first transaction date')
    }

    TransactionDate firstTransactionDate() {
        return new TransactionDate(
            WebUI.getText(FIRST_ROW_DATE).trim(),
            WebUI.getAttribute(FIRST_ROW_DATE, 'data-iso')
        )
    }

    TransactionSummary firstTransaction() {
        return new TransactionSummary(
            WebUI.getAttribute(TestObjectFactory.css("[data-testid='transaction-row']:first-of-type", 'first_transaction'), 'data-tx-id'),
            WebUI.getAttribute(FIRST_ROW_DATE, 'data-iso'),
            WebUI.getAttribute(TestObjectFactory.css("[data-testid='transaction-row']:first-of-type [data-testid='column-amount']", 'first_transaction_amount'), 'data-value'),
            WebUI.getText(TestObjectFactory.css("[data-testid='transaction-row']:first-of-type [data-testid='column-status']", 'first_transaction_status')).trim()
        )
    }

    void clickExport() {
        TestObject exportButton = TestObjectFactory.byTestId('export-csv-button')
        waitFor(exportButton, 'the Export CSV button')
        WebUI.click(exportButton)
    }

    static class TransactionDate {
        final String visibleText
        final String isoUtc

        TransactionDate(String visibleText, String isoUtc) {
            this.visibleText = visibleText
            this.isoUtc = isoUtc
        }
    }

    static class TransactionSummary {
        final String id
        final String isoUtc
        final String amount
        final String status

        TransactionSummary(String id, String isoUtc, String amount, String status) {
            this.id = id
            this.isoUtc = isoUtc
            this.amount = amount
            this.status = status
        }
    }
}

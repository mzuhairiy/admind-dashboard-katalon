package utils

import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject

/** Builds scoped, data-testid based objects when a native repository object is not appropriate. */
class TestObjectFactory {
    static TestObject byTestId(String testId, String name = null) {
        return css("[data-testid='${cssLiteral(testId)}']", name ?: "testid_${testId}")
    }

    static TestObject childByTestId(String containerTestId, String childTestId, String name = null) {
        return css("[data-testid='${cssLiteral(containerTestId)}'] [data-testid='${cssLiteral(childTestId)}']",
            name ?: "${containerTestId}_${childTestId}")
    }

    static TestObject transactionRow(String transactionId) {
        return css("[data-testid='transaction-row'][data-tx-id='${cssLiteral(transactionId)}']", "transaction_${transactionId}")
    }

    static TestObject css(String selector, String name = 'dynamic_css') {
        TestObject object = new TestObject(name)
        object.addProperty('css', ConditionType.EQUALS, selector)
        return object
    }

    private static String cssLiteral(String value) {
        if (value == null || value.contains("'")) {
            throw new IllegalArgumentException('Test-id and attribute values must be non-null and must not contain a single quote.')
        }
        return value
    }
}

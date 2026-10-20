package pages

import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.ObjectRepository

class DashboardPage extends BasePage {
    private static final TestObject PAGE = ObjectRepository.findTestObject('Object Repository/Shared/Dashboard_Page')

    void waitUntilLoaded() { waitFor(PAGE, 'the dashboard page') }
}

package pages

import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.ObjectRepository

class RevenuePage extends BasePage {
    private static final TestObject PAGE = ObjectRepository.findTestObject('Object Repository/Shared/Revenue_Page')
    private static final TestObject FORBIDDEN = ObjectRepository.findTestObject('Object Repository/Shared/Forbidden_Page')

    void open() { openPath('/revenue') }
    void waitUntilLoaded() { waitFor(PAGE, 'the revenue page') }
    void waitUntilForbidden() { waitFor(FORBIDDEN, 'the forbidden page') }
}

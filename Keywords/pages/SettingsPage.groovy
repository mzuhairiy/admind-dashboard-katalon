package pages

import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import utils.TestObjectFactory

class SettingsPage extends BasePage {
    private static final TestObject PAGE = TestObjectFactory.byTestId('settings-page')
    private static final TestObject TIMEZONE = TestObjectFactory.byTestId('timezone-select')
    private static final TestObject CURRENCY = TestObjectFactory.byTestId('currency-select')

    void open() {
        openPath('/settings')
        waitFor(PAGE, 'the settings page')
    }

    String selectedTimezone() { selectedText(TIMEZONE) }
    String selectedCurrency() { selectedText(CURRENCY) }

    void selectTimezone(String timezone) {
        String slug = timezone.replace('/', '-').toLowerCase(Locale.ROOT)
        choose(TIMEZONE, "timezone-option-${slug}")
    }

    void selectCurrency(String currency) {
        choose(CURRENCY, "currency-option-${currency.toLowerCase(Locale.ROOT)}")
    }

    void save() {
        TestObject savedToast = TestObjectFactory.byTestId('settings-saved-toast')
        assert WebUI.waitForElementNotPresent(savedToast, 10) :
            'The previous save toast must disappear before submitting again.'
        WebUI.click(TestObjectFactory.byTestId('settings-save-button'))
        waitFor(savedToast, 'the settings saved toast')
    }

    private void choose(TestObject trigger, String optionId) {
        WebUI.click(trigger)
        TestObject option = TestObjectFactory.byTestId(optionId)
        waitFor(option, optionId)
        WebUI.click(option)
    }

    private String selectedText(TestObject trigger) {
        waitFor(trigger, 'the settings selection')
        long deadline = System.currentTimeMillis() + timeout * 1000L
        while (System.currentTimeMillis() < deadline) {
            String value = WebUI.getText(trigger).trim()
            if (value) return value
            Thread.sleep(200)
        }
        throw new AssertionError("Selection did not hydrate: ${trigger.objectId}")
    }
}

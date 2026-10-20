import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import pages.LoginPage as LoginPage
import pages.SettingsPage as SettingsPage
import utils.TestObjectFactory as TestObjectFactory

SettingsPage settings = new SettingsPage()
String originalTimezone = null
String originalCurrency = null
boolean browserOpened = false

try {
    WebUI.openBrowser(GlobalVariable.baseUrl)
    browserOpened = true
    new LoginPage().open().signInAsManager()

    settings.open()
    originalTimezone = settings.selectedTimezone()
    originalCurrency = settings.selectedCurrency()
    assert originalTimezone && originalCurrency : 'Existing preferences must be readable before changing them.'

    String newTimezone = originalTimezone == 'UTC' ? 'Asia/Tokyo' : 'UTC'
    String newCurrency = originalCurrency == 'USD' ? 'SGD' : 'USD'
    settings.selectTimezone(newTimezone)
    settings.selectCurrency(newCurrency)
    settings.save()

    WebUI.navigateToUrl("${GlobalVariable.baseUrl}/dashboard?preset=30d")
    def filterTimezone = TestObjectFactory.byTestId('filter-timezone')
    assert WebUI.waitForElementVisible(filterTimezone, 15) : 'The dashboard timezone filter must load.'
    long deadline = System.currentTimeMillis() + 15_000
    while (WebUI.getText(filterTimezone).trim() != newTimezone && System.currentTimeMillis() < deadline) {
        Thread.sleep(200)
    }
    assert WebUI.getText(filterTimezone).trim() == newTimezone :
        'The saved timezone must become the filter default on the next page load.'

    WebUI.navigateToUrl("${GlobalVariable.baseUrl}/dashboard?preset=30d&tz=UTC")
    def revenue = TestObjectFactory.byTestId('stats-value-revenue')
    assert WebUI.waitForElementVisible(revenue, 15) : 'The dashboard revenue KPI must load.'
    assert WebUI.getAttribute(revenue, 'data-value') == '225641000' :
        'Changing display currency must not convert the raw UTC 30D revenue.'
    assert !WebUI.getText(revenue).contains('Rp') : 'USD/SGD display must not use the IDR symbol.'

    settings.open()
    assert settings.selectedTimezone() == newTimezone : 'Timezone selection must persist after navigation.'
    assert settings.selectedCurrency() == newCurrency : 'Currency selection must persist after navigation.'
}
finally {
    if (browserOpened) {
        try {
            if (originalTimezone && originalCurrency) {
                settings.open()
                settings.selectTimezone(originalTimezone)
                settings.selectCurrency(originalCurrency)
                settings.save()
                settings.open()
                assert settings.selectedTimezone() == originalTimezone : 'Original timezone must be restored.'
                assert settings.selectedCurrency() == originalCurrency : 'Original currency must be restored.'
            }
        }
        finally {
            WebUI.closeBrowser()
        }
    }
}

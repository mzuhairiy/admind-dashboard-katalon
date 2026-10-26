import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import pages.DashboardPage as DashboardPage
import pages.LoginPage as LoginPage
import pages.RevenuePage as RevenuePage
import pages.SettingsPage as SettingsPage
import utils.TestObjectFactory as TestObjectFactory
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

SettingsPage settings = new SettingsPage()
String originalCurrency = null
boolean browserOpened = false

def captureTooltipValue = { String pointTestId, String currency ->
    TestObject point = TestObjectFactory.byTestId(pointTestId)
    assert WebUI.waitForElementVisible(point, 15) : "Chart point '${pointTestId}' must be visible."
    String rawValue = WebUI.getAttribute(point, 'data-value')
    assert rawValue != null : "Chart point '${pointTestId}' must expose its raw data-value."

    WebUI.mouseOver(point)

    TestObject tooltip = TestObjectFactory.byTestId('chart-tooltip')
    assert WebUI.waitForElementVisible(tooltip, 10) : "Tooltip for '${pointTestId}' must appear on hover."
    assert WebUI.getAttribute(tooltip, 'data-currency') == currency :
        "Tooltip container must identify selected currency '${currency}'."

    TestObject tooltipValueObjects = TestObjectFactory.allByTestId('chart-tooltip-value')
    assert WebUI.waitForElementVisible(tooltipValueObjects, 10) :
        "Tooltip '${pointTestId}' must expose at least one formatted monetary value."
    List tooltipValues = WebUI.findWebElements(tooltipValueObjects, 10)
    def tooltipValue = tooltipValues.find { element ->
        element.isDisplayed() && element.getAttribute('data-raw-value') == rawValue
    }
    assert tooltipValue != null :
        "Tooltip '${pointTestId}' must expose raw value '${rawValue}' in chart-tooltip-value."
    assert tooltipValue.getAttribute('data-currency') == currency :
        "Tooltip value must identify selected currency '${currency}'."

    String formattedText = tooltipValue.getText().trim()
    assert formattedText && formattedText != rawValue :
        "Tooltip value '${pointTestId}' must display a formatted currency, not the raw number '${rawValue}'."
    if (currency != 'IDR') {
        assert !formattedText.contains('Rp') : "${currency} tooltip must not display the IDR symbol."
    }

    return [raw: rawValue, text: formattedText]
}

try {
    WebUI.openBrowser(GlobalVariable.baseUrl)
    WebUI.maximizeWindow()
    browserOpened = true

    new LoginPage().open().signInAsManager()
    assert WebUI.waitForElementVisible(findTestObject('Shared/Dashboard_Page'), 10) :
        'Dashboard must be visible after successful Manager login.'

    settings.open()
    originalCurrency = settings.selectedCurrency()
    assert originalCurrency in ['IDR', 'USD', 'SGD'] :
        "Unexpected original currency preference '${originalCurrency}'."

    Map resultsByCurrency = [:]
    ['IDR', 'USD', 'SGD'].each { String currency ->
        settings.open()
        if (settings.selectedCurrency() != currency) {
            settings.selectCurrency(currency)
            settings.save()
        }

        WebUI.navigateToUrl("${GlobalVariable.baseUrl}/dashboard?preset=30d&tz=UTC")
        new DashboardPage().waitUntilAnalyticsLoaded()
        Map chartValues = [
            revenueTrend: captureTooltipValue('chart-revenue-trend-point', currency),
            topCategory: captureTooltipValue('chart-top-categories-bar', currency)
        ]

        WebUI.navigateToUrl("${GlobalVariable.baseUrl}/revenue?preset=30d&tz=UTC")
        new RevenuePage().waitUntilLoaded()
        chartValues.revenueBreakdown = captureTooltipValue('revenue-breakdown-segment', currency)
        resultsByCurrency[currency] = chartValues
    }

    ['USD', 'SGD'].each { String currency ->
        ['revenueTrend', 'topCategory', 'revenueBreakdown'].each { String chartName ->
            def idrValue = resultsByCurrency['IDR'][chartName]
            def selectedValue = resultsByCurrency[currency][chartName]
            assert selectedValue.raw == idrValue.raw :
                "${chartName} raw amount must remain unchanged in ${currency}; IDR=${idrValue.raw}, ${currency}=${selectedValue.raw}."
            assert selectedValue.text != idrValue.text :
                "${chartName} tooltip formatting must respond to the ${currency} preference."
        }
    }
}
finally {
    if (browserOpened) {
        try {
            if (originalCurrency) {
                settings.open()
                if (settings.selectedCurrency() != originalCurrency) {
                    settings.selectCurrency(originalCurrency)
                    settings.save()
                }
                settings.open()
                assert settings.selectedCurrency() == originalCurrency :
                    'Original currency preference must be restored.'
            }
        }
        finally {
            WebUI.closeBrowser()
        }
    }
}

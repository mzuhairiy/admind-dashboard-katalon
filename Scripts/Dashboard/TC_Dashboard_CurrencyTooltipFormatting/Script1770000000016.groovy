import com.kms.katalon.core.testobject.TestObject
import org.openqa.selenium.WebElement
import org.openqa.selenium.interactions.Actions
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.webui.driver.DriverFactory
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

def captureTooltipValue = { String chartTestId, String pointTestId, String currency ->
    TestObject points = TestObjectFactory.allByTestId(pointTestId)
    assert WebUI.waitForElementVisible(points, 15) : "Chart point '${pointTestId}' must be visible."
    TestObject chart = TestObjectFactory.byTestId(chartTestId)
    assert WebUI.waitForElementVisible(chart, 10) : "Chart '${chartTestId}' must be visible before hovering."
    WebUI.scrollToElement(chart, 5)

    WebElement chartElement = WebUI.findWebElement(chart, 10)
    List<WebElement> visiblePoints = WebUI.findWebElements(points, 10).findAll { WebElement candidate ->
        candidate.isDisplayed() && candidate.size.width > 0 && candidate.size.height > 0 &&
            candidate.getAttribute('data-value') != null
    }
    assert !visiblePoints.isEmpty() : "Chart '${chartTestId}' has no visible, hoverable '${pointTestId}' points."

    // Hover an actual rendered SVG point. Calculating an offset from the outer
    // chart wrapper can miss the Recharts SVG surface in headless Chrome.
    WebElement pointElement = visiblePoints.min { WebElement candidate ->
        Math.abs(candidate.location.x - chartElement.location.x - chartElement.size.width.intdiv(2)) +
            Math.abs(candidate.location.y - chartElement.location.y - chartElement.size.height.intdiv(2))
    }
    String rawValue = pointElement.getAttribute('data-value')
    // Move across the chart before targeting the SVG point so the browser emits
    // a fresh mousemove even if the pointer was already near this chart.
    new Actions(DriverFactory.getWebDriver())
        .moveToElement(chartElement)
        .moveToElement(pointElement)
        .perform()

    TestObject tooltip = TestObjectFactory.byTestId('chart-tooltip')
    boolean tooltipVisible = WebUI.waitForElementVisible(tooltip, 10)
    String hoverDiagnostics = ''
    if (!tooltipVisible) {
        try {
            hoverDiagnostics = WebUI.executeJavaScript('''
                const point = arguments[0];
                const chart = arguments[1];
                const rect = point.getBoundingClientRect();
                const x = rect.left + rect.width / 2;
                const y = rect.top + rect.height / 2;
                const hit = document.elementFromPoint(x, y);
                return JSON.stringify({
                    viewport: [window.innerWidth, window.innerHeight],
                    point: [rect.left, rect.top, rect.width, rect.height],
                    chart: (() => { const r = chart.getBoundingClientRect(); return [r.left, r.top, r.width, r.height]; })(),
                    hit: hit ? { tag: hit.tagName, testId: hit.getAttribute('data-testid'), className: String(hit.getAttribute('class')) } : null,
                    pointHovered: point.matches(':hover'),
                    chartHovered: chart.matches(':hover')
                });
            ''', [pointElement, chartElement]) as String
        } catch (Exception diagnosticError) {
            hoverDiagnostics = "unavailable (${diagnosticError.class.simpleName})"
        }
    }
    assert tooltipVisible :
        "Tooltip for '${pointTestId}' did not appear after native hover; browser hit-test=${hoverDiagnostics}; URL=${WebUI.getUrl()}."
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
            revenueTrend: captureTooltipValue('chart-revenue-trend', 'chart-revenue-trend-point', currency),
            topCategory: captureTooltipValue('chart-top-categories', 'chart-top-categories-bar', currency)
        ]

        WebUI.navigateToUrl("${GlobalVariable.baseUrl}/revenue?preset=30d&tz=UTC")
        new RevenuePage().waitUntilLoaded()
        chartValues.revenueBreakdown = captureTooltipValue(
            'revenue-breakdown-chart', 'revenue-breakdown-segment', currency
        )
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

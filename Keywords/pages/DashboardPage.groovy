package pages

import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.ObjectRepository
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import org.openqa.selenium.WebElement
import utils.TestObjectFactory

class DashboardPage extends BasePage {
    private static final TestObject PAGE = ObjectRepository.findTestObject('Object Repository/Shared/Dashboard_Page')
    private static final List<String> KPI_NAMES = ['revenue', 'orders', 'aov', 'refund-rate']
    private static final List<String> CHART_POINT_TEST_IDS = [
        'chart-revenue-trend-point',
        'chart-top-categories-bar',
        'chart-order-status-slice'
    ]

    void waitUntilLoaded() { waitFor(PAGE, 'the dashboard page') }

    void openWithQuery(String query) {
        openPath("/dashboard?${query}")
    }

    void waitUntilAnalyticsLoaded() {
        waitUntilLoaded()
        CHART_POINT_TEST_IDS.each { testId ->
            waitFor(TestObjectFactory.byTestId(testId), "at least one ${testId}")
        }
    }

    BigDecimal kpiValue(String name) {
        assert KPI_NAMES.contains(name) : "Unsupported dashboard KPI: ${name}"
        return decimalAttribute(TestObjectFactory.byTestId("stats-value-${name}"), 'data-value')
    }

    boolean isDatePresetSelected(String preset) {
        return WebUI.getAttribute(TestObjectFactory.byTestId("date-preset-${preset}"), 'aria-pressed') == 'true'
    }

    String filterText(String testId) {
        return WebUI.getText(TestObjectFactory.byTestId(testId)).trim()
    }

    BigDecimal sumChartValues(String pointTestId) {
        assert CHART_POINT_TEST_IDS.contains(pointTestId) : "Unsupported chart point: ${pointTestId}"
        List<WebElement> points = WebUI.findWebElements(TestObjectFactory.allByTestId(pointTestId), timeout)
        assert !points.isEmpty() : "No ${pointTestId} elements were rendered."
        return points.collect { point -> new BigDecimal(point.getAttribute('data-value')) }
            .inject(BigDecimal.ZERO) { total, value -> total + value }
    }

    private BigDecimal decimalAttribute(TestObject object, String attribute) {
        String value = WebUI.getAttribute(object, attribute)
        assert value : "${object.objectId} did not have a ${attribute} attribute."
        return new BigDecimal(value)
    }
}

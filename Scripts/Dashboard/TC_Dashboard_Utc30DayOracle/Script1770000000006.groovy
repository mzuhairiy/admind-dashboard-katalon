import pages.DashboardPage as DashboardPage
import pages.LoginPage as LoginPage
import internal.GlobalVariable as GlobalVariable
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
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

try {
    WebUI.openBrowser(GlobalVariable.baseUrl)

    WebUI.maximizeWindow()

    new LoginPage().open().signInAsViewer()

    DashboardPage dashboardPage = new DashboardPage()

    assert WebUI.waitForElementVisible(findTestObject('Shared/Dashboard_Page'), 10) :
        'Dashboard must be visible after successful Viewer login.'

    dashboardPage.openWithQuery('preset=30d&tz=UTC')

    dashboardPage.waitUntilAnalyticsLoaded()

    assert WebUI.getUrl().contains('preset=30d') : 'Dashboard must retain the explicit 30-day preset.'

    assert WebUI.getUrl().contains('tz=UTC') : 'Dashboard must retain the explicit UTC timezone.'

    assert dashboardPage.kpiValue('revenue') == new BigDecimal('225641000') : 'UTC 30-day revenue must match the independent seed oracle.'

    assert dashboardPage.kpiValue('orders') == new BigDecimal('544') : 'UTC 30-day order count must match the independent seed oracle.'

    assert dashboardPage.kpiValue('aov') == new BigDecimal('480087') : 'UTC 30-day AOV must match the independent seed oracle.'

    assert dashboardPage.kpiValue('refund-rate') == new BigDecimal('6.1') : 'UTC 30-day refund rate must match the independent seed oracle.'

    assert dashboardPage.sumChartValues('chart-revenue-trend-point') == dashboardPage.kpiValue('revenue') : 'Revenue trend points must sum to total revenue.'

    assert dashboardPage.sumChartValues('chart-top-categories-bar') == dashboardPage.kpiValue('revenue') : 'Category bars must sum to total revenue.'

    assert dashboardPage.sumChartValues('chart-order-status-slice') == dashboardPage.kpiValue('orders') : 'Status slices must sum to total orders.'
}
finally { 
    WebUI.closeBrowser()
}


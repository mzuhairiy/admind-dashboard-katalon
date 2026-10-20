import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import pages.DashboardPage as DashboardPage
import pages.LoginPage as LoginPage
import internal.GlobalVariable as GlobalVariable

try {
    WebUI.openBrowser(GlobalVariable.baseUrl)

    WebUI.maximizeWindow()

    new LoginPage().open().signInAsViewer()

    new DashboardPage().waitUntilLoaded()

    assert WebUI.getUrl().contains('/dashboard')
}
finally { 
    WebUI.closeBrowser()
}


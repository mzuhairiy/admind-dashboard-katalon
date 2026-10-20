package pages

import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable
import utils.TestObjectFactory

abstract class BasePage {
	protected final int timeout = (GlobalVariable.defaultTimeout ?: 15) as int

	protected void openPath(String path) {
		String normalizedPath = path.startsWith('/') ? path : "/${path}"
		WebUI.navigateToUrl("${GlobalVariable.baseUrl}${normalizedPath}")
	}

	protected void waitFor(TestObject object, String description) {
		if (!WebUI.waitForElementVisible(object, timeout)) {
			throw new StepFailedException("Timed out after ${timeout}s waiting for ${description}.")
		}
	}

	protected boolean isPresent(String testId) {
		return WebUI.verifyElementPresent(TestObjectFactory.byTestId(testId), 2,
				com.kms.katalon.core.model.FailureHandling.OPTIONAL)
	}
}

package pages

import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.ObjectRepository
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable

class LoginPage extends BasePage {
	private static final TestObject FORM = ObjectRepository.findTestObject('Object Repository/Authentication/SignIn_Form')
	private static final TestObject EMAIL = ObjectRepository.findTestObject('Object Repository/Authentication/SignIn_Email')
	private static final TestObject PASSWORD = ObjectRepository.findTestObject('Object Repository/Authentication/SignIn_Password')
	private static final TestObject SUBMIT = ObjectRepository.findTestObject('Object Repository/Authentication/SignIn_Submit')
	private static final TestObject ERROR = ObjectRepository.findTestObject('Object Repository/Authentication/SignIn_Error')

	LoginPage open(String callbackPath = null) {
		openPath(callbackPath ? "/sign-in?callbackUrl=${URLEncoder.encode(callbackPath, 'UTF-8')}" : '/sign-in')
		waitFor(FORM, 'the sign-in form')
		return this
	}

	void signIn(String email, String password) {
		WebUI.setText(EMAIL, email)
		// Profile values are plain by design; do not use setEncryptedText here.
		WebUI.setText(PASSWORD, password)
		WebUI.click(SUBMIT)
	}

	void signInAsViewer() {
		signIn(GlobalVariable.viewerEmail as String, GlobalVariable.viewerPassword as String)
	}

	void signInAsManager() {
		signIn(GlobalVariable.managerEmail as String, GlobalVariable.managerPassword as String)
	}

	String errorMessage() {
		waitFor(ERROR, 'the sign-in validation error')
		return WebUI.getText(ERROR)
	}
}

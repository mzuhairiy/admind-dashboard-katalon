package utils

import groovy.json.JsonSlurper
import internal.GlobalVariable

import java.net.CookieManager
import java.net.CookiePolicy
import java.net.HttpURLConnection
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * Small stateful HTTP client for API-only test cases. Each instance owns its
 * cookie jar, so role/session state cannot leak between scenarios.
 */
class ApiClient {
	private final String baseUrl = (GlobalVariable.baseUrl as String).replaceAll('/+$', '')
	private final CookieManager cookies = new CookieManager(null, CookiePolicy.ACCEPT_ALL)

	ApiResponse get(String path) {
		return request('GET', path)
	}

	void signInAsViewer() {
		signIn(GlobalVariable.viewerEmail.toString(), GlobalVariable.viewerPassword.toString())
	}

	void signInAsManager() {
		signIn(GlobalVariable.managerEmail.toString(), GlobalVariable.managerPassword.toString())
	}

	void signIn(String email, String password) {
		ApiResponse csrfResponse = get('/api/auth/csrf')
		assert csrfResponse.statusCode == 200 : "CSRF request failed: ${csrfResponse.statusCode} ${csrfResponse.body}"

		String csrfToken = new JsonSlurper().parseText(csrfResponse.body).csrfToken
		assert csrfToken : 'CSRF response did not contain csrfToken.'

		String form = formUrlEncoded([
			csrfToken: csrfToken,
			email    : email,
			password : password,
			json     : 'true'
		])
		ApiResponse signInResponse = request('POST', '/api/auth/callback/credentials', [
			'Content-Type': 'application/x-www-form-urlencoded'
		], form)
		assert [200, 302, 303].contains(signInResponse.statusCode) :
		"Sign-in request failed: ${signInResponse.statusCode} ${signInResponse.body}"

		// Auth.js normally completes a credentials sign-in with a redirect.
		// Verify the resulting cookie instead of treating the redirect itself
		// as proof that authentication succeeded (failed logins can redirect too).
		ApiResponse sessionResponse = get('/api/auth/session')
		assert sessionResponse.statusCode == 200 :
		"Session verification failed: ${sessionResponse.statusCode} ${sessionResponse.body}"
		def session = new JsonSlurper().parseText(sessionResponse.body)
		assert session.user?.email == email :
		"Sign-in did not create the expected session for ${email}: ${sessionResponse.body}"
	}

	private ApiResponse request(String method, String path, Map<String, String> requestHeaders = [:], String requestBody = null) {
		URI uri = new URI("${baseUrl}${path}")
		HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection()
		connection.requestMethod = method
		connection.instanceFollowRedirects = false
		connection.connectTimeout = 15_000
		connection.readTimeout = 15_000
		connection.setRequestProperty('Accept', '*/*')

		Map<String, List<String>> cookieHeaders = cookies.get(uri, [:])
		cookieHeaders.each { name, values ->
			connection.setRequestProperty(name, values.join('; '))
		}
		requestHeaders.each { name, value -> connection.setRequestProperty(name, value) }

		if (requestBody != null) {
			byte[] bodyBytes = requestBody.getBytes(StandardCharsets.UTF_8)
			connection.doOutput = true
			connection.setFixedLengthStreamingMode(bodyBytes.length)
			connection.outputStream.withCloseable { output -> output.write(bodyBytes) }
		}

		int statusCode = connection.responseCode
		cookies.put(uri, connection.headerFields)
		InputStream stream = statusCode >= 400 ? connection.errorStream : connection.inputStream
		String body = stream == null ? '' : stream.getText(StandardCharsets.UTF_8.name())
		return new ApiResponse(statusCode, body, connection.headerFields)
	}

	private static String formUrlEncoded(Map<String, String> values) {
		return values.collect { key, value ->
			"${URLEncoder.encode(key, StandardCharsets.UTF_8.name())}=${URLEncoder.encode(value, StandardCharsets.UTF_8.name())}"
		}.join('&')
	}

	static class ApiResponse {
		final int statusCode
		final String body
		final Map<String, List<String>> headers

		ApiResponse(int statusCode, String body, Map<String, List<String>> headers) {
			this.statusCode = statusCode
			this.body = body
			this.headers = headers
		}

		String header(String name) {
			List<String> values = headers.find { key, ignored -> key?.equalsIgnoreCase(name) }?.value
			return values?.join(', ')
		}
	}
}

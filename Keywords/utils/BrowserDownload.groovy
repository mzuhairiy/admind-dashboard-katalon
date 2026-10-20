package utils

import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.webui.driver.DriverFactory
import org.openqa.selenium.WebDriver

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/** Chrome download setup and bounded file waiting for local WebUI tests. */
class BrowserDownload {
	static Path prepare(String fileName) {
		Path directory = Paths.get(RunConfiguration.getProjectDir(), 'Downloads').toAbsolutePath().normalize()
		Files.createDirectories(directory)
		Path target = directory.resolve(fileName).normalize()
		assert target.parent == directory : 'Download filename must remain inside the project Downloads directory.'
		Files.deleteIfExists(target)
		Files.deleteIfExists(Paths.get("${target}.crdownload"))

		WebDriver driver = DriverFactory.getWebDriver()
		assert driver.metaClass.respondsTo(driver, 'executeCdpCommand', String, Map) :
		'The export-button download smoke test currently requires Chrome/Chromium.'
		driver.executeCdpCommand('Page.setDownloadBehavior', [
			behavior    : 'allow',
			downloadPath: directory.toString()
		])
		return target
	}

	static File waitForCompleted(Path target, int timeoutSeconds = 20) {
		long deadline = System.currentTimeMillis() + timeoutSeconds * 1000L
		Path partial = Paths.get("${target}.crdownload")
		while (System.currentTimeMillis() < deadline) {
			if (Files.isRegularFile(target) && Files.size(target) > 0 && !Files.exists(partial)) {
				return target.toFile()
			}
			Thread.sleep(250)
		}
		throw new AssertionError("Download did not complete within ${timeoutSeconds}s: ${target}")
	}
}

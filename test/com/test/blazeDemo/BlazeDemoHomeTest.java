package com.test.blazeDemo;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

import javax.imageio.ImageIO;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class BlazeDemoHomeTest {

	protected WebDriver driver;
	protected static String baseUrl = "https://blazedemo.com/";
	public static Logger log = LogManager.getLogger(BlazeDemoHomeTest.class.getName());
	
	@BeforeClass
	public void launchBrowser() {
		
		try {
			System.out.println("launching Chrome browser");
			
			InputStream ip = BlazeDemoHomeTest.class.getResourceAsStream("/config.properties");
			Properties p = new Properties();
			p.load(ip);
			String driverName = p.getProperty("driver");
			String driverPath = p.getProperty("driverPath");
			
			System.setProperty("webdriver.chrome.driver", driverPath);

			boolean isValidDriver = Class.forName(driverName).newInstance() instanceof WebDriver;
			if (isValidDriver) {
				driver = (WebDriver) Class.forName(driverName).newInstance();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@BeforeMethod
	public void setHome() {
		driver.navigate().to(baseUrl);
	}

	@Test(groups = "HomePage", priority = 0)
	public void verifyHomepageTitle() {
		String expectedTitle = "BlazeDemo";
		String actualTitle = driver.getTitle();
		Assert.assertEquals(actualTitle, expectedTitle);
		log.info("Title of page is correct");
	}
	
	@Test(groups = "HomePage")
	public void verifyDepartureOptionsPresent() {
		WebElement ele = driver.findElement(By.name("fromPort"));
		Assert.assertNotEquals(ele, null);
		Assert.assertEquals(ele.getTagName(), "select");
		log.info("Departure options are present");
	}

	@Test(groups = "HomePage")
	public void verifyArrivalOptionsPresent() {
		WebElement ele = driver.findElement(By.name("toPort"));
		Assert.assertNotEquals(ele, null);
		Assert.assertEquals(ele.getTagName(), "select");
		log.info("Arrival options are present");
	}

	@Test(groups = "HomePage", priority = 1)
	public void verifyAllLinks() {
		HttpURLConnection huc;
		try {
			List<WebElement> links = driver.findElements(By.xpath(".//a[@href!='']"));
			for (WebElement href : links) {
				huc = (HttpURLConnection)(new URL(href.getAttribute("href")).openConnection());
			    huc.setRequestMethod("HEAD");
			    huc.connect();
			    Assert.assertEquals(String.valueOf(huc.getResponseCode()),"200");
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
	}

	@AfterMethod (alwaysRun = true)
	public void testStatus(ITestResult result) throws IOException {

		if (result.getStatus() == ITestResult.FAILURE) {
			File screen = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			BufferedImage img = ImageIO.read(screen);
			File filetest = Paths.get(".").toAbsolutePath().normalize().toFile();
			System.out.println(Paths.get(".").toAbsolutePath().normalize().toFile());
			ImageIO.write(img, "png", new File(filetest + "\\test-output\\screenshots\\" + "FailedResult"
					+ result.getTestName() + result.getEndMillis() + ".png"));
			log.error(result.getName(), result.getThrowable());
		}
	}

	@AfterClass(alwaysRun = true)
	public void terminateBrowser() {
		log.info("Closing browser");
		driver.quit();
	}
	
}

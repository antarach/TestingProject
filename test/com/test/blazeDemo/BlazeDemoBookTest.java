package com.test.blazeDemo;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
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

/**
 * 
 * @author Antara
 *
 */
public class BlazeDemoBookTest {
 
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
		driver.findElement(By.xpath("//input[@value=\"Find Flights\"]")).submit();
		driver.findElement(By.xpath("//input[@value=\"Choose This Flight\"]")).click();
		
	}

	@Test(groups = "BookPage", priority = 0)
	public void verifyBookPageTitle() {
		String expectedTitle = "BlazeDemo Purchase";
		String actualTitle = driver.getTitle();
		Assert.assertEquals(actualTitle, expectedTitle);
		log.info("Title of page is correct");
	}
	
	@Test(groups = "BookPage", priority = 1)
	public void restrictBookingWithoutDetails() {
			WebElement confirmLink = driver.findElement(By.xpath("//input[@value=\"Purchase Flight\"]"));
			confirmLink.click();
			Assert.assertEquals(driver.getCurrentUrl(), "https://blazedemo.com/purchase.php");
	}
	
	@Test(groups = "BookPage", priority = 1)
	public void restrictOpenConfirmationPage() {
		driver.get("https://blazedemo.com/confirmation.php");
		System.out.println(driver.getTitle());
		Assert.assertEquals(driver.getTitle(), "Error");
	}

	@AfterMethod
	public void testStatus(ITestResult result) throws IOException {

		if (result.getStatus() == ITestResult.FAILURE) {
			File screen = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			BufferedImage img = ImageIO.read(screen);
			File filetest = Paths.get(".").toAbsolutePath().normalize().toFile();
			ImageIO.write(img, "png", new File(filetest + "\\test-output\\screenshots\\" + "FailedResult"
					+ result.getName() + result.getEndMillis() + ".png"));
			log.error(result.getName(), result.getThrowable());
		}
	}

	@AfterClass (alwaysRun = true)
	public void terminateBrowser() {
		log.info("Closing browser");
		driver.quit();
	}
	

}

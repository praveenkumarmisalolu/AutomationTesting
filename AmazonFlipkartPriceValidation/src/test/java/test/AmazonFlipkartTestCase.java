package test;

import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;
import pages.AmazonHomePage;
import pages.FlipkartHomePage;

public class AmazonFlipkartTestCase {

	WebDriver driver;
	AmazonHomePage objAmazonHomePage;
	FlipkartHomePage objFlipkartHomePage;
	String driverPath;
	WebElement elements;
	Properties prop;
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	@BeforeTest
	public void beforeTestMethod() throws IOException {

		try (InputStream input = AmazonFlipkartTestCase.class.getClassLoader()
				.getResourceAsStream("config.properties")) {
			prop = new Properties();
			if (input == null) {
				System.out.println("Sorry, unable to find config.properties");
				return;
			}
			// load a properties file from class path, inside static method
			prop.load(input);
			// get the property value and print it out
			driverPath = prop.getProperty("driverPath");
		}

	}
	
	/**
	 * 
	 *	Chrome browser instantiating
	 */

	public void setup() {
		WebDriverManager.chromedriver().version(prop.getProperty("chromeDriverVersion")).setup();
		ChromeOptions options = new ChromeOptions();
		driver = new ChromeDriver(options); 
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

	}

	/**
	 * 
	 * This test case will login in https://www.amazon.in/
	 * 
	 * search for iPhone XR (64GB) - Yellow
	 * 
	 * Select the matching iPhone once list appears.
	 * 
	 * Return the price of the selected iPhone
	 * @throws IOException 
	 * 
	 * 
	 */
	public int valueOfProductFromAmazon() throws IOException {

		setup();
		// creating object for amazon page
		objAmazonHomePage = new AmazonHomePage(driver);

		// Login home page for amazon
		String homePageDashboardUserName = objAmazonHomePage.getHomePageUserName();
		driver.get(homePageDashboardUserName);
		assertTrue(driver.findElement(By.xpath(objAmazonHomePage.getamazonLoginTest())).isDisplayed(),"Amazon page logged in successfully");
		LOGGER.info("Amazon page logged in successfully");
		
		String searchPage = objAmazonHomePage.getSearchPage();
		// searching data product in amazon
		driver.findElement(By.id((String) prop.get("searchAmazontextbox"))).sendKeys((String) prop.get("product"));
		driver.findElement(By.xpath(searchPage)).click();

		// Product value getting from amazon
		String valueOfProductFromAmazonValue = driver.findElement(By.xpath(objAmazonHomePage.getCostValuefromAmazon()))
				.getText();
		LOGGER.info("Found "+(String) prop.get("product") +" successfully in Amazon");
		//Assertion for Product found
		assertTrue( driver.findElement(By.xpath(objAmazonHomePage.getCostValuefromAmazon())).isDisplayed());
		String[] split = valueOfProductFromAmazonValue.split(",");
		String valueOfProductFromAmazonSplit = split[0] + "" + split[1];
		driver.close();
		LOGGER.info("Amazon page logged out");
		return Integer.parseInt(valueOfProductFromAmazonSplit);
	}

	/**
	 * 
	 * This test case will login in https://www.flipkart.com/
	 * 
	 * search for iPhone XR (64GB) - Yellow
	 * 
	 * Select the matching iPhone once list appears.
	 * 
	 * Return the price of the selected iPhone
	 * 
	 * 
	 */
	public int valueOfProductFromFlipkart() throws IOException {

		setup();
		objFlipkartHomePage = new FlipkartHomePage(driver);

		driver.get(objFlipkartHomePage.homePageUserName());
		assertTrue(driver.findElement(By.xpath(objFlipkartHomePage.getflipkartLoginTest())).isDisplayed(),"Flipkart page loggedin successfully");
		LOGGER.info("Flipkart page logged in successfully");
		if (driver.findElement(By.xpath(objFlipkartHomePage.getLoginPopUp())).isDisplayed()) {
			driver.findElement(By.xpath(objFlipkartHomePage.getLoginPopUp())).click();
		}
		driver.findElement(By.xpath(objFlipkartHomePage.getSearchPage())).sendKeys((String) prop.get("product"));
		driver.findElement(By.xpath(objFlipkartHomePage.getSubmitButton())).click();

		String valueOfProductFromFlipkart = driver
				.findElement(By.xpath(objFlipkartHomePage.getValueOfProductFromFlipkart())).getText();
		
		//Assertion for Product found
		assertTrue(driver
				.findElement(By.xpath(objFlipkartHomePage.getValueOfProductFromFlipkart())).isDisplayed());
		LOGGER.info("Found "+(String) prop.get("product") +" successfully in Flipkart");
		
		String[] split1 = valueOfProductFromFlipkart.split((String) prop.get("rupeevalue"));
		String valueOfProductFromFlipkartSplit = split1[1] + "" + split1[2];
		driver.close();
		LOGGER.info("FlipKart page logged out");
		return Integer.parseInt(valueOfProductFromFlipkartSplit);

	}

	/**
	 * 
	 * This test case will login in https://www.amazon.in/
	 * 
	 * search for iPhone XR (64GB) - Yellow
	 * 
	 * Select the matching iPhone once list appears.
	 * 
	 * Get the price of the selected iPhone
	 * 
	 * Now, go to https://www.flipkart.com/.
	 * 
	 * Repeat steps 2 to 4 and get the price.
	 * 
	 * Compare the price on both the website and determine which website. has
	 * lesser value for the iPhone and print the final result on the console
	 * 
	 */
	@Test(priority = 0)
	public void testHomePageAppearCorrect() throws IOException {

		// login to amazon and get the price value
		int valueOfProductFromAmazon = valueOfProductFromAmazon();

		// login to flipkart and get the price value
		int valueOfProductFromFlipkart = valueOfProductFromFlipkart();

		if (valueOfProductFromAmazon < valueOfProductFromFlipkart) {
			LOGGER.info("********************************************************************************");
			LOGGER.info("As Amazon cost: " + valueOfProductFromAmazon);
			LOGGER.info("As Flipkart cost: " + valueOfProductFromFlipkart);
			LOGGER.info("Prefering to purchase prodcut from amazon:-------> " + valueOfProductFromAmazon);

		} else if (valueOfProductFromAmazon > valueOfProductFromFlipkart) {

			LOGGER.info("Prefering to purchase prodcut from flipkart:------->");
			LOGGER.info("As Amazon cost: " + valueOfProductFromAmazon);
			LOGGER.info("As Flipkart cost: " + valueOfProductFromFlipkart);
			LOGGER.info("Prefering to purchase prodcut from flipkart:-------> " + valueOfProductFromAmazon);

		} else {

			LOGGER.info("Both price in Amazon and flipkart same------->");
			LOGGER.info("Amazon cost: " + valueOfProductFromAmazon);
			LOGGER.info("Flipkart cost: " + valueOfProductFromFlipkart);

		}
		LOGGER.info("********************************************************************************");
	}

	@AfterTest
	public void afterTestMethod() {
		driver.quit();
	}

}

package utility;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import io.github.bonigarcia.wdm.WebDriverManager;

public class CommonUtil {
	public static WebDriver driver;
	public WebDriverWait wait;
	public static String imagePath = System.getProperty("user.dir") + "/Screenshots/";
	public static ExtentTest test;
	public static ExtentSparkReporter reporter;
	public static ExtentReports extent;

	public CommonUtil(WebDriver driver) {
		CommonUtil.driver = driver;

	}

	public boolean clickiIfElmentPresent(WebElement element) {
		try {
			driver.manage().timeouts().implicitlyWait(500, TimeUnit.MILLISECONDS);
			element.isDisplayed();
			element.isEnabled();
			element.click();
			driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			return true;
		} catch (org.openqa.selenium.NoSuchElementException e) {
			return false;
		} catch (org.openqa.selenium.StaleElementReferenceException e) {
			return false;
		}

	}

	public static String takeScreenshot(String filename) {
		try {
			File scrnShot = ((org.openqa.selenium.TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(scrnShot, new File(imagePath + filename + ".png"));
			return imagePath + filename + ".png";
		} catch (WebDriverException e) {
			e.printStackTrace();
			return "";
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}

	public void waitForElement(WebElement element, int timeOutInSeconds) {
		wait = new WebDriverWait(driver, timeOutInSeconds);
		wait.until(ExpectedConditions.visibilityOf(element));
		wait.until(ExpectedConditions.elementToBeClickable(element));
	}

	public boolean elementIsVisible(WebElement element) {
		try {
			wait = new WebDriverWait(driver, 3);
			wait.until(ExpectedConditions.visibilityOf(element));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public void enterText(WebElement e, String text) {
		e.click();
		e.clear();
		e.sendKeys(text);
		e.sendKeys(Keys.TAB);
	}

	public static void cleanDirectory(File dir) {
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			if (files != null && files.length > 0) {
				for (File aFile : files) {
					aFile.delete();
				}
			}
		}
	}

	public static List<HashMap<String, String>> readExcel(String excelpath, String sheetName) {

		HashMap<String, String> dataMap = new HashMap<String, String>();
		List<String> keys = new ArrayList<String>();
		List<HashMap<String, String>> excelMap = new ArrayList<HashMap<String, String>>();
		XLS_Reader xls = new XLS_Reader(excelpath);
		int rowCount = xls.getRowCount(sheetName);
		int colCount = xls.getColCount(sheetName);
		for (int i = 0; i < colCount; i++) {
			keys.add(xls.getCellData(sheetName, i, 0));
		}
		for (int i = 1; i < rowCount; i++) {
			for (int j = 0; j < colCount; j++)
				dataMap.put(keys.get(j), xls.getCellData(sheetName, j, i));
			excelMap.add(new HashMap<String, String>(dataMap));
		}
		return excelMap;
	}

	public static ExtentReports setupExtentReport() {
		reporter = new ExtentSparkReporter(System.getProperty("user.dir") + "/target/Reports");
		reporter.config().setDocumentTitle("OrangeHrEmpTests");
		reporter.config().setReportName("EmpTest");
		extent = new ExtentReports();
		extent.attachReporter(reporter);
		extent.setSystemInfo("Presented To", "Testometer");
		extent.setSystemInfo("Author", "Suyog Sikarwar");
		return extent;
	}

	public static WebDriver browserFactory(String browser) {
		switch (browser) {
		case "chrome":
		case "Chrome":
		case "CHROME":
			WebDriverManager.chromedriver().setup();
			driver = new ChromeDriver();
			break;
		case "firefox":
		case "ff":
		case "Firefox":
		case "FF":
			WebDriverManager.firefoxdriver().setup();
			driver = new FirefoxDriver();
			break;
		case "ie":
		case "Internet Explorer":
		case "IE":
		case "internet explorer":
			WebDriverManager.iedriver().setup();
			driver = new InternetExplorerDriver();
			break;
		case "edge":
		case "Edge":
		case "EDGE":
			WebDriverManager.edgedriver().setup();
			driver = new EdgeDriver();
			break;
		default:
			System.out.println("Invalid browser");
			break;

		}
		return driver;
	}

}

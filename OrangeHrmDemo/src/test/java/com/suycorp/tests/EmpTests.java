package com.suycorp.tests;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.suycorp.pages.PIM_Employee;

import utility.CommonUtil;

public class EmpTests {

	Properties prop = new Properties();
	public static final String Test_Prop = "HrmTest.properties";
	// static String chromDriverpath = System.getProperty("user.dir") +
	// "/src/test/resources/drivers/";
	public WebDriver driver;
	public static String TEST_URL = null;
	private static String USERNAME = null;
	private static String PASSWORD = null;
	List<HashMap<String, String>> dataMap;
	List<HashMap<String, String>> updatedDataMap = new ArrayList<HashMap<String, String>>();
	PIM_Employee pim;
	public static ExtentTest test;
	public static ExtentReports extent;

	@BeforeTest
	@Parameters({ "browser" })
	public void setUp(String browser) {
		try {
			CommonUtil.cleanDirectory(new File(CommonUtil.imagePath));
			InputStream input = ClassLoader.getSystemResourceAsStream(Test_Prop);
			prop.load(input);
			TEST_URL = prop.getProperty("url");
			USERNAME = prop.getProperty("username");
			PASSWORD = new String(Base64.decodeBase64(prop.getProperty("password")));
			dataMap = CommonUtil.readExcel(System.getProperty("user.dir") + "/src/test/resources/Emp_Data.xlsx",
					"Emp Details");
			driver = CommonUtil.browserFactory(browser);
			extent = CommonUtil.setupExtentReport();

		} catch (IOException e) {
			CommonUtil.takeScreenshot("Failed in " + new Exception().getStackTrace()[0].getMethodName());
			Assert.fail("Failed in " + new Exception().getStackTrace()[0].getMethodName());
		}
	}

	@Test(priority = 0)
	public void login() {
		test = extent.createTest("Login to Orange HR");
		try {
			pim = new PIM_Employee(driver);
			test.info("Logging in");
			pim.login(TEST_URL, USERNAME, PASSWORD);
			test.pass("Login Successful");
			extent.flush();
		} catch (AssertionError | Exception e) {
			test.fail("Login Failed :" + e.getMessage());
			test.addScreenCaptureFromPath(CommonUtil.takeScreenshot("Login_Failed"));
			extent.flush();
			Assert.fail("Login Failed");
		}

	}

	@Test(priority = 1, dependsOnMethods = { "login" })
	public void addEmps() {
		System.out.println("addEmp");
		test = extent.createTest("Add employees");
		try {
			for (HashMap<String, String> data : dataMap) {
				if (data.get("Execution").equalsIgnoreCase("yes")) {
					test.info("Adding Employee" + data.get("Fname"));
					data.put("Emp_Id", pim.addEmp(data.get("Fname"), data.get("Lname"), data.get("Photopath")));
					test.info(data.get("Fname") + " added ");
					updatedDataMap.add(new HashMap<String, String>(data));
				}
			}
			test.pass("Employees added successfully");
			extent.flush();
		} catch (AssertionError | Exception e) {

			test.fail("Adding Employees failed : " + e.getMessage());
			test.addScreenCaptureFromPath(
					CommonUtil.takeScreenshot("Failed in " + new Exception().getStackTrace()[0].getMethodName()));
			extent.flush();
			Assert.fail("Failed in " + new Exception().getStackTrace()[0].getMethodName());

		}

	}

	@Test(priority = 2, dependsOnMethods = { "addEmps" })
	public void editAddedEmpAndVerify() {
		test = extent.createTest("Search and Edit employees");
		System.out.println("editAddedEmpAndVerify");
		try {
			for (HashMap<String, String> data : updatedDataMap)
				pim.searchAndEditEmp(data.get("Emp_Id"), data.get("Gender"), data.get("MaritalStatus"),
						data.get("Nationality"), data.get("Dob"), Boolean.parseBoolean(data.get("Smoker")));
			test.pass("Added employees edited successfully");
			extent.flush();
		} catch (AssertionError | Exception e) {
			test.fail("Searching and editing added employees failed : " + e.getMessage());
			test.addScreenCaptureFromPath(
					CommonUtil.takeScreenshot("Failed in " + new Exception().getStackTrace()[0].getMethodName()));
			extent.flush();
			Assert.fail("Failed in " + new Exception().getStackTrace()[0].getMethodName());

		}
	}

	@Test(priority = 3, dependsOnMethods = { "addEmps" })
	public void delAddedEmps() {
		System.out.println("delAddedEmps");
		test = extent.createTest("Delete added employees");
		try {
			for (HashMap<String, String> data : updatedDataMap)
				pim.delEmp(data.get("Emp_Id"), 0);
			test.pass("Added employees deleted successfully");
			extent.flush();
		} catch (AssertionError | Exception e) {
			test.fail("Deleting added employees failed : " + e.getMessage());
			test.addScreenCaptureFromPath(
					CommonUtil.takeScreenshot("Failed in " + new Exception().getStackTrace()[0].getMethodName()));
			extent.flush();
			Assert.fail("Failed in " + new Exception().getStackTrace()[0].getMethodName());

		}
	}

	@Test(priority = 4, dependsOnMethods = { "login" })
	public void delMultiEmp() {
		System.out.println("delMultiEmp");
		test = extent.createTest("Delete multiple employees");
		try {
			pim.delEmp("", 5);
			test.pass("Multiple employees deleted successfully");
			extent.flush();
		} catch (AssertionError | Exception e) {
			test.fail("Deleting added employees failed : " + e.getMessage());
			test.addScreenCaptureFromPath(
					CommonUtil.takeScreenshot("Failed in " + new Exception().getStackTrace()[0].getMethodName()));
			extent.flush();
			Assert.fail("Failed in " + new Exception().getStackTrace()[0].getMethodName());

		}
	}

	@AfterTest
	public void closeSession() {
		extent.flush();
		if (driver != null)
			driver.quit();
	}

}

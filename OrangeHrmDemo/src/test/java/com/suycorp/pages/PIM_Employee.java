package com.suycorp.pages;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import com.suycorp.tests.EmpTests;

import utility.CommonUtil;

public class PIM_Employee extends CommonUtil {
	public WebDriverWait wait;

	public static List<String> empid = new ArrayList<String>();
	Actions action;

	@FindBy(id = "txtUsername")
	WebElement usernameInput;

	@FindBy(id = "txtPassword")
	WebElement passwordInput;

	@FindBy(id = "btnLogin")
	WebElement loginButton;

	@FindBy(id = "menu_pim_viewPimModule")
	WebElement viewpimLink;

	@FindBy(id = "menu_pim_addEmployee")
	WebElement addEmployeeLink;

	@FindBy(css = "#employeeId")
	WebElement empId;

	@FindBy(id = "firstName")
	WebElement fnameTxtBox;

	@FindBy(id = "lastName")
	WebElement lnameTxtBox;

	@FindBy(id = "photofile")
	WebElement choosePhotoButton;

	@FindBy(id = "btnSave")
	WebElement saveEmpButton;

	@FindBy(xpath = "//a[@id='menu_pim_viewEmployeeList']")
	WebElement empListBtn;

	@FindBy(css = "#empsearch_id")
	WebElement empSrchId;

	@FindBy(id = "searchBtn")
	WebElement srchBtn;

	@FindBy(xpath = "//table[@id='resultTable']//td[2]/a")
	WebElement tableIdCol;

	@FindBy(id = "personal_cmbNation")
	WebElement nationalityDrpDwn;

	@FindBy(id = "personal_cmbMarital")
	WebElement maritalStatDrpDwn;

	@FindBy(id = "personal_DOB")
	WebElement dobField;

	@FindBy(id = "personal_chkSmokeFlag")
	WebElement smkChkBox;

	@FindBy(xpath = "//table[@id='resultTable']//td")
	List<WebElement> empListCols;

	@FindBy(xpath = "//table[@id='resultTable']//td[1]/input")
	List<WebElement> empListChkBoxes;

	@FindBy(id = "btnDelete")
	WebElement delEmpBtn;

	@FindBy(id = "dialogDeleteBtn")
	WebElement delConfirmBtn;

	@FindBy(xpath = "//div[contains(@class,'message success fadable')]")
	WebElement successMessage;

	public PIM_Employee(WebDriver driver) {
		super(driver);
		action = new Actions(CommonUtil.driver);
		PageFactory.initElements(CommonUtil.driver, this);
	}

	public void login(String url, String username, String password) {
		driver.get(url);
		driver.manage().window().maximize();
		this.usernameInput.sendKeys(username);
		this.passwordInput.sendKeys(password);
		this.loginButton.click();
		Assert.assertEquals(driver.getCurrentUrl(), "https://opensource-demo.orangehrmlive.com/index.php/dashboard");
		waitForElement(this.viewpimLink, 5);

	}

	public String addEmp(String fname, String lname, String photoPath) {
		action.moveToElement(viewpimLink).build().perform();
		waitForElement(addEmployeeLink, 2);
		addEmployeeLink.click();
		waitForElement(fnameTxtBox, 2);
		Assert.assertEquals(driver.getCurrentUrl(),
				"https://opensource-demo.orangehrmlive.com/index.php/pim/addEmployee");
		enterText(fnameTxtBox, fname);
		enterText(lnameTxtBox, lname);
		String eid = empId.getAttribute("value");
		empid.add(eid);
		choosePhotoButton.sendKeys(photoPath);
		saveEmpButton.click();
		takeScreenshot("AddedEmp_" + eid);
		return eid;
	}

	public void searchEmp() {
		action.moveToElement(viewpimLink).build().perform();
		waitForElement(addEmployeeLink, 2);
		empListBtn.click();

	}

	public void searchEmp(String eid) {
		action.moveToElement(viewpimLink).build().perform();
		waitForElement(addEmployeeLink, 2);
		empListBtn.click();
		enterText(empSrchId, eid);
		srchBtn.click();
	}

	public void clickDelete() {
		delEmpBtn.click();
		waitForElement(delConfirmBtn, 2);
		delConfirmBtn.click();
	}

	public void searchAndEditEmp(List<String> eIds, String gender, String maritalStat, String nationality, String dob,
			boolean smoker) {
		if (eIds.size() == 0)
			Assert.assertTrue(false, "Empty search criteria");
		else {
			searchEmp();
			for (String eid : eIds) {
				enterText(empSrchId, eid);
				srchBtn.click();
				if (empListCols.size() > 1) {
					Assert.assertTrue(true, "Employee found");
					tableIdCol.click();
					waitForElement(saveEmpButton, 2);
					saveEmpButton.click();
					driver.findElement(By.xpath("//label[text()='" + gender.substring(0, 1).toUpperCase()
							+ gender.substring(1) + "']/preceding-sibling::input")).click();
					Select select = new Select(nationalityDrpDwn);
					select.selectByVisibleText(nationality.substring(0, 1).toUpperCase() + nationality.substring(1));
					select = new Select(maritalStatDrpDwn);
					select.selectByVisibleText(maritalStat.substring(0, 1).toUpperCase() + maritalStat.substring(1));
					enterText(dobField, dob);
					if (smoker)
						smkChkBox.click();
					saveEmpButton.click();
					verifyEditedEmpDetailsUpdated(gender, maritalStat, nationality, dob, smoker);
				} else
					Assert.assertTrue(false, "No Record Found");
			}
		}
	}

	public void searchAndEditEmp(String eId, String gender, String maritalStat, String nationality, String dob,
			boolean smoker) {
		searchEmp();
		enterText(empSrchId, eId);
		srchBtn.click();
		if (empListCols.size() > 1) {
			Assert.assertTrue(true, "Employee found");
			EmpTests.test.info("Editing Employee Id : " + eId);
			tableIdCol.click();
			waitForElement(saveEmpButton, 2);
			saveEmpButton.click();
			driver.findElement(By.xpath("//label[text()='" + gender.substring(0, 1).toUpperCase() + gender.substring(1)
					+ "']/preceding-sibling::input")).click();
			Select select = new Select(nationalityDrpDwn);
			select.selectByVisibleText(nationality.substring(0, 1).toUpperCase() + nationality.substring(1));
			select = new Select(maritalStatDrpDwn);
			select.selectByVisibleText(maritalStat.substring(0, 1).toUpperCase() + maritalStat.substring(1));
			enterText(dobField, dob);
			if (smoker)
				smkChkBox.click();
			saveEmpButton.click();
			Assert.assertEquals(elementIsVisible(successMessage), true);
			takeScreenshot("EditedEmp_" + eId);
			verifyEditedEmpDetailsUpdated(gender, maritalStat, nationality, dob, smoker);
			EmpTests.test.info("Edited deatils verified for Employee Id : " + eId);
		} else {
			takeScreenshot("No Record Found for " + eId);
			Assert.assertTrue(false, "No Record Found");
		}

	}

	public void verifyEditedEmpDetailsUpdated(String gender, String maritalStat, String nationality, String dob,
			boolean smoker) {
		if (!driver.findElement(By.xpath("//label[text()='" + gender.substring(0, 1).toUpperCase() + gender.substring(1)
				+ "']/preceding-sibling::input")).isSelected())
			Assert.assertTrue(false, "Gender no updated");
		Select select = new Select(nationalityDrpDwn);
		Assert.assertEquals(nationality.substring(0, 1).toUpperCase() + nationality.substring(1),
				select.getFirstSelectedOption().getText(), "Naitionality no updated");
		select = new Select(maritalStatDrpDwn);
		Assert.assertEquals(maritalStat.substring(0, 1).toUpperCase() + maritalStat.substring(1),
				select.getFirstSelectedOption().getText(), "Martial Status not updated");

		Assert.assertEquals(dob, dobField.getAttribute("value"), "Dob not updated");
		if (smoker && !smkChkBox.isSelected())
			Assert.assertTrue(false, "Smoker profile not updated");
	}

	public void delEmp(List<String> eIds, int multi) {

		searchEmp();
		if (multi == 0) {
			for (String eid : eIds) {
				enterText(empSrchId, eid);
				srchBtn.click();
				if (empListCols.size() > 1) {
					empListChkBoxes.get(0).click();
					clickDelete();
				} else {
					Assert.assertTrue(false, "No record found");
				}
			}
		} else {
			for (int i = 1; i <= multi; i++) {
				driver.findElement(By.xpath("(//table[@id='resultTable']//td[1]/input)[" + i + "]")).click();
			}
			clickDelete();
		}

	}

	public void delEmp(String eId, int multi) {

		searchEmp();
		if (multi == 0) {
			EmpTests.test.info("Deleting Employee Id : " + eId);
			enterText(empSrchId, eId);
			srchBtn.click();
			if (empListCols.size() > 1) {
				empListChkBoxes.get(0).click();

			} else {
				takeScreenshot("No Record Found for " + eId);
				Assert.assertTrue(false, "No record found");
			}

		} else {
			EmpTests.test.info("Deleting multiple employees");
			for (int i = 1; i <= multi; i++) {
				driver.findElement(By.xpath("(//table[@id='resultTable']//td[1]/input)[" + i + "]")).click();
			}

		}
		clickDelete();
		Assert.assertEquals(elementIsVisible(successMessage), true);
		EmpTests.test.info("Employee deleted successfully");

	}

}

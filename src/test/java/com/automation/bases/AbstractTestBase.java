package com.automation.bases;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.*;
import utilities.BrowserUtils;
import utilities.ConfigurationReader;
import utilities.Driver;

import java.io.IOException;

//TestBase class exists in TestNG projects
//TestBase is abstract class,provides minimum essential setup and cleanup for every test of our project
//It is abstract to be extended! by sub classes
//Does not contain Test
//We initialize WebDriverwait,Actions class object and open the browser
//It should not be under utilities for the design point of view!
//testBase is a blueprint for all test classes
 public abstract class AbstractTestBase {
     //protected will be visible in the subclass,(same package or outside the package sub class as well)
protected WebDriverWait wait;
protected Actions actions;
protected ExtentReports report;
protected ExtentHtmlReporter htmlReporter;//deprecated
protected ExtentTest test;

@BeforeTest
@Parameters("reportName")//comes from testng.xml
public void setupTest(@Optional String reportName) {
    System.out.println("Report name: " + reportName);
    reportName = reportName == null ? "report.html" : reportName+".html";

    report = new ExtentReports();

    String reportPath = "";
    //location of report file
    if (System.getProperty("os.name").toLowerCase().contains("win")) {
        reportPath = System.getProperty("user.dir") + "\\test-output\\" + reportName;
    } else {
        reportPath = System.getProperty("user.dir") + "/test-output/" + reportName;
    }
    //is a HTML report itself
    htmlReporter = new ExtentHtmlReporter(reportPath);
    //add it to the reporter
    report.attachReporter(htmlReporter);
    htmlReporter.config().setReportName("VYTRACK Test Automation Results");
}
@AfterTest
public void afterTest(){
    report.flush();//to release a report
}

    @BeforeMethod
   public void setup(){
        String URL= ConfigurationReader.getProperty("qa3");
       Driver.getDriver().get(URL);
       Driver.getDriver().manage().window().maximize();
        wait=new WebDriverWait(Driver.getDriver(),25);
        //implicit wait should be in BeforeMethod
       //We should not use both implicit and explicit waits in same test
       //It might cause unexpected delays
       //Explicit wait is used inside a Test itself,since we have to provide WebElement
       actions=new Actions(Driver.getDriver());
   }

   @AfterMethod
   public void teardown(ITestResult iTestResult) throws IOException {
       //ITestResult class describes the result of a test.
       //if test failed, take a screenshot
       if (iTestResult.getStatus() == ITestResult.FAILURE) {
           //screenshot will have a name of the test
           String screenshotPath = BrowserUtils.getScreenShot(iTestResult.getName());
           test.fail(iTestResult.getName());//attach test name that failed
           BrowserUtils.wait(2);
           test.addScreenCaptureFromPath(screenshotPath, "Failed");//attach screenshot
           test.fail(iTestResult.getThrowable());//attach console output
       }
       BrowserUtils.wait(2);
       Driver.closeDriver();
   }

}
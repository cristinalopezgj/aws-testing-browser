package com.browser.test;

import org.junit.Assert;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.Test;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.devicefarm.DeviceFarmClient;
import software.amazon.awssdk.services.devicefarm.model.CreateTestGridUrlRequest;
import software.amazon.awssdk.services.devicefarm.model.CreateTestGridUrlResponse;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Properties;

public class AwsBrowserTest {

    private static String BROWSER_CHROME = "chrome";
    private static String BROWSER_FIREFOX = "firefox";
    private static String BROWSER_IE = "iexplore";

    private Properties fileprops = new Properties();
    private static RemoteWebDriver driver;
    private static DesiredCapabilities desired_capabilities;

    @Parameters({"browser"})
    @BeforeTest
    public void setUp(String browser) throws Exception {
        URL testGridUrl = null;

        System.setProperty("aws.accessKeyId", getProperties().getProperty("aws-access-key"));
        System.setProperty("aws.secretAccessKey", getProperties().getProperty("aws-secret-access-key"));

        DeviceFarmClient client = DeviceFarmClient.builder().region(Region.US_WEST_2).build();
        CreateTestGridUrlRequest request = CreateTestGridUrlRequest.builder()
                .expiresInSeconds(300)
                .projectArn(getProperties().getProperty("aws-project-arn"))
                .build();

        try {
            CreateTestGridUrlResponse response = client.createTestGridUrl(request);
            testGridUrl = new URL(response.url());
            Assert.assertNotNull(testGridUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        getCapabilities(browser);
        driver = new RemoteWebDriver(testGridUrl, desired_capabilities);
    }

    @Test
    public void searchGoogle() {
        driver.get("https://www.google.com/");
        WebElement inputSearch = driver.findElement(By.xpath("//input[@name='q']"));
        inputSearch.sendKeys("AWS Device Farm");
        inputSearch.sendKeys(Keys.ENTER);
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@id='result-stats']")));
        WebElement results = driver.findElement(By.xpath("//div[@id='result-stats']"));
        Assert.assertTrue(results.isDisplayed());
    }

    @AfterTest
    public void tearDown() {
        driver.quit();
    }

    private Properties getProperties() throws Exception {
        fileprops.load(new FileInputStream(new File("src/test/resources/awsbrowser.properties").getAbsolutePath()));
        return fileprops;
    }

    private void getCapabilities(String browser) {
        System.out.println("****************************************");
        System.out.println("Browser: " + browser);
        System.out.println("****************************************");

        if (browser.equalsIgnoreCase(BROWSER_CHROME)) {
            desired_capabilities = DesiredCapabilities.chrome();
        } else if (browser.equalsIgnoreCase(BROWSER_FIREFOX)) {
            desired_capabilities = DesiredCapabilities.firefox();
        } else {
            desired_capabilities = DesiredCapabilities.internetExplorer();
        }
    }
}

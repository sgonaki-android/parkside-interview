import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.AfterClass;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;


public class WikipediaTestMe {
	private WebDriver driver;
	private String homepage="https://en.wikipedia.org/wiki/Metis_(mythology)";

    @BeforeClass
    public void beforeClass() {
    	//System.setProperty("webdriver.gecko.driver", "C:\\Users\\shant\\Documents\\project\\geckodriver.exe");
    	// driver = new FirefoxDriver();
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\shant\\Documents\\project\\chromedriver.exe");
        driver = new ChromeDriver();
    }
   

    @AfterClass
    public void afterClass() {
        driver.quit();
    }

    @Test
    public void verifyContentsHeadingDisplayed() {
    	driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.get(homepage);

       List<WebElement>  Contents = driver.findElements(By.xpath("//span[@class='toctext']"));
       List<WebElement>  Headings = driver.findElements(By.xpath("//span[@class='mw-headline']"));
        
        Assert.assertEquals(Contents.size(), Headings.size(), "Invalid number of headings displayed!");
        
        List<String> contentsText = new ArrayList<String>();
        for(WebElement e : Contents) {
        	
        	String c=e.getText();
        	contentsText.add(c);
        	
        }
        
        List<String> headingText = new ArrayList<String>();
        for(WebElement ele : Headings) {
        	
        	String c=ele.getText();
        	headingText.add(c);
        	Assert.assertTrue(ele.isDisplayed(),"The heading"+c+"is not displayed");
        	
        }
        Assert.assertTrue(contentsText.containsAll(headingText),"Headings and Contents are mismatching");
    }
    
    @Test
    public void verifyContentsHeadingUrlActive() {
    	driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.get(homepage);
        String url=" ";
        HttpURLConnection connection = null;
        int actualResponseCode = 0;
        int expectedResponseCode=200;
        
       List<WebElement>  Contents = driver.findElements(By.xpath("//li[contains(@class,'toclevel-1')]/a"));
        for(WebElement e : Contents) {
        
        	url=e.getAttribute("href");
        	try {
            	try {
					connection = (HttpURLConnection)(new URL(url).openConnection());
				} catch (MalformedURLException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				connection.setRequestMethod("HEAD");
				try {
					connection.connect();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	        	try {
					actualResponseCode = connection.getResponseCode();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	        	
			} catch (ProtocolException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        	
        	Assert.assertEquals(actualResponseCode, expectedResponseCode,"The following url is broken"+url);
        	
        }
  
    }
    
    @Test
    public void verifyPopUpContents() {
    	driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.get(homepage);
        String expectedTooltip = "In ancient Greek civilization, Nike was a goddess who personified victory. Her Roman equivalent was Victoria.";					
        JavascriptExecutor js = (JavascriptExecutor)driver;
        WebDriverWait wait = new WebDriverWait(driver, 20);
       	WebElement nike= wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@class='div-col']/ul/li/a[@title='Nike (mythology)']")));
       	js.executeScript("arguments[0].scrollIntoView();", nike);

       Actions actions = new Actions(driver);
       Action mouseOverHome = actions.moveToElement(nike).build();
       mouseOverHome.perform(); 
       
       
       WebElement tooltip=driver.findElement(By.xpath("//div[@class='mwe-popups-container']"));
       if(tooltip.isDisplayed()) {
       WebElement toolTipElement = driver.findElement(By.xpath("//a[@class='mwe-popups-extract']/p"));							
       String actualTooltip = toolTipElement.getText();
       Assert.assertEquals(actualTooltip, expectedTooltip);
       }
  
    }
    
    @Test
    public void verifyPageNavigation() {
    	driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.get(homepage);
        JavascriptExecutor js = (JavascriptExecutor)driver;
        WebDriverWait wait = new WebDriverWait(driver, 20);
       	WebElement nike= wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@class='div-col']/ul/li/a[@title='Nike (mythology)']")));
       	js.executeScript("arguments[0].scrollIntoView();", nike);
        String parentHandle = driver.getWindowHandle();

       nike.click();  
       Set<String> handlewindow = driver.getWindowHandles();
       for(String handler:handlewindow) {
    	   if(!handler.equalsIgnoreCase(parentHandle)) {
    		   driver.switchTo().window(handler);
    	   }
       }
       WebElement familyTreeSection=driver.findElement(By.xpath("//span[@id='Family_tree']"));
       Assert.assertTrue(familyTreeSection.isDisplayed(), "No Family Tree Section  is displayed in Nike Page on Wikipedia");
       WebElement familyTreeTable=driver.findElement(By.xpath("//table[@class='toccolours']"));
       Assert.assertTrue(familyTreeTable.isDisplayed(), "No Family Tree Table is displayed in Nike Page on Wikipedia");

    }
}

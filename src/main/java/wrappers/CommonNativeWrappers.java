package wrappers;

import io.appium.java_client.*;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.SupportsContextSwitching;
import io.appium.java_client.remote.SupportsRotation;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.PointerInput.Kind;
import org.openqa.selenium.interactions.PointerInput.MouseButton;
import org.openqa.selenium.interactions.PointerInput.Origin;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

public class CommonNativeWrappers extends AbstractTestNGCucumberTests {
    public static final int MAX_SCROLL = 10;
    //    public static AppiumDriver driver;
    static ThreadLocal<AppiumDriver> driver = new ThreadLocal<>();
    public boolean useExistingApp = true;

    public static AppiumDriver getDriver() {
        return driver.get();
    }

    // To launch the application (Native/Hybrid)
    public boolean launchApp(String platformName, String deviceName, String udid, String appPackage, String appActivity,
                             String automationName, String chromeDriverPort, String systemPort, String xcodeOrgId, String xcodeSigningId,
                             String bundleId, String app, String mjpegServerPort, String wdaLocalPort) {
        try {
            DesiredCapabilities dc = new DesiredCapabilities();
            // To pass the Unique Device IDentifier
            if (!udid.equals(""))
                dc.setCapability("udid", udid);
            // To pass the absolute path of the application
            if (!app.equals(""))
                dc.setCapability("app", System.getProperty("user.dir") + app);
            // To set the device name
            if (!deviceName.equals(""))
                dc.setCapability("deviceName", deviceName);
            // Android
            if (!appPackage.equals(""))
                dc.setCapability("appPackage", appPackage);
            if (!appActivity.equals(""))
                dc.setCapability("appActivity", appActivity);
            // For hybrid app parallel testing
            if (!chromeDriverPort.equals(""))
                dc.setCapability("chromedriverPort", chromeDriverPort);
            // For native app parallel testing
            if (!mjpegServerPort.equals(""))
                dc.setCapability("mjpegServerPort", mjpegServerPort);
            // For hybrid/native app parallel testing
            if (!systemPort.equals(""))
                dc.setCapability("systemPort", systemPort);
            // iOS
            // For hybrid/native app parallel testing
            if (!wdaLocalPort.equals(""))
                dc.setCapability("wdaLocalPort", wdaLocalPort);
            // To pass the Xcode Org ID if the application and WDA are built with different developer certificates
            if (!xcodeOrgId.equals(""))
                dc.setCapability("xcodeOrgId", xcodeOrgId);
            // To pass the Xcode Signing ID if the application and WDA are built with different developer certificates
            if (!xcodeSigningId.equals(""))
                dc.setCapability("xcodeSigningId", xcodeSigningId);
            if (!bundleId.equals(""))
                dc.setCapability("bundleId", bundleId);
            // Mandatory desired capabilities
            // To pass the VDM
            dc.setCapability("automationName", automationName);
            // Comment the below line based on need
            if (useExistingApp) {
                // Below script helps to not clear the cache and data of the app
                dc.setCapability("noReset", true);
                // Below script helps to re-launch the app everytime the session starts
                dc.setCapability("forceAppLaunch", true);
                // Below script helps to close the app when driver.quit() is called
                dc.setCapability("shouldTerminateApp", true);
            }
            if (platformName.equalsIgnoreCase("Android")) {
                // Comment the below line based on need
                dc.setCapability("autoGrantPermissions", true);
                driver.set(new AndroidDriver(new URI("http://0.0.0.0:4723").toURL(), dc));
            } else if (platformName.equalsIgnoreCase("iOS")) {
                // Comment the below line based on need
                dc.setCapability("autoAcceptAlerts", true);
                driver.set(new IOSDriver(new URI("http://0.0.0.0:4723").toURL(), dc));
            }
            getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    // Install the application if the application is not installed
    public boolean verifyAndInstallApp(String bundleIdOrAppPackage, String appPath) {
        boolean bInstallSuccess = false;
        try {
            if (((InteractsWithApps) getDriver()).isAppInstalled(bundleIdOrAppPackage)) {
                ((InteractsWithApps) getDriver()).removeApp(bundleIdOrAppPackage);
            }
            ((InteractsWithApps) getDriver()).installApp(System.getProperty("user.dir") + appPath);
            bInstallSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bInstallSuccess;
    }

    // Install the application
    public boolean installApp(String appPath) {
        boolean bInstallSuccess = false;
        try {
            ((InteractsWithApps) getDriver()).installApp(System.getProperty("user.dir") + appPath);
            bInstallSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bInstallSuccess;
    }

    // Remove the application
    public boolean removeApp(String bundleIdOrAppPackage) {
        boolean bInstallSuccess = false;
        try {
            ((InteractsWithApps) getDriver()).removeApp(bundleIdOrAppPackage);
            bInstallSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bInstallSuccess;
    }

    // Not recommended for use (Sleep). Should not be used for app testing
    public void sleep(int mSec) {
        try {
            Thread.sleep(mSec);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // To print the context available in application
    public void printContext() {
        try {
            Set<String> contexts = ((SupportsContextSwitching) getDriver()).getContextHandles();
            for (String context : contexts) {
                System.out.println(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // To switch the context available in application
    public void switchContext(String context) {
        try {
            ((SupportsContextSwitching) getDriver()).context(context);
            getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // To switch the context as Native
    public void switchNativeView() {
        try {
            Set<String> contextNames = ((SupportsContextSwitching) getDriver()).getContextHandles();
            for (String contextName : contextNames) {
                if (contextName.contains("NATIVE_APP"))
                    ((SupportsContextSwitching) getDriver()).context(contextName);
            }
            getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // To get the WebElements
    public WebElement getWebElement(String locator, String locValue) {
        try {
            switch (locator) {
                case "id":
//                    return getDriver().findElement(AppiumBy.id(locValue));
                    return getDriver().findElement(AppiumBy.xpath("//*[@resource-id='" + locValue + "' or @id='" + locValue + "']"));
                case "name":
//                    return getDriver().findElement(AppiumBy.name(locValue));
                    return getDriver().findElement(AppiumBy.xpath("//*[@name='" + locValue + "']"));
                case "className":
                    return getDriver().findElement(AppiumBy.className(locValue));
                case "link":
                    return getDriver().findElement(AppiumBy.linkText(locValue));
                case "partialLink":
                    return getDriver().findElement(AppiumBy.partialLinkText(locValue));
                case "tag":
                    return getDriver().findElement(AppiumBy.tagName(locValue));
                case "css":
                    return getDriver().findElement(AppiumBy.cssSelector(locValue));
                case "xpath":
                    return getDriver().findElement(AppiumBy.xpath(locValue));
                case "accessibilityId":
                    return getDriver().findElement(AppiumBy.accessibilityId(locValue));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // To take screenshots in application
    public long takeScreenShot() {
        long number = (long) Math.floor(Math.random() * 900000000L) + 10000000L;
        try {
            File srcFiler = getDriver().getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(srcFiler, new File("./reports/images/" + number + ".png"));
        } catch (WebDriverException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("The snapshot could not be taken");
        }
        return number;
    }

    // To verify element is displayed
    public boolean eleIsDisplayed(WebElement ele) {
        try {
            return ele.isDisplayed();
        } catch (Exception e) {
            return false;
        }

    }

    // To verify text in element
    public boolean verifyText(WebElement ele, String expectedText) {
        try {
            return ele.getText().equals(expectedText);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // To scroll in application
    public boolean scrollWithGivenCoOrdinates(int startX, int startY, int endX, int endY) {
        try {
            PointerInput finger = new PointerInput(Kind.TOUCH, "finger");
            Sequence sequence = new Sequence(finger, 1);
            sequence.addAction(finger.createPointerMove(Duration.ZERO, Origin.viewport(), startX, startY));
            sequence.addAction(finger.createPointerDown(MouseButton.LEFT.asArg()));
            sequence.addAction(finger.createPointerMove(Duration.ofSeconds(2), Origin.viewport(), endX, endY));
            sequence.addAction(finger.createPointerUp(MouseButton.LEFT.asArg()));
            getDriver().perform(Collections.singletonList(sequence));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // To double tap in application
    public void doubleTap(int x, int y) {
        PointerInput finger = new PointerInput(Kind.TOUCH, "finger");
        Sequence doubleTap = new Sequence(finger, 1);
        doubleTap.addAction(finger.createPointerMove(Duration.ZERO, Origin.viewport(), x, y));
        doubleTap.addAction(finger.createPointerDown(MouseButton.LEFT.asArg()));
        doubleTap.addAction(new Pause(finger, Duration.ofMillis(100)));
        doubleTap.addAction(finger.createPointerUp(MouseButton.LEFT.asArg()));
        doubleTap.addAction(new Pause(finger, Duration.ofMillis(100)));
        doubleTap.addAction(finger.createPointerDown(MouseButton.LEFT.asArg()));
        doubleTap.addAction(new Pause(finger, Duration.ofMillis(100)));
        doubleTap.addAction(finger.createPointerUp(MouseButton.LEFT.asArg()));
        getDriver().perform(Collections.singletonList(doubleTap));
    }

    // To long press in application
    public void longPress(int x, int y) {
        PointerInput finger = new PointerInput(Kind.TOUCH, "finger");
        Sequence longPress = new Sequence(finger, 1);
        longPress.addAction(finger.createPointerMove(Duration.ZERO, Origin.viewport(), x, y));
        longPress.addAction(finger.createPointerDown(MouseButton.LEFT.asArg()));
        longPress.addAction(new Pause(finger, Duration.ofMillis(2000)));
        longPress.addAction(finger.createPointerUp(MouseButton.LEFT.asArg()));
        getDriver().perform(Collections.singletonList(longPress));
    }

    // To pinch in application
    public void pinchInApp() {
        getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
        int maxY = getDriver().manage().window().getSize().getHeight();
        int maxX = getDriver().manage().window().getSize().getWidth();
        PointerInput finger1 = new PointerInput(Kind.TOUCH, "finger1");
        Sequence a = new Sequence(finger1, 1);
        a.addAction(finger1.createPointerMove(Duration.ofSeconds(0), Origin.viewport(), (int) (maxX * 0.75),
                (int) (maxY * 0.25)));
        a.addAction(finger1.createPointerDown(MouseButton.LEFT.asArg()));
        a.addAction(finger1.createPointerMove(Duration.ofSeconds(1), Origin.viewport(), (int) (maxX * 0.5),
                (int) (maxY * 0.5)));
        a.addAction(finger1.createPointerUp(MouseButton.LEFT.asArg()));
        PointerInput finger2 = new PointerInput(Kind.TOUCH, "finger2");
        Sequence b = new Sequence(finger2, 1);
        b.addAction(finger2.createPointerMove(Duration.ofSeconds(0), Origin.viewport(), (int) (maxX * 0.25),
                (int) (maxY * 0.75)));
        b.addAction(finger2.createPointerDown(MouseButton.LEFT.asArg()));
        b.addAction(finger2.createPointerMove(Duration.ofSeconds(1), Origin.viewport(), (int) (maxX * 0.5),
                (int) (maxY * 0.5)));
        b.addAction(finger2.createPointerUp(MouseButton.LEFT.asArg()));
        getDriver().perform(Arrays.asList(a, b));
    }

    // To zoom in application
    public void zoomInApp() {
        int maxY = getDriver().manage().window().getSize().getHeight();
        int maxX = getDriver().manage().window().getSize().getWidth();
        PointerInput finger1 = new PointerInput(Kind.TOUCH, "lokesh-finger1");
        Sequence a = new Sequence(finger1, 1);
        a.addAction(finger1.createPointerMove(Duration.ofSeconds(0), Origin.viewport(), (int) (maxX * 0.5),
                (int) (maxY * 0.5)));
        a.addAction(finger1.createPointerDown(MouseButton.LEFT.asArg()));
        a.addAction(finger1.createPointerMove(Duration.ofSeconds(1), Origin.viewport(), (int) (maxX * 0.75),
                (int) (maxY * 0.25)));
        a.addAction(finger1.createPointerUp(MouseButton.LEFT.asArg()));
        PointerInput finger2 = new PointerInput(Kind.TOUCH, "lokesh-finger2");
        Sequence b = new Sequence(finger2, 1);
        b.addAction(finger2.createPointerMove(Duration.ofSeconds(0), Origin.viewport(), (int) (maxX * 0.5),
                (int) (maxY * 0.5)));
        b.addAction(finger2.createPointerDown(MouseButton.LEFT.asArg()));
        b.addAction(finger2.createPointerMove(Duration.ofSeconds(1), Origin.viewport(), (int) (maxX * 0.25),
                (int) (maxY * 0.75)));
        b.addAction(finger2.createPointerUp(MouseButton.LEFT.asArg()));
        getDriver().perform(Arrays.asList(a, b));
    }

    // To scroll up in application
    public void swipe(String direction) {
        switch (direction.toLowerCase()) {
            case "up":
                swipeUpInApp();
                break;
            case "down":
                swipeDownInApp();
                break;
            case "left":
                swipeLeftInApp();
                break;
            case "right":
                swipeRightInApp();
                break;
            default:
                throw new RuntimeException("Invalid direction. So could not perform swipe");
        }
    }

    private boolean swipeUpInApp() {
        Dimension size = getDriver().manage().window().getSize();
        int startX = (int) (size.getWidth() * 0.5);
        int startY = (int) (size.getHeight() * 0.8);
        int endX = (int) (size.getWidth() * 0.5);
        int endY = (int) (size.getHeight() * 0.2);
        return scrollWithGivenCoOrdinates(startX, startY, endX, endY);
    }

    // To scroll down in application
    private boolean swipeDownInApp() {
        Dimension size = getDriver().manage().window().getSize();
        int startX = (int) (size.getWidth() * 0.5);
        int startY = (int) (size.getHeight() * 0.2);
        int endX = (int) (size.getWidth() * 0.5);
        int endY = (int) (size.getHeight() * 0.8);
        return scrollWithGivenCoOrdinates(startX, startY, endX, endY);
    }

    // To scroll left in application
    private boolean swipeLeftInApp() {
        Dimension size = getDriver().manage().window().getSize();
        int startX = (int) (size.getWidth() * 0.8);
        int startY = (int) (size.getHeight() * 0.5);
        int endX = (int) (size.getWidth() * 0.2);
        int endY = (int) (size.getHeight() * 0.5);
        return scrollWithGivenCoOrdinates(startX, startY, endX, endY);
    }

    // To scroll right in application
    private boolean swipeRightInApp() {
        Dimension size = getDriver().manage().window().getSize();
        int startX = (int) (size.getWidth() * 0.2);
        int startY = (int) (size.getHeight() * 0.5);
        int endX = (int) (size.getWidth() * 0.8);
        int endY = (int) (size.getHeight() * 0.5);
        return scrollWithGivenCoOrdinates(startX, startY, endX, endY);
    }

    // To scroll down within web element in application
    public void swipeWithinWebElement(String direction, WebElement ele) {
        switch (direction.toLowerCase()) {
            case "up":
                swipeUpInAppWithWebElement(ele);
                break;
            case "down":
                swipeDownInAppWithWebElement(ele);
                break;
            case "left":
                swipeLeftInAppWithWebElement(ele);
                break;
            case "right":
                swipeRightInAppWithWebElement(ele);
                break;
            default:
                throw new RuntimeException("Invalid direction. So could not perform swipe");
        }
    }

    private boolean swipeDownInAppWithWebElement(WebElement ele) {
        Rectangle rect = ele.getRect();
        int startX = (int) (((rect.getWidth()) * 0.5) + rect.getX());
        int startY = (int) (((rect.getHeight()) * 0.2) + rect.getY());
        int endX = (int) (((rect.getWidth()) * 0.5) + rect.getX());
        int endY = (int) (((rect.getHeight()) * 0.8) + rect.getY());
        return scrollWithGivenCoOrdinates(startX, startY, endX, endY);
    }

    // To scroll up within web element in application
    private boolean swipeUpInAppWithWebElement(WebElement ele) {
        Rectangle rect = ele.getRect();
        int startX = (int) (((rect.getWidth()) * 0.5) + rect.getX());
        int startY = (int) (((rect.getHeight()) * 0.8) + rect.getY());
        int endX = (int) (((rect.getWidth()) * 0.5) + rect.getX());
        int endY = (int) (((rect.getHeight()) * 0.2) + rect.getY());
        return scrollWithGivenCoOrdinates(startX, startY, endX, endY);
    }

    // To scroll right within web element in application
    private boolean swipeRightInAppWithWebElement(WebElement ele) {
        Rectangle rect = ele.getRect();
        int startX = (int) (((rect.getWidth()) * 0.2) + rect.getX());
        int startY = (int) (((rect.getHeight()) * 0.5) + rect.getY());
        int endX = (int) (((rect.getWidth()) * 0.8) + rect.getX());
        int endY = (int) (((rect.getHeight()) * 0.5) + rect.getY());
        return scrollWithGivenCoOrdinates(startX, startY, endX, endY);
    }

    // To scroll left within web element in application
    private boolean swipeLeftInAppWithWebElement(WebElement ele) {
        Rectangle rect = ele.getRect();
        int startX = (int) (((rect.getWidth()) * 0.8) + rect.getX());
        int startY = (int) (((rect.getHeight()) * 0.5) + rect.getY());
        int endX = (int) (((rect.getWidth()) * 0.2) + rect.getX());
        int endY = (int) (((rect.getHeight()) * 0.5) + rect.getY());
        return scrollWithGivenCoOrdinates(startX, startY, endX, endY);
    }

    // To scroll up until web element is displayed in application
    public boolean swipeUpInAppUntilElementIsVisible(String locator, String locValue) {
        int i = 1;
        while (!eleIsDisplayed(getWebElement(locator, locValue))) {
            swipeUpInApp();
            i++;
            if (i == MAX_SCROLL)
                break;
        }
        return true;
    }

    // To scroll down until web element is displayed in application
    public boolean swipeDownInAppUntilElementIsVisible(String locator, String locValue) {
        int i = 1;
        while (!eleIsDisplayed(getWebElement(locator, locValue))) {
            swipeDownInApp();
            i++;
            if (i == MAX_SCROLL)
                break;
        }
        return true;
    }

    // To scroll left until web element is displayed in application
    public boolean swipeLeftInAppUntilElementIsVisible(String locator, String locValue) {
        int i = 1;
        while (!eleIsDisplayed(getWebElement(locator, locValue))) {
            swipeLeftInApp();
            i++;
            if (i == MAX_SCROLL)
                break;
        }
        return true;
    }

    // To scroll right until web element is displayed in application
    public boolean swipeRightInAppUntilElementIsVisible(String locator, String locValue) {
        int i = 1;
        while (!eleIsDisplayed(getWebElement(locator, locValue))) {
            swipeRightInApp();
            i++;
            if (i == MAX_SCROLL)
                break;
        }
        return true;
    }

    // To pull a file from the device
    public boolean pullFileFromDevice(String phonePath, String destinationPath) {
        byte[] srcData = ((PullsFiles) getDriver()).pullFile(phonePath);
        Path destData = Paths.get(System.getProperty("user.dir") + destinationPath);
        try {
            Files.write(destData, srcData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    // To close all the application opened in this session
    public void closeApp() {
        if (getDriver() != null) {
            try {
                getDriver().quit();
            } catch (Exception ignored) {
            }
        }
    }

    // To set portrait orientation
    public boolean setPortraitOrientation() {
        ((SupportsRotation) getDriver()).rotate(ScreenOrientation.PORTRAIT);
        return true;
    }

    // To set landscape orientation
    public boolean setLandscapeOrientation() {
        ((SupportsRotation) getDriver()).rotate(ScreenOrientation.LANDSCAPE);
        return true;
    }

    // To hide the keyboard if it is visible
    //Note: Will not work for numerical keyboard in iOS devices
    public void hideKeyboard() {
        if (isKeyboardShown()) {
            try {
                ((HidesKeyboard) getDriver()).hideKeyboard();
            } catch (Exception e) {
                if (getDriver().getCapabilities().getPlatformName().toString().equalsIgnoreCase("iOS")) {
                    String context = ((SupportsContextSwitching) getDriver()).getContext();
                    assert context != null;
                    boolean isNative = context.equalsIgnoreCase("NATIVE_APP");
                    if (!isNative) {
                        switchNativeView();
                    }
                    if (isKeyboardShown()) {
                        click(getWebElement(Locators.ACCESSIBILITY_ID.toString(), "Done"));
                    }
                    if (!isNative) {
                        switchContext(context);
                    }
                }
            }
        }
    }

    public boolean isKeyboardShown() {
        return ((HasOnScreenKeyboard) getDriver()).isKeyboardShown();
    }

    // To get orientation set in the application
    public String getOrientation() {
        return ((SupportsRotation) getDriver()).getOrientation().toString();
    }

    // To enter data in web element
    public boolean enterValue(WebElement ele, String data) {
        return enterValue(ele, data, true);
    }

    public boolean enterValue(WebElement ele, String data, boolean hideKeyboard) {
        ele.clear();
        ele.sendKeys(data);
        if (hideKeyboard) {
            hideKeyboard();
        }
        return true;
    }

    // To click in web element
    public void click(WebElement ele) {
        try {
            ele.click();
        } catch (Exception ignored) {
        }
    }

    // To get text in web element
    public String getText(WebElement ele) {
        return ele.getText();
    }

    // To switch to another application installed in device
    public void switchToAnotherApp(String bundleIdOrAppPackage) {
        ((InteractsWithApps) getDriver()).activateApp(bundleIdOrAppPackage);
    }

    // To close the application installed in device
    public void stopRunningApp(String bundleIdOrAppPackage) {
        ((InteractsWithApps) getDriver()).terminateApp(bundleIdOrAppPackage);
    }

    // Locators ENUM
    public enum Locators {
        ID("id"),
        NAME("name"),
        CLASS_NAME("className"),
        LINK_TEXT("link"),
        PARTIAL_LINK_TEXT("partialLink"),
        TAG_NAME("tag"),
        CSS("css"),
        XPATH("xpath"),
        ACCESSIBILITY_ID("accessibilityId");

        private final String value;

        Locators(String value) {
            this.value = value;
        }

        public String asString() {
            return this.value;
        }
    }
}
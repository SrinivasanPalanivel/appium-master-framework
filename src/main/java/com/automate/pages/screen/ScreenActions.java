package com.automate.pages.screen;

import com.automate.driver.manager.DriverManager;
import com.automate.enums.MobileFindBy;
import com.automate.enums.WaitStrategy;
import com.automate.reports.ExtentReportLogger;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Ordering;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.android.PowerACState;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Keys;
import org.openqa.selenium.ScreenOrientation;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;

import java.time.Duration;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.automate.enums.MobileFindBy.ACCESSIBILITY_ID;
import static com.automate.enums.MobileFindBy.CLASS;
import static com.automate.enums.MobileFindBy.CSS;
import static com.automate.enums.MobileFindBy.ID;
import static com.automate.enums.MobileFindBy.NAME;
import static com.automate.enums.MobileFindBy.XPATH;
import static com.automate.factories.WaitFactory.explicitlyWaitForElement;

public class ScreenActions {

  private final Map<MobileFindBy, Function<String, WebElement>> mobileFindByFunctionMap = new EnumMap<>(MobileFindBy.class);
  private final Function<String, WebElement> findByXpath =
    mobileElement -> DriverManager.getDriver().findElement(By.xpath(mobileElement));
  private final Function<String, WebElement> findByCss =
    mobileElement -> DriverManager.getDriver().findElement(By.cssSelector(mobileElement));
  private final Function<String, WebElement> findById = mobileElement -> DriverManager.getDriver().findElement(By.id(mobileElement));
  private final Function<String, WebElement> findByName =
    mobileElement -> DriverManager.getDriver().findElement(By.name(mobileElement));
  private final Function<String, WebElement> findByAccessibilityId =
    mobileElement -> DriverManager.getDriver().findElement(AppiumBy.accessibilityId(mobileElement));
  private final Function<String, WebElement> findByClassName =
    mobileElement -> DriverManager.getDriver().findElement(By.className(mobileElement));
  protected ScreenActions() {
    PageFactory.initElements(new AppiumFieldDecorator(DriverManager.getDriver()), this);
  }

  private WebElement getMobileElement(String mobileElement, MobileFindBy mobileFindBy) {
    if (mobileFindByFunctionMap.isEmpty()) {
      mobileFindByFunctionMap.put(XPATH, findByXpath);
      mobileFindByFunctionMap.put(CSS, findByCss);
      mobileFindByFunctionMap.put(ID, findById);
      mobileFindByFunctionMap.put(NAME, findByName);
      mobileFindByFunctionMap.put(ACCESSIBILITY_ID, findByAccessibilityId);
      mobileFindByFunctionMap.put(CLASS, findByClassName);
    }
    return mobileFindByFunctionMap.get(mobileFindBy).apply(mobileElement);
  }

  protected WebElement getDynamicMobileElement(String mobileElement, MobileFindBy mobileFindBy) {
    if (mobileFindBy == XPATH) {
      return DriverManager.getDriver().findElement(By.xpath(mobileElement));
    } else if (mobileFindBy == MobileFindBy.CSS) {
      return DriverManager.getDriver().findElement(By.cssSelector(mobileElement));
    }
    return null;
  }

  protected void waitForPageLoad(int waitTime) {
    DriverManager.getDriver().manage().timeouts().pageLoadTimeout(Duration.ofSeconds(waitTime));
  }

  protected String getTextFromAttribute(WaitStrategy waitStrategy, WebElement element) {
    return explicitlyWaitForElement(waitStrategy, element).getAttribute("text");
  }

  protected String getText(WebElement element, WaitStrategy waitStrategy) {
    return explicitlyWaitForElement(waitStrategy, element).getText();
  }

  protected boolean isElementDisplayed(WebElement element) {
    return element.isDisplayed();
  }

  protected void doClear(WebElement element) {
    element.clear();
  }

  protected void getServerStatus() {
    DriverManager.getDriver().getStatus();
  }

  protected void setOrientation(ScreenOrientation screenOrientationType) {
    Object driver = DriverManager.getDriver();
    if (driver instanceof AndroidDriver) {
      ((AndroidDriver) driver).rotate(screenOrientationType);
      ExtentReportLogger.logInfo("Device Orientation is set to " + screenOrientationType);
    } else if (driver instanceof IOSDriver) {
      ((IOSDriver) driver).rotate(screenOrientationType);
      ExtentReportLogger.logInfo("Device Orientation is set to " + screenOrientationType);
    } else {
      throw new IllegalStateException("Unexpected driver type for setOrientation: " + driver);
    }
  }

  protected void backgroundApp() {
    // runAppInBackground is available on AndroidDriver/iOSDriver; cast to AppiumDriver subclass
    if (DriverManager.getDriver() instanceof AndroidDriver) {
      ((AndroidDriver) DriverManager.getDriver()).runAppInBackground(Duration.ofSeconds(10));
    } else if (DriverManager.getDriver() instanceof IOSDriver) {
      ((IOSDriver) DriverManager.getDriver()).runAppInBackground(Duration.ofSeconds(10));
    } else {
      DriverManager.getDriver().executeScript("mobile: backgroundApp", ImmutableMap.of("seconds", 10));
    }
  }

  protected String getElementAttribute(WebElement element, String attributeName) {
    return element.getAttribute(attributeName);
  }

  protected boolean isElementSelected(WebElement element) {
    return element.isSelected();
  }

  protected boolean isElementEnabled(WebElement element) {
    return element.isEnabled();
  }

  protected WebElement getActiveElement() {
    return DriverManager.getDriver().switchTo().activeElement();
  }

  protected void moveMouseToElement(WebElement element, int xoffset, int yoffset) {
    new Actions(DriverManager.getDriver())
      .moveToElement(element, xoffset, yoffset)
      .perform();
    ExtentReportLogger.logInfo("Move to target element :" + element);
  }

  protected void doubleClickOnElement(WebElement element) {
    new Actions(DriverManager.getDriver())
      .moveToElement(element)
      .doubleClick()
      .perform();
    ExtentReportLogger.logInfo("Double click on element : " + element);
  }

  protected void performSingleTap(WebElement element) {
    try {
      tapElement(element);
      ExtentReportLogger.logInfo("Single tap on element : " + element);
    } catch (Exception e) {
      ExtentReportLogger.logFail("Exception in performSingleTap", e);
    }
  }

  protected void performDoubleTap(WebElement element) {
    try {
      tapElement(element);
      try { Thread.sleep(100); } catch (InterruptedException ignored) {}
      tapElement(element);
      ExtentReportLogger.logInfo("Double tap on element : " + element);
    } catch (Exception e) {
      ExtentReportLogger.logFail("Exception in performDoubleTap", e);
    }
  }

  protected void performLongTap(WebElement element) {
    try {
      longPressElement(element, 2000);
      ExtentReportLogger.logInfo("Long press on element : " + element);
    } catch (Exception e) {
      ExtentReportLogger.logFail("Exception in performLongTap", e);
    }
  }

  protected void touchScreenScroll(WebElement element, int x, int y) {
    try {
      // start from element center
      org.openqa.selenium.Rectangle rect = element.getRect();
      int startX = rect.getX() + rect.getWidth() / 2;
      int startY = rect.getY() + rect.getHeight() / 2;
      swipeCoordinates(startX, startY, x, y, 800);
    } catch (Exception e) {
      ExtentReportLogger.logFail("Exception in touchScreenScroll", e);
    }
  }

  protected void hideKeyboard() {
    Object driver = DriverManager.getDriver();
    try {
      if (driver instanceof AndroidDriver) {
        ((AndroidDriver) driver).hideKeyboard();
      } else if (driver instanceof IOSDriver) {
        ((IOSDriver) driver).hideKeyboard();
      } else {
        DriverManager.getDriver().executeScript("mobile:hideKeyboard");
      }
    } catch (Exception ignored) {
    }
  }

  protected void scrollClickAndroid(String scrollableListId, String selectionText) {
    ((AndroidDriver) DriverManager.getDriver()).findElement(AppiumBy.androidUIAutomator(
      "new UiScrollable(new UiSelector().scrollable(true)."
        + "resourceId(\"" + scrollableListId + "\"))"
        + ".setAsHorizontalList().scrollIntoView(new UiSelector().text(\"" + selectionText + "\"))")).click();
  }

  protected void click(WebElement element, String elementName) {
    try {
      element.click();
      ExtentReportLogger.logInfo("Clicked on " + elementName);
    } catch (Exception e) {
      ExtentReportLogger.logFail("Exception occurred when clicking on - " + elementName, e);
    }
  }

  public void click(String element, MobileFindBy elementType, String elementName) {
    click(getMobileElement(element, elementType), elementName);
  }

  protected void enter(WebElement element, String value, String elementName) {
    try {
      explicitlyWaitForElement(WaitStrategy.VISIBLE, element);
      doClear(element);
      element.sendKeys(value);
      ExtentReportLogger.logInfo("Entered value - <b>" + value + "</b> in the field " + elementName);
    } catch (Exception e) {
      ExtentReportLogger.logFail("Exception occurred while entering value in the field - " + elementName, e);
    }
  }

  protected void enterValueAndPressEnter(WebElement element, String value, String elementName) {
    try {
      doClear(element);
      element.sendKeys(value, Keys.ENTER);
      ExtentReportLogger.logInfo("Entered value - <b>" + value + "</b> in the field " + elementName + " and pressed enter");
    } catch (Exception e) {
      ExtentReportLogger.logFail("Exception caught while entering value", e);
    }
  }

  protected void enter(String element, MobileFindBy elementType, String value, String elementName) {
    enter(getMobileElement(element, elementType), value, elementName);
  }

  public boolean isTextPresent(String containsText) {
    return DriverManager.getDriver().getPageSource().contains(containsText);
  }

  public void powerStateAndroid(String powerState) {
    switch (powerState) {
      case "ON":
        if (DriverManager.getDriver() instanceof AndroidDriver) {
          ((AndroidDriver) DriverManager.getDriver()).setPowerAC(PowerACState.ON);
        }
        break;
      case "OFF":
        if (DriverManager.getDriver() instanceof AndroidDriver) {
          ((AndroidDriver) DriverManager.getDriver()).setPowerAC(PowerACState.OFF);
        }
        break;
      default:
        ExtentReportLogger.warning("Power state not available");
        break;
    }
  }

  /**
   * Swipe Down
   */
  public void swipeDown() {
    DriverManager.getDriver().executeScript("mobile:scroll",
                                            ImmutableMap.of("direction", "down"));
    ExtentReportLogger.logInfo("Swipe Down");
  }

  /**
   * Swipe Up
   */
  public void swipeUP() {
    DriverManager.getDriver().executeScript("mobile:scroll", ImmutableMap.of("direction", "up"));
    ExtentReportLogger.logInfo("Swipe Up");
  }

  /**
   * Accept Alert
   */
  public void acceptAlert() {
    DriverManager.getDriver().executeScript("mobile:acceptAlert");
    ExtentReportLogger.logInfo("Accept Alert");
  }

  /**
   * Dismiss Alert
   */
  public void dismissAlert() {
    DriverManager.getDriver().executeScript("mobile:dismissAlert");
    ExtentReportLogger.logInfo("Dismiss Alert");
  }

  /**
   * Long press key
   *
   * @param element element
   */
  public void longPress(WebElement element) {
    try {
      longPressElement(element, 1000);
    } catch (Exception e) {
      ExtentReportLogger.logFail("Exception caught while performing long press on the Mobile Element", e);
    }
  }

  /**
   * Scroll to specific location
   */
  public void scrollToLocation() {
    try {
      HashMap<String, Double> scrollElement = new HashMap<>();
      scrollElement.put("startX", 0.50);
      scrollElement.put("startY", 0.95);
      scrollElement.put("endX", 0.50);
      scrollElement.put("endY", 0.01);
      scrollElement.put("duration", 3.0);
      DriverManager.getDriver().executeScript("mobile: swipe", scrollElement);
    } catch (Exception e) {
      ExtentReportLogger.logFail("Exception caught when scrolling to specific location", e);
    }
  }

  public boolean checkListIsSorted(List<String> listToSort) {
    if (!listToSort.isEmpty()) {
      try {
        if (Ordering.natural().isOrdered(listToSort)) {
          ExtentReportLogger.logPass("List is sorted");
          return true;
        } else {
          ExtentReportLogger.logInfo("List is not sorted");
          return false;
        }
      } catch (Exception e) {
        ExtentReportLogger.logFail("Exception caught when checking if list is sorted", e);
      }
    } else {
      ExtentReportLogger.warning("List is empty");
    }
    return false;
  }

  /**
   * Touch Actions
   *
   * @param a1   axis 1
   * @param b1   axis 2
   * @param a2   axis 3
   * @param b2   axis 4
   * @param time time
   */
  @SuppressWarnings("rawtypes")
  private void touchActions(int a1, int b1, int a2, int b2, int time) {
    swipeCoordinates(a1, b1, a2, b2, time);
  }

  /**
   * Swipe with axix
   *
   * @param x    x axis
   * @param y    y axis
   * @param x1   x1 axis
   * @param y1   y1 axis
   * @param time timeInMilli
   */
  protected void swipeAxis(int x, int y, int x1, int y1, int count, int time) {
    for (int i = 0; i < count; i++) {
      touchActions(x, y, x1, y1, time);
    }
  }

  /**
   * tap to element for 250 ms
   *
   * @param androidElement element
   */
  @SuppressWarnings("rawtypes")
  public void tapByElement(WebElement androidElement) {
    tapElement(androidElement);
    try { Thread.sleep(250); } catch (InterruptedException ignored) {}
  }

  /**
   * Tap by coordinates
   *
   * @param x x
   * @param y y
   */
  @SuppressWarnings("rawtypes")
  public void tapByCoordinates(int x, int y) {
    try {
      pressCoordinates(x, y, 50);
      try { Thread.sleep(250); } catch (InterruptedException ignored) {}
    } catch (Exception e) {
      ExtentReportLogger.logFail("Exception in tapByCoordinates", e);
    }
  }

  /**
   * Press by element
   *
   * @param element element
   * @param seconds time
   */
  @SuppressWarnings("rawtypes")
  public void pressByElement(WebElement element, long seconds) {
    longPressElement(element, seconds * 1000);
  }

  /**
   * LongPress by element
   *
   * @param element element
   * @param seconds time
   */
  @SuppressWarnings("rawtypes")
  public void longPressByElement(WebElement element, long seconds) {
    longPressElement(element, seconds * 1000);
  }

  /**
   * Press by co-ordinates
   *
   * @param x       x
   * @param y       y
   * @param seconds time
   */
  @SuppressWarnings("rawtypes")
  public void pressByCoordinates(int x, int y, long seconds) {
    pressCoordinates(x, y, (int) (seconds * 1000));
  }

  /**
   * Horizontal swipe by percentage
   *
   * @param startPercentage  start
   * @param endPercentage    end
   * @param anchorPercentage anchor
   */
  @SuppressWarnings("rawtypes")
  public void horizontalSwipeByPercentage(double startPercentage, double endPercentage, double anchorPercentage) {
    Dimension size = DriverManager.getDriver().manage().window().getSize();
    int anchor = (int) (size.height * anchorPercentage);
    int startPoint = (int) (size.width * startPercentage);
    int endPoint = (int) (size.width * endPercentage);
    swipeCoordinates(startPoint, anchor, endPoint, anchor, 1000);
  }

  /**
   * Vertical swipe by percentage
   *
   * @param startPercentage  start
   * @param endPercentage    end
   * @param anchorPercentage anchor
   */
  @SuppressWarnings("rawtypes")
  public void verticalSwipeByPercentages(double startPercentage, double endPercentage, double anchorPercentage) {
    Dimension size = DriverManager.getDriver().manage().window().getSize();
    int anchor = (int) (size.width * anchorPercentage);
    int startPoint = (int) (size.height * startPercentage);
    int endPoint = (int) (size.height * endPercentage);

    swipeCoordinates(anchor, startPoint, anchor, endPoint, 1000);
  }

  /**
   * Swipe by elements
   *
   * @param startElement start
   * @param endElement   end
   */
  @SuppressWarnings("rawtypes")
  public void swipeByElements(WebElement startElement, WebElement endElement) {
    int startX = startElement.getLocation().getX() + (startElement.getSize().getWidth() / 2);
    int startY = startElement.getLocation().getY() + (startElement.getSize().getHeight() / 2);

    int endX = endElement.getLocation().getX() + (endElement.getSize().getWidth() / 2);
    int endY = endElement.getLocation().getY() + (endElement.getSize().getHeight() / 2);

    swipeCoordinates(startX, startY, endX, endY, 1000);
  }

  /**
   * Multi touch by element
   *
   * @param androidElement element
   */
  @SuppressWarnings("rawtypes")
  public void multiTouchByElement(WebElement androidElement) {
    try {
      // Multi-touch is complex to emulate reliably; fallback to a single press-release
      pressByElement(androidElement, 1);
    } catch (Exception e) {
      ExtentReportLogger.logFail("Exception in multiTouchByElement", e);
    }
    }

  // --- W3C PointerInput helpers ---
  private void tapElement(WebElement element) {
    PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
    Sequence tap = new Sequence(finger, 1);
    // move to center of element
    int centerX = element.getRect().getWidth() / 2;
    int centerY = element.getRect().getHeight() / 2;
    tap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.fromElement(element), centerX, centerY));
    tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
    tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
    DriverManager.getDriver().perform(List.of(tap));
  }

  private void longPressElement(WebElement element, long durationMillis) {
    PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
    Sequence seq = new Sequence(finger, 1);
    int centerX = element.getRect().getWidth() / 2;
    int centerY = element.getRect().getHeight() / 2;
    seq.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.fromElement(element), centerX, centerY));
    seq.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
    seq.addAction(new org.openqa.selenium.interactions.Pause(finger, Duration.ofMillis(durationMillis)));
    seq.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
    DriverManager.getDriver().perform(List.of(seq));
  }

  private void swipeCoordinates(int startX, int startY, int endX, int endY, int durationMillis) {
    PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
    Sequence swipe = new Sequence(finger, 1);
    swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
    swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
    swipe.addAction(finger.createPointerMove(Duration.ofMillis(durationMillis), PointerInput.Origin.viewport(), endX, endY));
    swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
    DriverManager.getDriver().perform(List.of(swipe));
  }

  private void pressCoordinates(int x, int y, int durationMillis) {
    PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
    Sequence seq = new Sequence(finger, 1);
    seq.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y));
    seq.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
    seq.addAction(new org.openqa.selenium.interactions.Pause(finger, Duration.ofMillis(durationMillis)));
    seq.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
    DriverManager.getDriver().perform(List.of(seq));
  }

}

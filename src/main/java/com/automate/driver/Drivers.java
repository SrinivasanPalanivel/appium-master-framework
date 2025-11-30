package com.automate.driver;

import com.automate.constants.FrameworkConstants;
import com.automate.customexceptions.DriverInitializationException;
import com.automate.enums.ConfigJson;
import com.automate.enums.MobileBrowserName;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.options.XCUITestOptions;
import io.appium.java_client.remote.AutomationName;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.CapabilityType;

import java.net.URI;
import java.net.URL;
import java.time.Duration;

import static com.automate.utils.configloader.JsonUtils.getConfig;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Drivers {

  public static AppiumDriver createAndroidDriverForNativeApp(String deviceName, String udid, int port, String emulator) {
    try {
      UiAutomator2Options options = new UiAutomator2Options();
      options.setPlatformName(Platform.ANDROID.name());
      options.setDeviceName(deviceName);
      options.setAutomationName(AutomationName.ANDROID_UIAUTOMATOR2);
      options.setUdid(udid);
      options.setApp(FrameworkConstants.ANDROID_APK_PATH);
      options.setAppPackage(getConfig(ConfigJson.APP_PACKAGE));
      options.setAppActivity(getConfig(ConfigJson.APP_ACTIVITY));
      options.setSystemPort(port);
      if (emulator.equalsIgnoreCase("yes")) {
        options.setAvd(deviceName);
        options.setAvdLaunchTimeout(Duration.ofSeconds(Integer.parseInt(getConfig(ConfigJson.AVD_LAUNCH_TIMEOUT))));
      }
      return new AndroidDriver(new URI(getConfig(ConfigJson.APPIUM_URL)).toURL(), options);
    } catch (Exception e) {
      throw new DriverInitializationException("Failed to initialize driver. Please check the desired capabilities", e);
    }
  }

  public static AppiumDriver createAndroidDriverForWeb(String deviceName, String udid, int port, String emulator) {
    try {
      UiAutomator2Options options = new UiAutomator2Options();
      options.setPlatformName(Platform.ANDROID.name());
      options.setDeviceName(deviceName);
      options.setAutomationName(AutomationName.ANDROID_UIAUTOMATOR2);
      options.setUdid(udid);
      options.setCapability(CapabilityType.BROWSER_NAME, MobileBrowserName.CHROME);
      options.setCapability("chromedriverPort", port);
      if (emulator.equalsIgnoreCase("yes")) {
        options.setCapability("avd", deviceName);
        options.setCapability("avdLaunchTimeout", Integer.parseInt(getConfig(ConfigJson.AVD_LAUNCH_TIMEOUT)));
      }

      return new AndroidDriver(new URL(getConfig(ConfigJson.APPIUM_URL)), options);
    } catch (Exception e) {
      throw new DriverInitializationException("Failed to initialize driver. Please check the desired capabilities", e);
    }
  }

  public static AppiumDriver createIOSDriverForNativeApp(String deviceName, String udid, int port) {
    try {
      XCUITestOptions options = new XCUITestOptions();
      options.setPlatformName(Platform.IOS.name());
      options.setDeviceName(deviceName);
      options.setAutomationName(AutomationName.IOS_XCUI_TEST);
      options.setUdid(udid);
      options.setApp(FrameworkConstants.IOS_APP_PATH);
      options.setCapability("bundleId", getConfig(ConfigJson.BUNDLE_ID));
      options.setCapability("wdaLocalPort", port);

      return new IOSDriver(new URL(getConfig(ConfigJson.APPIUM_URL)), options);
    } catch (Exception e) {
      throw new DriverInitializationException("Failed to initialize driver. Please check the desired capabilities", e);
    }
  }

  public static AppiumDriver createIOSDriverForWeb(String deviceName, String udid, int port) {
    try {
      XCUITestOptions options = new XCUITestOptions();
      options.setPlatformName(Platform.IOS.name());
      options.setDeviceName(deviceName);
      options.setAutomationName(AutomationName.IOS_XCUI_TEST);
      options.setUdid(udid);
      options.setCapability("bundleId", getConfig(ConfigJson.BUNDLE_ID));
      options.setCapability(CapabilityType.BROWSER_NAME, MobileBrowserName.SAFARI);
      options.setCapability("webkitDebugProxyPort", port);

      return new IOSDriver(new URL(getConfig(ConfigJson.APPIUM_URL)), options);
    } catch (Exception e) {
      throw new DriverInitializationException("Failed to initialize driver. Please check the desired capabilities", e);
    }
  }
}

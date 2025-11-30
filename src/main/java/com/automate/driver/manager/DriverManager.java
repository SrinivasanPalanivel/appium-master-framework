package com.automate.driver.manager;

import io.appium.java_client.AppiumDriver;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DriverManager {

  private static final ThreadLocal<AppiumDriver> threadLocalDriver = new ThreadLocal<>();

  public static AppiumDriver getDriver() {
    return threadLocalDriver.get();
  }

  public static void setAppiumDriver(AppiumDriver driver) {
    if (Objects.nonNull(driver))
      threadLocalDriver.set(driver);
  }

  public static void unload() {
    threadLocalDriver.remove();
  }
}

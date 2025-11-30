package com.automate.pages;

import com.automate.pages.screen.ScreenActions;
import org.openqa.selenium.WebElement;
import io.appium.java_client.pagefactory.AndroidFindBy;

public final class SettingsPage extends ScreenActions {

  @AndroidFindBy(accessibility = "test-LOGOUT")
  private WebElement logOutButton;

  public LoginPage pressLogOutButton() {
    click(logOutButton, "Logout");
    return new LoginPage();
  }
}

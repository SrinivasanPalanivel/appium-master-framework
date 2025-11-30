package com.automate.pages;

import com.automate.pages.screen.ScreenActions;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public final class GoogleSearchPage extends ScreenActions {

  @FindBy(xpath = "//input[@aria-label='Search']")
  private WebElement txtFieldSearch;

  public GoogleSearchResultPage performSearch(String searchText) {
    enterValueAndPressEnter(txtFieldSearch, searchText, "Search text box");
    return new GoogleSearchResultPage();
  }
}

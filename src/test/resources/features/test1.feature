Feature: Basic Test-Selenium

  Scenario Outline: Test Login Page- <case>
    When I open the page "https://practicetestautomation.com/practice/".
    Then I click the button  with xpath "//*[text()='Test Login Page']" on the page.
    When I execute the case "<case>" with userName "<username>" and password "<password>".
    Then check expectedResult"<expectedResult>".
    Examples:
      | case                                | username      | password          | expectedResult                                     |
      | Test case 1: Positive LogIn test    | student       | Password123       | practicetestautomation.com/logged-in-successfully/ |
      | Test case 2: Negative username test | incorrectUser | Password123       | Your username is invalid!                          |
      | Test case 3: Negative password test | student       | incorrectPassword | Your password is invalid!                          |


  Scenario Outline: Test Exceptions- <case>
    When I open the page "https://practicetestautomation.com/practice/".
    Then I click the button  with xpath "//*[text()='Test Exceptions']" on the page.
    Then I execute the case with operation "<operation>" and check result "<expectedResult>".
    Examples:
      | case                                         | operation                       | expectedResult                  |
      | Test case 1: NoSuchElementException          | Click Add button                | NoSuchElementException          |
      | Test case 2: ElementNotInteractableException | Push Save button                | ElementNotInteractableException |
      | Test case 3: InvalidElementStateException    | Clear input field               | InvalidElementStateException    |
      | Test case 4: StaleElementReferenceException  | Verify instruction text element | StaleElementReferenceException  |
      | Test case 5: TimeoutException                | Click Add button                | TimeoutException                |
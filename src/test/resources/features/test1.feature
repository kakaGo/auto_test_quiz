Feature: Question1
  Scenario: test1
    When I open the page "https://practicetestautomation.com/practice/".
    Then I click the button "Test Login Page" with xpath "//*[text()='Test Login Page']" on the page.
    #Test case 1: Positive LogIn test
    When I input "student" into the field with xpath "//*[@id='username'] " on the page.
    When I input "Password123" into the field with xpath "//*[@id='password'] " on the page.
    Then I click the button "Test Login Page" with xpath "//button[text()='Submit']" on the page.
    Then I can check the page url contain "practicetestautomation.com/logged-in-successfully/".
    Then I can check the page text contain "Congratulations" or "successfully logged in".
    Then I click the button "Test Login Page" with xpath "//*[text()='Log out']" on the page.
    Then I wait for "2" seconds.
    #Test case 2: Negative username test
    When I input "incorrectUser" into the field with xpath "//*[@id='username'] " on the page.
    When I input "Password123" into the field with xpath "//*[@id='password'] " on the page.
    Then I click the button "Test Login Page" with xpath "//button[text()='Submit']" on the page.
    Then I can check the message with xpath "//*[@id='error']" appear.
    Then I can check the message the message is "Your username is invalid!" with the path "//*[@id='error']".
    Then I wait for "2" seconds.
    #Test case 3: Negative password test
    When I input "student" into the field with xpath "//*[@id='username'] " on the page.
    When I input "incorrectPassword" into the field with xpath "//*[@id='password'] " on the page.
    Then I click the button "Test Login Page" with xpath "//button[text()='Submit']" on the page.
    Then I can check the message with xpath "//*[@id='error']" appear.
    Then I can check the message the message is "Your password is invalid!" with the path "//*[@id='error']".
    #case4
    Then I wait for "2" seconds.
    Then I return the Previous Page.
    Then I wait for "2" seconds.
    Then I return the Previous Page.
    Then I wait for "2" seconds.
    Then I return the Previous Page.
    Then I wait for "2" seconds.
    Then I can check the page url contain "https://practicetestautomation.com/practice/".
    Then I can check the page text contain "Test Exceptions" or "Page to reproduce the most common Selenium Exceptions.".
    Then I wait for "2" seconds.
    Then I click the button "Test Exceptions" with xpath "//*[text()='Test Exceptions']" on the page.
    #Exception
    #Test case 1: NoSuchElementException
    Then I click the button "Add" with xpath "//button[text()='Add']" on the page.
    Then I can check the element with xpath "//*[@id="row2"]" displayed.

    #Test case 2: ElementNotInteractableException
    Then I click the button "Add" with xpath "//button[text()='Add']" on the page.
    Then I wait until the element with xpath "//*[@id="row2"]" display.
    When I input "second text" into the field with xpath "//*[@id="row2"]" on the page.
    Then I click the button by "name" with path "Save" on the page.
    Then I can check the message with xpath "//*[text()="Row 2 was saved"]" appear.

    #Test case 3: InvalidElementStateException
    Then I clear the input field with xpath "//*[@id="row1"]/input" on the page.
    When I input "change text" into the field with xpath "//*[@id="row1"]/input" on the page.
    Then I can check the message the message is "change text" with the path "//*[@id="row1"]/input".

    #Test case 4: StaleElementReferenceException
    Then I can check the message the message is "Push “Add” button to add another row" with the path "//p[@id="instructions"]".
    Then I click the button "Add" with xpath "//button[text()='Add']" on the page.
    Then I can check the element with xpath "//p[@id="instructions"]" disappear.

    #Test case 5: TimeoutException
    Then I click the button "Add" with xpath "//button[text()='Add']" on the page.
    Then Wait for "3" seconds for the second input field to be displayed with xpath "//*[@id="row2"]".
    Then I can check the message with xpath "//*[@id="row2"]" appear.





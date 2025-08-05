Feature: Question2
  Scenario: test
    When I open the page "https://opensource-demo.orangehrmlive.com/web/index.php/auth/login".
    Then I wait for "10000" seconds.
    #case1
    When I input "Admin" into the field with xpath "//input[@name='username']" on the page.
    When I input "admin123" into the field with xpath "//input[@name='password']" on the page.
    Then I click the button "Test Login Page" with xpath "//button[@type='submit']" on the page.
    Then I can check the page url contain "opensource-demo.orangehrmlive.com/web/index.php/dashboard/index".
    Then I wait for "2000" seconds.
    Then I click the button "Test Login Page" with xpath "//*[text()='Claim']/.." on the page.
    Then I wait for "2000" seconds.
    Then I click the button "Test Login Page" with xpath "//a[text()="Employee Claims"]" on the page.
    Then I wait for "2000" seconds.
    Then I click the button "Test Login Page" with xpath "//button[text()=" Assign Claim "]" on the page.
    Then I wait for "2000" seconds.
    When I input "Amelia  Brown" on the element with xpath "//input[@placeholder="Type for hints..."]".
    Then I select "Travel Allowance" on the element with xpath "(//*[@class="oxd-select-text-input"])[1]".
    Then I select "Euro" on the element with xpath "(//*[@class="oxd-select-text-input"])[2]".
    Then I click the button "Test Login Page" with xpath "//button[text()=" Create "]" on the page.






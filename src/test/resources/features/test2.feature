Feature: Question2

  Scenario: test
    When I open the page "https://opensource-demo.orangehrmlive.com/web/index.php/auth/login".
    Then I wait for "10000" seconds.
    #登录
    When I input "Admin" into the field with xpath "//input[@name='username']" on the page.
    When I input "admin123" into the field with xpath "//input[@name='password']" on the page.
    Then I click the button "Test Login Page" with xpath "//button[@type='submit']" on the page.
    Then I can check the page url contain "opensource-demo.orangehrmlive.com/web/index.php/dashboard/index".
    Then I wait for "2000" seconds.
    #点击左侧菜单栏Claim
    Then I click the button "Test Login Page" with xpath "//*[text()='Claim']/.." on the page.
    Then I wait for "2000" seconds.
    #点击上方菜单栏Employee Claims
    Then I click the button "Test Login Page" with xpath "//a[text()="Employee Claims"]" on the page.
    Then I wait for "2000" seconds.
    #点击新增一条数据
    Then I click the button "Test Login Page" with xpath "//button[text()=" Assign Claim "]" on the page.
    Then I wait for "2000" seconds.
    #输入表单数据
    When I input "Amelia  Brown" on the element with xpath "//input[@placeholder="Type for hints..."]".
    Then I wait for "2000" seconds.
    Then I select "Travel Allowance" on the element with xpath "(//*[@class="oxd-select-text-input"])[1]".
    Then I wait for "2000" seconds.
    Then I select "Euro" on the element with xpath "(//*[@class="oxd-select-text-input"])[2]".
    Then I wait for "2000" seconds.
    #点击保存
    Then I click the button "Test Login Page" with xpath "//button[text()=" Create "]" on the page.
    Then I wait for "1000" seconds.
    #校验成功
    Then I check the success message is "Success" with the xpath "//p[text()='Success']".
    Then I check the success message is "Successfully Saved" with the xpath "//p[text()='Successfully Saved']".
    Then I wait for "7000" seconds.
    #校验进入页面
    Then I can check the page url contain "opensource-demo.orangehrmlive.com/web/index.php/claim/assignClaim/id/".
    Then I wait for "2000" seconds.
    #校验详情页面数值等于添加的数值
    Then I can check the value is "Amelia Brown" with the path "//*[@class='oxd-form']/div[1]//input".
    Then I wait for "2000" seconds.
    Then I can check the value is "Travel Allowance" with the path "//*[@class='oxd-form']/div[3]//input".
    Then I wait for "2000" seconds.
    Then I can check the value is "Euro" with the path "(//*[@class='oxd-form']/div[2]//input)[2]".
    Then I wait for "2000" seconds.
    Then I can check the value is "Initiated" with the path "(//*[@class='oxd-form']/div[2]//input)[3]".
    Then I wait for "2000" seconds.
    #在详情页面添加扩展信息
    Then I click the button "Test Login Page" with xpath "//h6[text()="Expenses"]/../button" on the page.
    Then I wait for "2000" seconds.
    Then I select "Transport" on the element with xpath "//*[text()="-- Select --"]".
    Then I wait for "2000" seconds.
    Then I click the button "Test Login Page" with xpath "//*[@class="oxd-date-input"]/input" on the page.
    Then I wait for "2000" seconds.
    Then I click the button "Test Login Page" with xpath "//*[text()="Today"]" on the page.
    Then I wait for "2000" seconds.
    When I input "888.00" into the field with xpath "//label[text()="Amount"]/../..//input" on the page.
    Then I wait for "2000" seconds.
    #保存扩展信息并校验成功提示
    Then I click the button "Test Login Page" with xpath "//button[text()=" Save "]" on the page.
    Then I wait for "1000" seconds.
    Then I check the success message is "Success" with the xpath "//p[text()='Success']".
    Then I check the success message is "Successfully Saved" with the xpath "//p[text()='Successfully Saved']".
    Then I wait for "5000" seconds.
    #校验添加的扩展提示
    Then I check TableList row "1" value with table xpath "(//*[@class="orangehrm-container"]/*[@role="table"])[1]".
      | title         | value      |
      | Expense Type  | Transport  |
      | Date          | 2025-06-08 |
      | Amount (Euro) | 888.00     |
    #返回上一页
    Then I wait for "2000" seconds.
    Then I click the button "Test Login Page" with xpath "//button[text()=" Back "]" on the page.
    #校验列表中该条数据的全部数值等于创建时添加的数值
    Then I wait for "5000" seconds.
    Then I check TableList row "1" value with table xpath "(//*[@class="orangehrm-container"]/*[@role="table"])[1]".
      | title          | value            |
      | Employee Name  | Amelia Brown     |
      | Event Name     | Travel Allowance |
      | Currency       | Euro             |
      | Amount         | 888.00           |
      | Submitted Date | 2025-06-08       |








# InventoryManagementAPITest_RestAssured

This project provides automated API tests for an Inventory Management system using Java, RestAssured, TestNG, and Allure for reporting.

## Key Methods and Technologies Used

- **RestAssured:** For making HTTP requests and validating API responses.
- **TestNG:** For organizing and running test cases.
- **Allure:** For generating detailed and interactive test reports.
- **Maven:** For dependency management and build automation.

## Example Test Workflow

1. **Setup:** Initialize RestAssured base URI and authentication in a base test class.
2. **Test Execution:** Use TestNG to run test methods that:
   - Send HTTP requests (GET, POST, PUT, DELETE) to API endpoints.
   - Validate status codes, response bodies, and headers.
   - Use assertions to check business logic.
3. **Reporting:Pending ** Generate Allure reports after test execution for detailed insights.
4. 

## How to Run

1. Install dependencies:
   ```
   mvn clean install
   ```
2. Run tests:
   ```
   mvn test
   ```
3. Generate Allure report:
   ```
   mvn allure:report
   ```
4. Open the report:
   - Open `target/site/allure-maven-plugin/index.html` in your browser, or
   - Run `mvn allure:serve` to view the report locally.

## Suggestions for Extension

- Add CI/CD integration for automated testing.
- Parameterize tests for broader coverage.
- Integrate with API contract validation tools.
- Add performance and security tests.

> For more details, refer to the code comments and structure in the repository.
```
````

</file>

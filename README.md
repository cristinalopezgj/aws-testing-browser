# aws-testing-browser

Before run the test, please input the following data in the properties file:

- AWS-ACCESS-KEY
- AWS-SECRET-KEY
- AWS-PROJECT-ARN

The value of aws-project-arn can be obtained from AWS Device Farm creating a new Desktop Browser Project

To run the test:

```
mvn clean test
```

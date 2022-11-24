# AWS Lambda Get Events (Java)

An application for scraping events from websites.

The project source includes function code and supporting resources:

- `src/main` - A Java Lambda function that scales down an image stored in S3.
- `src/test` - A unit test and helper classes.
- `pom.xml` - A Maven build file.

Use the following instructions to deploy the sample application.

# Requirements
- [Java 11 runtime environment (SE JRE)](https://www.oracle.com/java/technologies/javase-downloads.html)
- [Maven 3](https://maven.apache.org/docs/history.html)
- [The AWS account](https://aws.amazon.com/)


# Setup
Download or clone this repository.

    $ git clone https://github.com/Brogzybro/aws-lambda-get-events.git

In AWS do the following:
- Make sure you are using correct region.
- Create a new s3 bucket for where csv files can be stored.
- Create a new lambda function for Java 11.
- Create a trigger to the lambda function, use EventBridge (CloudWatch Events) as the trigger.

# Deploy
Instruction on how to deploy the code to lambda. 

First package the code using maven: `mvn package`. This will create a `.jar` file `/target/aws-lambda-get-events-1.0-SNAPSHOT.jar`

In lambda under `Code` and `Code source` press `upload from .zip or .jar file`. Select the `.jar` file produced in previous step.

# Test
### Testing locally
The Handler can be tested locally running the function `Handler.main()`.
Using this to debug is very useful when creating Scrapers as it makes it easier to see how the webpage and the HTML elements are structured.

### Testing the lambda function
To test the lambda function got to `Test` and create a new test event.

Give the event a name and create a json for a Scheduled event. For example like this:

    {
    "id": "cdc73f9d-aea9-11e3-9d5a-835b769c0d9c",
    "detail-type": "Scheduled Event",
    "source": "aws.events",
    "account": "123456789012",
    "time": "1970-01-01T00:00:00Z",
    "region": "us-east-1",
    "resources": [
    "arn:aws:events:us-east-1:123456789012:rule/ExampleRule"
    ],
    "detail": {}
    }

You can then save and test using the Event.

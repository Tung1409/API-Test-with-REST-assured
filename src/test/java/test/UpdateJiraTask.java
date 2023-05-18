package test;

import builder.IssueContentBuilder;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import models.IssueField;
import models.RequestCapability;
import org.apache.commons.lang3.RandomStringUtils;
import utils.*;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class UpdateJiraTask implements RequestCapability {

    public static void main(String[] args) {
        // API info
        String baseUri = "https://tung1409.atlassian.net";
        String path = "/rest/api/3/issue";
        String projectKey = "AP";

        String email = System.getenv("email");
        String apiToken = System.getenv("token");
        String encodedCredStr = AuthenticationHandler.encodeCredStr(email, apiToken);

        // Request Object
        RequestSpecification request = given();
        request.baseUri(baseUri);
        request.header(defaultHeader);
        request.header(acceptJSONHeader);
        request.header(getAuthenticatedHeader.apply(encodedCredStr));

        // Define body data

        ProjectInfo projectInfo = new ProjectInfo(baseUri, projectKey);
        String taskTypeId = projectInfo.getIssueTypeId("task");
        int desiredLength = 20;
        boolean hasLetters = true;
        boolean hasNumbers = true;
        String randomSummary  = RandomStringUtils.random(desiredLength, hasLetters, hasNumbers);
        IssueContentBuilder issueContentBuilder = new IssueContentBuilder();
        String issueFieldContent = issueContentBuilder.build(projectKey, taskTypeId, randomSummary);

        // Create Jira task
        Response response = request.body(issueFieldContent).post(path);

        // Check created task details
        Map<String, String> responeBody = JsonPath.from(response.asString()).get();
        final String CREATED_ISSUE_KEY = responeBody.get("key");

        // Read created jira task
        IssueField issueField = issueContentBuilder.getIssueField();
        String expectedSummary = issueField.getFields().getSummary();
        String expectedStatus = "To Do";

        Map<String, String> issueInfo = IssueInfo.getIssueInfo(CREATED_ISSUE_KEY);

        System.out.println("Expected summary: " + expectedSummary);
        System.out.println("Actual summary: " + issueInfo.get("summary"));

        System.out.println("Expected status: " + expectedStatus);
        System.out.println("Actual status: " + issueInfo.get("status"));

        TransitionInfo transitionInfo = new TransitionInfo(baseUri, CREATED_ISSUE_KEY);
        String transitionTypeId = transitionInfo.getTransitionTypeId("Done");
        String issueTransitionPath = "/rest/api/3/issue/" + CREATED_ISSUE_KEY + "/transitions";
        String transitionBody = "{\n" +
                "  \"transition\": {\n" +
                "    \"id\": "+ transitionTypeId +"\n" +
                "  }\n" +
                "}";

        request.body(transitionBody).post(issueTransitionPath).then().statusCode(204);
        issueInfo = IssueInfo.getIssueInfo(CREATED_ISSUE_KEY);
        String latestStatus = issueInfo.get("status");
        System.out.println("latestIssueStatus: " + latestStatus);
    }
}

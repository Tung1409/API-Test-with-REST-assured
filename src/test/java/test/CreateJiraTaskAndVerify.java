package test;

import builder.IssueContentBuilder;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import models.IssueField;
import models.RequestCapability;
import org.apache.commons.lang3.RandomStringUtils;
import utils.AuthenticationHandler;
import utils.ProjectInfo;

import java.util.Map;
import java.util.Objects;

import static io.restassured.RestAssured.given;

public class CreateJiraTaskAndVerify implements RequestCapability {

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
        String getIssuePath = "/rest/api/3/issue/" + responeBody.get("key");

        // Read created jira task
        response = request.get(getIssuePath);
        IssueField issueField = issueContentBuilder.getIssueField();
        String expectedSummary = issueField.getFields().getSummary();
        String expectedStatus = "To Do";

        Map<String, Object> fields = JsonPath.from(response.getBody().asString()).get("fields");
        String actualSummary = fields.get("summary").toString();
        Map<String, Object> status = (Map<String, Object>) fields.get("status");
        Map<String, Object> statusCategory = (Map<String, Object>) status.get("statusCategory");
        String actualStatus = statusCategory.get("name").toString();

        System.out.println("Expected summary: " + expectedSummary);
        System.out.println("Actual summary: " + actualSummary);

        System.out.println("Expected status: " + expectedStatus);
        System.out.println("Actual status: " + actualStatus);

    }
}

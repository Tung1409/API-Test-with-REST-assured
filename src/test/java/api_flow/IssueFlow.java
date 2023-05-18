package api_flow;

import builder.BodyJSONBuilder;
import builder.IssueContentBuilder;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import models.IssueField;
import models.IssueTransition;
import models.RequestCapability;
import org.apache.commons.lang3.RandomStringUtils;
import utils.AuthenticationHandler;
import utils.IssueInfo;
import utils.ProjectInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class IssueFlow {

    private static Map<String, String> transitionTypeMap = new HashMap<>();
    private static final String issuePathPrefix = "/rest/api/3/issue";
    private RequestSpecification request;
    private String baseUri;
    private Response response;
    private String createdIssueKey;
    private String projectKey;
    private String issueTypeStr;
    private IssueField issueField;
    private String status;

    static {
        transitionTypeMap.put("11", "To Do");
        transitionTypeMap.put("21", "In Progress");
        transitionTypeMap.put("31", "Done");
    }

    public IssueFlow(RequestSpecification request, String baseUri, String projectKey, String issueTypeStr) {
        this.request = request;
        this.baseUri = baseUri;
        this.projectKey = projectKey;
        this.issueTypeStr = issueTypeStr;
        this.status = "To do";
    }

    @Step("Creating Jira Issue")
    public void createIssue() {
        ProjectInfo projectInfo = new ProjectInfo(baseUri, projectKey);
        String taskTypeId = projectInfo.getIssueTypeId(issueTypeStr);
        int desiredLength = 20;
        boolean hasLetters = true;
        boolean hasNumbers = true;
        String randomSummary = RandomStringUtils.random(desiredLength, hasLetters, hasNumbers);
        IssueContentBuilder issueContentBuilder = new IssueContentBuilder();
        String issueFieldContent = issueContentBuilder.build(projectKey, taskTypeId, randomSummary);
        issueField = issueContentBuilder.getIssueField();
        this.response = request.body(issueFieldContent).post(issuePathPrefix);

        Map<String, String> responeBody = JsonPath.from(response.asString()).get();
        createdIssueKey = responeBody.get("key");
    }

    @Step("Verifying Jira Issue")
    public void verifyIssueDetails() {
        Map<String, String> issueInfo = getIssueInfo();
        String expectedSummary = issueField.getFields().getSummary();
        String expectedStatus = status;
        String actualSummary = issueInfo.get("summary");
        String actualStatus = issueInfo.get("status");

        System.out.println("Expected summary: " + expectedSummary);
        System.out.println("Actual summary: " + actualSummary);

        System.out.println("Expected status: " + expectedStatus);
        System.out.println("Actual status: " + actualStatus);
    }

    @Step("Updating Jira Issue")
    public void updateIssue(String issueStatusStr) {
        String targetTransitionId = null;
        for (String transitionId : transitionTypeMap.keySet()) {
            if (transitionTypeMap.get(transitionId).equalsIgnoreCase(issueStatusStr)) {
                targetTransitionId = transitionId;
                break;
            }
        }

        if (targetTransitionId == null) {
            throw new RuntimeException("[ERR] Issue status string provided is not supported: ");
        }

        String issueTransitionPath = issuePathPrefix + "/" + createdIssueKey + "/transitions";
        IssueTransition.Transition transition = new IssueTransition.Transition(targetTransitionId);
        IssueTransition issueTransition = new IssueTransition(transition);
        String transitionBody = BodyJSONBuilder.getJSONContent(issueTransition);

        request.body(transitionBody).post(issueTransitionPath).then().statusCode(204);
        Map<String, String> issueInfo = getIssueInfo();
        String actualIssueStatus = issueInfo.get("status");
        String expectedIssueStatus = transitionTypeMap.get(targetTransitionId);
        System.out.println("Actual Issue Status: " + actualIssueStatus);
        System.out.println("Expected Issue Status: " + expectedIssueStatus);
    }

    @Step("Deleting Jira Issue")
    public void deleteIssue() {
        String path = issuePathPrefix + "/" + createdIssueKey;
        request.delete(path);

        // Verify issue is not existing
        response = request.get(path);
        Map<String, List<String>> notExistingIssueRes = JsonPath.from(response.body().asString()).get();
        List<String> errorMessages = notExistingIssueRes.get("errorMessages");
        System.out.println("Return msg: " + errorMessages);
    }

    private Map<String, String> getIssueInfo() {
        String getIssuePath = issuePathPrefix + "/" + createdIssueKey;
        Response response_ = request.get(getIssuePath);

        Map<String, Object> fields = JsonPath.from(response_.getBody().asString()).get("fields");
        String actualSummary = fields.get("summary").toString();
        Map<String, Object> status = (Map<String, Object>) fields.get("status");
        Map<String, Object> statusCategory = (Map<String, Object>) status.get("statusCategory");
        String actualStatus = statusCategory.get("name").toString();

        Map<String, String> issueInfo = new HashMap<>();
        issueInfo.put("summary", actualSummary);
        issueInfo.put("status", actualStatus);
        return issueInfo;
    }
}

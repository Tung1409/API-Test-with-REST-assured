package test;

import api_flow.IssueFlow;
import io.restassured.specification.RequestSpecification;
import models.RequestCapability;
import org.testng.annotations.Test;
import utils.AuthenticationHandler;

import static io.restassured.RestAssured.given;

public class JiraIssueCRUD extends BaseTest {

    @Test
    public void testE2EFlow() {
        IssueFlow issueFlow = new IssueFlow(request, baseUri, projectKey, "task");
        issueFlow.createIssue();
        issueFlow.verifyIssueDetails();
        issueFlow.updateIssue("Done");
        issueFlow.verifyIssueDetails();
        issueFlow.deleteIssue();
    }
}

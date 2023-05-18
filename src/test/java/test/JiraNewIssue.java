package test;

import builder.BodyJSONBuilder;
import builder.IssueContentBuilder;
import com.google.gson.Gson;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import models.IssueField;
import models.RequestCapability;
import utils.AuthenticationHandler;
import utils.ProjectInfo;

import static io.restassured.RestAssured.given;

public class JiraNewIssue implements RequestCapability {

    public static void main(String[] args) {
        // API info
        String baseUri = "https://tung1409.atlassian.net";
        String path = "/rest/api/3/issue";
        String projectKey = "AP";

//        String email = "caoduytung1996@gmail.com";
//        String apiToken = "ATATT3xFfGF0Q5MBehh6nHm2fJ6m9otneWb46xBO2hxSk4LxnLnqNiogiHo00VoasvAseasTErE7gjX-VpR47d116671f11E9i3fc1sSJIyLw1JpcSsrmB-djsbUVCQo_LD0tVwXhZut4n2ZW8JK2XdEmm4usyafAN7Kmw1zCy6QNMbOJocu9SQ=6AE13D65";
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
        String taskIssueType = projectInfo.getIssueTypeId("task");
        String summary = "Summary | IssueContentBuilder";
        String issueFieldContent = new IssueContentBuilder().build(projectKey, taskIssueType, summary);

        // Send request
        Response response = request.body(issueFieldContent).post(path);
        response.prettyPrint();
    }
}

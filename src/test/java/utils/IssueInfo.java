package utils;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import models.RequestCapability;

import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

public class IssueInfo implements RequestCapability  {

    public static Map<String, String> getIssueInfo(String issueKey){
        String baseUri = "https://tung1409.atlassian.net";
        String getIssuePath = "/rest/api/3/issue/" + issueKey;
        String email = System.getenv("email");
        String apiToken = System.getenv("token");
        String encodedCredStr = AuthenticationHandler.encodeCredStr(email, apiToken);

        RequestSpecification request = given();
        request.baseUri(baseUri);
        request.header(defaultHeader);
        request.header(acceptJSONHeader);
        request.header(RequestCapability.getAuthenticatedHeader(encodedCredStr));

        Response response = request.get(getIssuePath);
        Map<String, Object> fields = JsonPath.from(response.getBody().asString()).get("fields");
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

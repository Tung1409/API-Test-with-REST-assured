package utils;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import models.RequestCapability;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class TransitionInfo implements RequestCapability {

    private String baseUri;
    private String CREATED_ISSUE_KEY;
    private List<Map<String, String>> transitionType;
    private Map<String, List<Map<String, String>>> transitionInfo;

    public TransitionInfo(String baseUri, String CREATED_ISSUE_KEY) {
        this.baseUri = baseUri;
        this.CREATED_ISSUE_KEY = CREATED_ISSUE_KEY;
        getTransitionInfo();
    }

    public String getTransitionTypeId(String transitionTypeStr) {
        getTransitionType();

        String transitionTypeId = null;
        for (Map<String, String> transitionType : transitionType) {
            if (transitionType.get("name").equalsIgnoreCase(transitionTypeStr)) {
                transitionTypeId = transitionType.get("id");
                break;
            }
        }

        if (transitionTypeId == null) {
            throw new RuntimeException("[ERR] Could not find the id for " + transitionTypeStr);
        }
        return transitionTypeId;
    }

    private void getTransitionType() {
        transitionType = transitionInfo.get("transitions");
    }

    private void getTransitionInfo() {
        String getIssueTransitions = "/rest/api/3/issue/" + CREATED_ISSUE_KEY + "/transitions";

        String email = System.getenv("email");
        String apiToken = System.getenv("token");
        String encodedCredStr = AuthenticationHandler.encodeCredStr(email, apiToken);

        RequestSpecification request = given();
        request.baseUri(baseUri);
        request.header(defaultHeader);
        request.header(RequestCapability.getAuthenticatedHeader(encodedCredStr));
        Response response = request.get(getIssueTransitions);
        transitionInfo = JsonPath.from(response.asString()).get();
    }
}

package test;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import models.RequestCapability;
import org.apache.commons.codec.binary.Base64;
import utils.ProjectInfo;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static io.restassured.RestAssured.given;

public class JiraIssueTypes implements RequestCapability {

    public static void main(String[] args) {
        String baseUri = "https://tung1409.atlassian.net";
        String projectKey = "AP";

        ProjectInfo projectInfo = new ProjectInfo(baseUri, projectKey);
        System.out.println("Task ID: " + projectInfo.getIssueTypeId("epic"));

    }

}

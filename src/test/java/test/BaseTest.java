package test;

import io.restassured.specification.RequestSpecification;
import models.RequestCapability;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import utils.AuthenticationHandler;

import static io.restassured.RestAssured.given;

public class BaseTest implements RequestCapability {

    protected String baseUri;
    protected String projectKey;
    protected String email;
    protected String apiToken;
    protected String encodedCredStr;
    protected RequestSpecification request;

    @BeforeSuite
    public void beforeSuite(){
        baseUri = "https://tung1409.atlassian.net";
        projectKey = "AP";

        email = System.getenv("email");
        apiToken = System.getenv("token");
        encodedCredStr = AuthenticationHandler.encodeCredStr(email, apiToken);
    }

    @BeforeTest
    public void beforeTest(){
        request = given();
        request.baseUri(baseUri);
        request.header(defaultHeader);
        request.header(acceptJSONHeader);
        request.header(getAuthenticatedHeader.apply(encodedCredStr));
    }
}

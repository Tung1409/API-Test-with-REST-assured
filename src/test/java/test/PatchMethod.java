package test;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import models.BuildModelJSON;
import models.PostBody;
import models.RequestCapability;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class PatchMethod implements RequestCapability {

    public static void main(String[] args) {

        String baseUri = "https://jsonplaceholder.typicode.com";

        RequestSpecification request = given();
        request.baseUri(baseUri);
        request.header(defaultHeader);

        PostBody postBody = new PostBody();
        postBody.setTitle("New title");

        String postBodyStr = BuildModelJSON.parseJSONString(postBody);

        final String TARGET_POST_ID = "1";
        Response response = request.body(postBodyStr).patch("/posts/".concat(TARGET_POST_ID));
        response.then().body("title", equalTo(postBody.getTitle()));
        response.prettyPrint();
    }
}

package test;

import com.google.gson.Gson;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import models.PostBody;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.containsStringIgnoringCase;

public class PostMethod {

    public static void main(String[] args) {
        String baseUri = "https://jsonplaceholder.typicode.com";

        // Request scope
        RequestSpecification request = given();
        request.baseUri(baseUri);

        // Content-type -> header
        request.header(new Header("Content-type", "application/json; charset=UTF-8"));

        // Form up request body
//        String postBody = "{\n" +
//                "  \"userId\": 1,\n" +
//                "  \"id\": 1,\n" +
//                "  \"title\": \"req's title\",\n" +
//                "  \"body\": \"req's body\"\n" +
//                "}";

        Gson gson = new Gson();
        PostBody postBody = new PostBody();
        postBody.setUserId(1);
        postBody.setTitle("req's title");
        postBody.setBody("req's body");

        // Send post request
        Response response = request.body(gson.toJson(postBody)).post("/posts");
        response.prettyPrint();


        // Verification
        response.then().statusCode(equalTo(201));
        response.then().statusLine(containsStringIgnoringCase("201 Created"));
        response.then().body("userId", equalTo(1));
        response.then().body("title", equalTo("req's title"));
        response.then().body("body", equalTo("req's body"));
    }
}

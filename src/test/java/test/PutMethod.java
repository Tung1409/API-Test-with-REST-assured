package test;

import com.google.gson.Gson;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import models.PostBody;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class PutMethod {

    public static void main(String[] args) {
        String baseUri = "https://jsonplaceholder.typicode.com";

        RequestSpecification request = given();
        request.baseUri(baseUri);
        request.header(new Header("Content-type", "application/json; charset=UTF-8"));

        PostBody postBody1 = new PostBody(1, 1, "New title1", "New Body1");
        PostBody postBody2 = new PostBody(1, 1, "New title2", "New Body2");
        PostBody postBody3 = new PostBody(1, 1, "New title3", "New Body3");

        List<PostBody> postBodyList = Arrays.asList(postBody1, postBody2, postBody3);
        for (PostBody postBody : postBodyList) {
            System.out.println(postBody);

            Gson gson = new Gson();
            String postBodyString = gson.toJson(postBody);

            final int TARGET_POST_NUM = 1;
            Response response =  request.body(postBodyString).put("/posts/".concat(String.valueOf(TARGET_POST_NUM)));
            response.then().body("userId", equalTo(postBody.getUserId()));
            response.then().body("id", equalTo(postBody.getId()));
            response.then().body("title", equalTo(postBody.getTitle()));
            response.then().body("body", equalTo(postBody.getBody()));
        }
    }
}

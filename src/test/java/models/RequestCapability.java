package models;

import io.restassured.http.Header;

import java.util.function.Function;

public interface RequestCapability {

    Header defaultHeader = new Header("Content-type", "application/json; charset=UTF-8");
    Header acceptJSONHeader = new Header("Accept", "application/json");

    static Header getAuthenticatedHeader(String encodedCredStr) {
        if (encodedCredStr == null) {
            throw new IllegalArgumentException("[ERR] encodedCredStr can't be null");
        }
        return new Header("Authorization", "Basic " + encodedCredStr);
    }

    //cach 2 dung Functional interface

    Function<String, Header> getAuthenticatedHeader = encodedCredStr -> {
        if (encodedCredStr == null) {
            throw new IllegalArgumentException("[ERR] encodedCredStr can't be null");
        }
        return new Header("Authorization", "Basic " + encodedCredStr);
    };
}

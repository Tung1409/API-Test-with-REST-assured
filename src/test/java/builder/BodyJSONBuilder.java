package builder;

import com.google.gson.Gson;

public class BodyJSONBuilder {

    public static <T> String getJSONContent(T dataObject){
        return new Gson().toJson(dataObject);
    }
}

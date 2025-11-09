package ru.yandex.stellarburgers.utils;

import io.restassured.response.Response;
import ru.yandex.stellarburgers.api.ApiConfig;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;

public class IngredientHelper {

    public static List<String> getValidIngredients() {
        Response response = given()
                .baseUri(ApiConfig.BASE_URI)
                .basePath(ApiConfig.API_PATH)
                .when()
                .get(ApiConfig.INGREDIENTS);

        return response.jsonPath().getList("data._id", String.class);
    }

    public static List<String> getInvalidIngredients() {
        return Arrays.asList("invalid_hash_1", "invalid_hash_2");
    }
}

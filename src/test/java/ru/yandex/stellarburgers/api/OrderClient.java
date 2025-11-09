package ru.yandex.stellarburgers.api;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.yandex.stellarburgers.model.Order;

import static io.restassured.RestAssured.given;

public class OrderClient extends BaseClient {

    @Step("Создание заказа с авторизацией")
    public Response createOrderWithAuth(Order order, String accessToken) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", accessToken)
                .body(order)
                .when()
                .post(ApiConfig.ORDERS);
    }

    @Step("Создание заказа без авторизации")
    public Response createOrderWithoutAuth(Order order) {
        return given()
                .spec(getBaseSpec())
                .body(order)
                .when()
                .post(ApiConfig.ORDERS);
    }

    @Step("Получение заказов пользователя с авторизацией")
    public Response getUserOrdersWithAuth(String accessToken) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", accessToken)
                .when()
                .get(ApiConfig.ORDERS);
    }

    @Step("Получение заказов пользователя без авторизации")
    public Response getUserOrdersWithoutAuth() {
        return given()
                .spec(getBaseSpec())
                .when()
                .get(ApiConfig.ORDERS);
    }
}

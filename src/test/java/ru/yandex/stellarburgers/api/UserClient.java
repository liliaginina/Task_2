package ru.yandex.stellarburgers.api;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.yandex.stellarburgers.model.User;

import static io.restassured.RestAssured.given;

public class UserClient extends BaseClient {

    @Step("Создание пользователя")
    public Response createUser(User user) {
        return given()
                .spec(getBaseSpec())
                .body(user)
                .when()
                .post(ApiConfig.USER_REGISTER);
    }

    @Step("Логин пользователя")
    public Response loginUser(User user) {
        return given()
                .spec(getBaseSpec())
                .body(user)
                .when()
                .post(ApiConfig.USER_LOGIN);
    }

    @Step("Обновление данных пользователя")
    public Response updateUser(User user, String accessToken) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", accessToken)
                .body(user)
                .when()
                .patch(ApiConfig.USER_INFO);
    }

    @Step("Обновление данных пользователя без авторизации")
    public Response updateUserWithoutAuth(User user) {
        return given()
                .spec(getBaseSpec())
                .body(user)
                .when()
                .patch(ApiConfig.USER_INFO);
    }

    @Step("Удаление пользователя")
    public Response deleteUser(String accessToken) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", accessToken)
                .when()
                .delete(ApiConfig.USER_INFO);
    }
}

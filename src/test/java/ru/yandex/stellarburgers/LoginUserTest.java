package ru.yandex.stellarburgers;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.stellarburgers.api.UserClient;
import ru.yandex.stellarburgers.model.User;
import ru.yandex.stellarburgers.utils.UserGenerator;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class LoginUserTest {
    private UserClient userClient;
    private String accessToken;
    private User user;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = UserGenerator.generateRandomUser();

        // Создаём пользователя для тестов
        Response createResponse = userClient.createUser(user);
        accessToken = createResponse.jsonPath().getString("accessToken");
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Логин под существующим пользователем")
    @Description("Проверка успешного логина с правильными учётными данными")
    public void testLoginExistingUser() {
        Response response = userClient.loginUser(user);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail().toLowerCase()))
                .body("user.name", equalTo(user.getName()))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());

        String loginAccessToken = response.jsonPath().getString("accessToken");
        assertNotNull("Access token should not be null", loginAccessToken);
    }

    @Test
    @DisplayName("Логин с неверным email")
    @Description("Проверка что нельзя войти с неверным email")
    public void testLoginWithWrongEmail() {
        User wrongUser = new User("wrong_" + user.getEmail(), user.getPassword(), user.getName());

        Response response = userClient.loginUser(wrongUser);

        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Логин с неверным паролем")
    @Description("Проверка что нельзя войти с неверным паролем")
    public void testLoginWithWrongPassword() {
        User wrongUser = new User(user.getEmail(), "wrongPassword123", user.getName());

        Response response = userClient.loginUser(wrongUser);

        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}

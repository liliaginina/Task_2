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

public class CreateUserTest {
    private UserClient userClient;
    private String accessToken;

    @Before
    public void setUp() {
        userClient = new UserClient();
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Создание уникального пользователя")
    @Description("Проверка успешного создания уникального пользователя")
    public void testCreateUniqueUser() {
        User user = UserGenerator.generateRandomUser();

        Response response = userClient.createUser(user);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail().toLowerCase()))
                .body("user.name", equalTo(user.getName()))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());

        accessToken = response.jsonPath().getString("accessToken");
        assertNotNull("Access token should not be null", accessToken);
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован")
    @Description("Проверка что нельзя создать пользователя с существующим email")
    public void testCreateDuplicateUser() {
        User user = UserGenerator.generateRandomUser();

        // Создаём пользователя первый раз
        Response firstResponse = userClient.createUser(user);
        accessToken = firstResponse.jsonPath().getString("accessToken");

        firstResponse.then()
                .statusCode(200)
                .body("success", equalTo(true));

        // Пытаемся создать пользователя с тем же email
        Response secondResponse = userClient.createUser(user);

        secondResponse.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя без обязательного поля email")
    @Description("Проверка что нельзя создать пользователя без email")
    public void testCreateUserWithoutEmail() {
        User user = UserGenerator.createUserWithoutEmail();

        Response response = userClient.createUser(user);

        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без обязательного поля password")
    @Description("Проверка что нельзя создать пользователя без password")
    public void testCreateUserWithoutPassword() {
        User user = UserGenerator.createUserWithoutPassword();

        Response response = userClient.createUser(user);

        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без обязательного поля name")
    @Description("Проверка что нельзя создать пользователя без name")
    public void testCreateUserWithoutName() {
        User user = UserGenerator.createUserWithoutName();

        Response response = userClient.createUser(user);

        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}

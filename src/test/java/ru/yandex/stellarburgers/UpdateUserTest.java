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

public class UpdateUserTest {
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
    @DisplayName("Изменение email с авторизацией")
    @Description("Проверка что можно изменить email авторизованного пользователя")
    public void testUpdateEmailWithAuth() {
        User updatedUser = new User();
        updatedUser.setEmail("updated_" + user.getEmail());

        Response response = userClient.updateUser(updatedUser, accessToken);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(updatedUser.getEmail().toLowerCase()));
    }

    @Test
    @DisplayName("Изменение имени с авторизацией")
    @Description("Проверка что можно изменить имя авторизованного пользователя")
    public void testUpdateNameWithAuth() {
        User updatedUser = new User();
        updatedUser.setName("UpdatedName");

        Response response = userClient.updateUser(updatedUser, accessToken);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.name", equalTo(updatedUser.getName()));
    }

    @Test
    @DisplayName("Изменение пароля с авторизацией")
    @Description("Проверка что можно изменить пароль авторизованного пользователя")
    public void testUpdatePasswordWithAuth() {
        User updatedUser = new User();
        updatedUser.setPassword("newPassword123");

        Response response = userClient.updateUser(updatedUser, accessToken);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Изменение email без авторизации")
    @Description("Проверка что нельзя изменить email без авторизации")
    public void testUpdateEmailWithoutAuth() {
        User updatedUser = new User();
        updatedUser.setEmail("updated_" + user.getEmail());

        Response response = userClient.updateUserWithoutAuth(updatedUser);

        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Изменение имени без авторизации")
    @Description("Проверка что нельзя изменить имя без авторизации")
    public void testUpdateNameWithoutAuth() {
        User updatedUser = new User();
        updatedUser.setName("UpdatedName");

        Response response = userClient.updateUserWithoutAuth(updatedUser);

        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Изменение пароля без авторизации")
    @Description("Проверка что нельзя изменить пароль без авторизации")
    public void testUpdatePasswordWithoutAuth() {
        User updatedUser = new User();
        updatedUser.setPassword("newPassword123");

        Response response = userClient.updateUserWithoutAuth(updatedUser);

        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}

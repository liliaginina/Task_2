package ru.yandex.stellarburgers;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.stellarburgers.api.OrderClient;
import ru.yandex.stellarburgers.api.UserClient;
import ru.yandex.stellarburgers.model.Order;
import ru.yandex.stellarburgers.model.User;
import ru.yandex.stellarburgers.utils.IngredientHelper;
import ru.yandex.stellarburgers.utils.UserGenerator;

import java.util.List;

import static org.hamcrest.Matchers.*;

public class GetUserOrdersTest {
    private OrderClient orderClient;
    private UserClient userClient;
    private String accessToken;
    private List<String> validIngredients;

    @Before
    public void setUp() {
        orderClient = new OrderClient();
        userClient = new UserClient();

        // Создаём пользователя для тестов
        User user = UserGenerator.generateRandomUser();
        Response createResponse = userClient.createUser(user);
        accessToken = createResponse.jsonPath().getString("accessToken");

        // Получаем валидные ингредиенты
        validIngredients = IngredientHelper.getValidIngredients();

        // Создаём заказ для пользователя
        Order order = new Order(validIngredients.subList(0, 2));
        orderClient.createOrderWithAuth(order, accessToken);
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Получение заказов авторизованного пользователя")
    @Description("Проверка что авторизованный пользователь может получить свои заказы")
    public void testGetUserOrdersWithAuth() {
        Response response = orderClient.getUserOrdersWithAuth(accessToken);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("orders", notNullValue())
                .body("orders", not(empty()))
                .body("total", notNullValue())
                .body("totalToday", notNullValue());
    }

    @Test
    @DisplayName("Получение заказов неавторизованного пользователя")
    @Description("Проверка что неавторизованный пользователь не может получить заказы")
    public void testGetUserOrdersWithoutAuth() {
        Response response = orderClient.getUserOrdersWithoutAuth();

        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}

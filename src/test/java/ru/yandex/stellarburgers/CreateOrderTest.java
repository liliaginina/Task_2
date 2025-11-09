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

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;

public class CreateOrderTest {
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
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и ингредиентами")
    @Description("Проверка успешного создания заказа авторизованным пользователем с ингредиентами")
    public void testCreateOrderWithAuthAndIngredients() {
        Order order = new Order(validIngredients.subList(0, 2));

        Response response = orderClient.createOrderWithAuth(order, accessToken);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("order.number", notNullValue())
                .body("order.ingredients", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без авторизации с ингредиентами")
    @Description("Проверка создания заказа без авторизации с ингредиентами")
    public void testCreateOrderWithoutAuthWithIngredients() {
        Order order = new Order(validIngredients.subList(0, 2));

        Response response = orderClient.createOrderWithoutAuth(order);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов с авторизацией")
    @Description("Проверка что нельзя создать заказ без ингредиентов")
    public void testCreateOrderWithoutIngredients() {
        Order order = new Order(new ArrayList<>());

        Response response = orderClient.createOrderWithAuth(order, accessToken);

        response.then()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    @Description("Проверка что нельзя создать заказ с невалидными ингредиентами")
    public void testCreateOrderWithInvalidIngredients() {
        Order order = new Order(IngredientHelper.getInvalidIngredients());

        Response response = orderClient.createOrderWithAuth(order, accessToken);

        response.then()
                .statusCode(500);
    }
}

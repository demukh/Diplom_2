import user.User;
import user.UserClient;
import order.Order;
import order.OrderClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class GetOrderTest {
    private Order order;
    private Response response;
    private final OrderClient orderClient = new OrderClient();
    private final UserClient userClient = new UserClient();
    private String token;

    @Test
    @DisplayName("Получение заказов под авторизованным пользователем")
    public void shouldGetOrdersWithAuth() {
        User user = User.createRandomUser();
        response = userClient.createUser(user);
        token = response.then().extract().body().path("accessToken");
        order = Order.getOrderCorrect();
        response = orderClient.createOrder(order, token);
        response = orderClient.createOrder(order, token);
        response = orderClient.getUserOrders(token);
        userClient.removeUser(token);
        response.then().assertThat().statusCode(200)
                .and().body("orders", notNullValue());
    }

    @Test
    @DisplayName("Получение заказов без авторизации")
    public void getOrdersWithoutAuthShouldBeError() {
        order = Order.getOrderCorrect();
        response = orderClient.createOrder(order, "token");
        response = orderClient.createOrder(order, "token");
        response = orderClient.getUserOrders("token");
        response.then().assertThat().statusCode(401)
                .and().body("message", equalTo("You should be authorised"));
    }
}
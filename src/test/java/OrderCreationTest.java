import user.User;
import user.UserClient;
import order.Order;
import order.OrderClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;
import static org.hamcrest.Matchers.*;

public class OrderCreationTest {
    private Order order;
    private Response response;
    private final OrderClient orderClient = new OrderClient();
    private final UserClient userClient = new UserClient();
    private String token;

    @Test
    @DisplayName("Создание заказа без авторизации")
    public void shouldCreateOrderWithoutAuth() {
        response = orderClient.createOrder(Order.getOrderCorrect(), "token");
        response.then().assertThat().statusCode(200)
                .and().body("success", equalTo(true));
    }

    @Test
    @DisplayName("Создание заказа с авторизацией")
    public void shouldCreateOrderWithAuth() {
        User user = User.createRandomUser();
        response = userClient.createUser(user);
        token = response.then().extract().body().path("accessToken");
        response = orderClient.createOrder(Order.getOrderCorrect(), token);
        userClient.removeUser(token);
        response.then().assertThat().statusCode(200)
                .and().body("success", equalTo(true));
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    public void createOrderWithoutIngrShouldBeError() {
        response = orderClient.createOrder(Order.getOrderEmpty(), "token");
        response.then().assertThat().statusCode(400)
                .and().body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с ингредиентами")
    public void shouldCreateOrderWithIngr() {
        order = Order.getOrderCorrect();
        response = orderClient.createOrder(order, "token");
        response.then().assertThat().statusCode(200)
                .and().body("success", equalTo(true));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем")
    public void createOrderWithWrongIngrHashShouldBeError() {
        order = Order.getOrderWrongHash();
        response = orderClient.createOrder(order, "token");
        response.then().assertThat().statusCode(500);
    }
}
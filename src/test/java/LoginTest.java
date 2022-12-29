import user.User;
import user.UserClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.Matchers.*;

public class LoginTest {
    private User user;
    private Response response;
    private final UserClient userClient = new UserClient();
    private String password, email, token;

    @Before
    public void setup() {
        user = User.createRandomUser();
        response = userClient.createUser(user);
        token = response.then().extract().body().path("accessToken");
    }
    @Test
    @DisplayName("Вход с существующим аккаунтом")
    public void shouldSuccessLogin() {
        response = userClient.loginUser(user = User.getExistUser());
        response.then().assertThat().body("success", equalTo(true))
                .and().statusCode(200);
    }
    @Test
    @DisplayName("Попытка входа с несуществующим логином")
    public void loginWithInvalidLogin() {
        email = user.getEmail();
        user.setEmail("random@mail.com");
        password = user.getPassword();
        response = userClient.loginUser(user, token);
        user.setEmail(email);
        user.setPassword(password);
        response.then().assertThat().body("success", equalTo(false))
                .and().body("success", equalTo(false));
    }
    @Test
    @DisplayName("Попытка входа с несуществующим паролем")
    public void loginWithInvalidPassword() {
        email = user.getEmail();
        password = user.getPassword();
        user.setPassword("RandomPassword");
        response = userClient.loginUser(user, token);
        user.setEmail(email);
        user.setPassword(password);
        response.then().assertThat().statusCode(401)
                .and().body("success", equalTo(false));
    }
    @After
    public void teardown() {
        userClient.removeUser(token);
    }
}
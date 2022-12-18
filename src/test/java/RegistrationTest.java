
import io.qameta.allure.junit4.DisplayName;
        import io.restassured.response.Response;
        import org.junit.Test;


        import static org.hamcrest.Matchers.*;

public class RegistrationTest {
    private User user;
    private Response response;
    private final UserClient userClient = new UserClient();

    @Test
    @DisplayName("Создание нового аккаунта")
    public void successfullRegistrationTest() {
        user = User.createRandomUser();
        response = userClient.createUser(user);
        String token = response.then().extract().body().path("accessToken");
        userClient.removeUser(token);
        response.then().assertThat().body("accessToken", notNullValue())
                .and().statusCode(200);
    }

    @Test
    @DisplayName("Попытка создания уже существующего аккаунта")
    public void registrationExistingAccountShouldBeErrorTest() {
        user = User.getExistUser();
        response = userClient.createUser(user);
        response.then().assertThat().body("message", equalTo("User already exists"))
                .and().statusCode(403);
    }

    @Test
    @DisplayName("Попытка создания акккаунта без имени")
    public void registrationWithoutNameShouldBeErrorTest() {
        user = User.createUserWithoutName();
        response = userClient.createUser(user);
        response.then().assertThat().body("message", equalTo("Email, password and name are required fields"))
                .and().statusCode(403);
    }

    @Test
    @DisplayName("Попытка создания акккаунта без почты")
    public void registrationWithoutEmailShouldBeErrorTest() {
        user = User.createUserWithoutEmail();
        response = userClient.createUser(user);
        response.then().assertThat().body("message", equalTo("Email, password and name are required fields"))
                .and().statusCode(403);
    }

    @Test
    @DisplayName("Попытка создания акккаунта без пароля")
    public void registrationWithoutPasswordShouldBeErrorTest() {
        user = User.createUserWithoutPassword();
        response = userClient.createUser(user);
        response.then().assertThat().body("message", equalTo("Email, password and name are required fields"))
                .and().statusCode(403);
    }
}
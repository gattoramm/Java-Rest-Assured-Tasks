package api;

import io.restassured.http.ContentType;
import static io.restassured.RestAssured.given;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class ReqestTest {
    private static final String URL = "https://reqres.in/";

    @Test
    public void checkAvatarAndIdTest() {
        List<UserData> users = given()
                .when()
                .contentType(ContentType.JSON)
                .get(URL + "api/users?page=2")
                .then()
                .log()
                .all()
                .extract()
                .body()
                .jsonPath()
                .getList("data", UserData.class);

        users.forEach(x -> Assert.assertTrue(x.getAvatar().contains(x.getId().toString())));
    }
}

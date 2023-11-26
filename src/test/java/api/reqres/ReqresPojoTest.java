package api.reqres;

import static io.restassured.RestAssured.given;

import api.reqres.colors.ColorsData;
import api.reqres.registration.Register;
import api.reqres.registration.SuccessUserReg;
import api.reqres.registration.UnSuccessUserReg;
import api.reqres.spec.Specification;
import api.reqres.users.UserData;
import api.reqres.users.UserTime;
import api.reqres.users.UserTimeResponse;
import org.junit.Assert;
import org.junit.Test;

import java.time.Clock;
import java.util.List;
import java.util.stream.Collectors;

public class ReqresPojoTest {
    private static final String URL = "https://reqres.in/";

    @Test
    public void checkAvatarAndIdTest() {
        Specification.installSpecification(Specification.requestSpec(URL), Specification.responseSpecUnique(200));
        List<UserData> users = given()
                .when()
                .get("api/users?page=2")
                .then()
                .log()
                .all()
                .extract()
                .body()
                .jsonPath()
                .getList("data", UserData.class);

        users.forEach(x -> Assert.assertTrue(x.getAvatar().contains(x.getId().toString())));

        Assert.assertTrue(users.stream().allMatch(x -> x.getEmail().endsWith("@reqres.in")));

        List<String> avatars = users.stream().map(UserData::getAvatar).collect(Collectors.toList());
        List<String> ids = users.stream().map(x -> x.getId().toString()).collect(Collectors.toList());

        for (int i = 0; i < avatars.size(); i++) {
            Assert.assertTrue(avatars.get(i).contains(ids.get(i)));
        }
    }

    @Test
    public void successReqTest() {
        Specification.installSpecification(Specification.requestSpec(URL), Specification.responseSpecUnique(200));
        Integer id = 4;
        String token = "QpwL5tke4Pnpja7X4";
        Register user = new Register("eve.holt@reqres.in", "pistol");

        SuccessUserReg successUserReg = given()
                .body(user)
                .when()
                .post("api/register")
                .then()
                .log()
                .all()
                .extract()
                .as(SuccessUserReg.class);
        Assert.assertNotNull(successUserReg.getId());
        Assert.assertNotNull(successUserReg.getToken());
        Assert.assertEquals(id, successUserReg.getId());
        Assert.assertEquals(token, successUserReg.getToken());
    }

    @Test
    public void unSuccessReqTest() {
        Specification.installSpecification(Specification.requestSpec(URL), Specification.responseSpecUnique(400));
        Register user = new Register("sydney@fife", "");

        UnSuccessUserReg unSuccessUserReg = given()
                .body(user)
                .post("api/register")
                .then()
                .log()
                .all()
                .extract()
                .as(UnSuccessUserReg.class);
        Assert.assertEquals("Missing password", unSuccessUserReg.getError());
    }

    @Test
    public void sortedYearsTest() {
        Specification.installSpecification(Specification.requestSpec(URL), Specification.responseSpecUnique(200));
        List<ColorsData> colors = given()
                .when()
                .get("api/unkwoun")
                .then()
                .log()
                .all()
                .extract()
                .body()
                .jsonPath()
                .getList("data", ColorsData.class);

        List<Integer> years = colors
                .stream()
                .map(ColorsData::getYear)
                .collect(Collectors.toList());

        List<Integer> sortedYears = years
                .stream()
                .sorted()
                .collect(Collectors.toList());

        Assert.assertEquals(sortedYears, years);
    }

    @Test
    public void deleteUserTest() {
        Specification.installSpecification(Specification.requestSpec(URL), Specification.responseSpecUnique(204));
        given()
                .when()
                .delete("api/users/2")
                .then()
                .log()
                .all();
    }

    @Test
    public void timeTest() {
        Specification.installSpecification(Specification.requestSpec(URL), Specification.responseSpecUnique(200));
        UserTime user = new UserTime("morpheus", "zion resident");
        UserTimeResponse response = given()
                .body(user)
                .when()
                .put("api/users/2")
                .then()
                .log().all()
                .extract()
                .as(UserTimeResponse.class);
        String regex = "(.{5})$";
        String currentTime = Clock.systemUTC().instant().toString().replaceAll(regex, "");

        Assert.assertEquals(currentTime, response.getUpdatedAt().replaceAll(regex, ""));
    }
}

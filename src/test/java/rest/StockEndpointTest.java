package rest;

import com.google.gson.JsonObject;
import entities.Currency;
import entities.User;
import entities.Role;
import entities.Stock;
import entities.Transaction;
import facades.StockFacade;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import java.net.URI;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;

//Disabled
public class StockEndpointTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;
    private static StockFacade stockFacade;
    private static User user,admin,both;
    private static Role userRole,adminRole;
    private static Stock s1,s2,s3,s4;
    private static Currency c1,c2,c3,c4;
    private static Transaction t1,t2,t3,t4;
    
    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    public static void setUpClass() {
        //This method must be called before you request the EntityManagerFactory
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        stockFacade = StockFacade.getStockFacade(emf);

        httpServer = startServer();
        //Setup RestAssured
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;
        
    }

    @AfterAll
    public static void closeTestServer() {
        //Don't forget this, if you called its counterpart in @BeforeAll
        EMF_Creator.endREST_TestWithDB();
        
        httpServer.shutdownNow();
    }

    // Setup the DataBase (used by the test-server and this test) in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the EntityClass used below to use YOUR OWN (renamed) Entity class
    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNamedQuery("Transaction.deleteAllRows").executeUpdate();
            em.createNamedQuery("Stock.deleteAllRows").executeUpdate();
            em.createNamedQuery("User.deleteAllRows").executeUpdate();
            em.createNamedQuery("Currency.deleteAllRows").executeUpdate();
            em.createNamedQuery("Role.deleteAllRows").executeUpdate();
            
            user = new User("user", "testUser");
            admin = new User("admin", "testAdmin");
            both = new User("user_admin", "testBoth");
            
            userRole = new Role("user");
            adminRole = new Role("admin");
            
            s1 = new Stock("s1","test1 INC.");
            s2 = new Stock("s2","test2 INC.");
            s3 = new Stock("s3","test3 INC.");
            s4 = new Stock("s4","test4 INC.");
         
            c1 = new Currency("code_c1","name_c1");
            c2 = new Currency("code_c2","name_c2");
            c3 = new Currency("code_c3","name_c3");
            c4 = new Currency("code_c4","name_c4");
            
            t1 = new Transaction(s1,100,c1,1000.1);
            t2 = new Transaction(s2,200,c2,2000.2);
            t3 = new Transaction(s3,300,c3,3000.3);
            t4 = new Transaction(s4,400,c4,4000.4);
 
            user.addRole(userRole);
            admin.addRole(adminRole);
            both.addRole(userRole);
            both.addRole(adminRole);
            
            user.setCurrencyCode(c1);
            both.setCurrencyCode(c2);
            
            user.addTransaction(t1);
            user.addTransaction(t2);
            both.addTransaction(t3);
            both.addTransaction(t4);
           
            em.persist(userRole);
            em.persist(adminRole);
          
            em.persist(user);
            em.persist(admin);
            em.persist(both);
            
            em.persist(c1);
            em.persist(c2);
            em.persist(c3);
            em.persist(c4);
            
            em.persist(s1);
            em.persist(s2);
            em.persist(s3);
            em.persist(s4);
            
            em.persist(t1);
            em.persist(t2);
            em.persist(t3);
            em.persist(t4);
            em.getTransaction().commit();
            
        } finally {
            em.close();
        }
    }

    //This is how we hold on to the token after login, similar to that a client must store the token somewhere
    private static String securityToken;


    //Utility method to login and set the returned securityToken
    private static void login(String username, String password) {
        String json = String.format("{username: \"%s\", password: \"%s\"}", username, password);
        securityToken = given()
                .contentType("application/json")
                .body(json)
                //.when().post("/api/login")
                .when().post("/login")
                .then()
                .extract().path("token");
        //System.out.println("TOKEN ---> " + securityToken);
    }

    private void logOut() {
        securityToken = null;
    }
    
    @Test
    public void testAddTransaction() {
        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("units", 100);
        jsonBody.addProperty("boughtPrice", 10.10);
        jsonBody.addProperty("stockSymbol", "AAPL");
        jsonBody.addProperty("currencyCode", c4.getCode());
        
        login(user.getUserName(),"testUser");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .body(jsonBody.toString())
                .when().post("/stock")
                .then()
                .body("id", greaterThan(0));
    }
    
    @Test
    public void testAddTransactionWithWrongCurrency() {
        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("units", 100);
        jsonBody.addProperty("boughtPrice", 10.10);
        jsonBody.addProperty("stockSymbol", "AAPL");
        jsonBody.addProperty("currencyCode", "fail");

        login(user.getUserName(), "testUser");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .body(jsonBody.toString())
                .when().post("/stock")
                .then()
                .statusCode(400)
                .body("message", equalTo("Currency not found"));
    }
    
    @Test
    public void testAddTransactionWithWrongSymbol() {
        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("units", 100);
        jsonBody.addProperty("boughtPrice", 10.10);
        jsonBody.addProperty("stockSymbol", "failSymbol");
        jsonBody.addProperty("currencyCode", c4.getCode());

        login(user.getUserName(), "testUser");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .body(jsonBody.toString())
                .when().post("/stock")
                .then()
                .statusCode(400)
                .body("message", equalTo("Stock symbol not found"));
    }
    
    //@Test
    public void testGetUser() {
        login(user.getUserName(), "testUser");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when().get("/stock")
                .then()
                .statusCode(200)
                .body(equalTo("user")); 
    }
}

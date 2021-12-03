package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dtos.stock.GroupDTO;
import entities.Currency;
import entities.Group;
import entities.PortfolioValue;
import entities.User;
import entities.Role;
import entities.Stock;
import entities.Transaction;
import facades.StockFacade;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.parsing.Parser;
import java.net.URI;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
    private static PortfolioValue pfv1, pfv2, pfv3, pfv4;
     private static Group g1,g2,g3,g4;
    
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
            em.createNamedQuery("Group.deleteAllRows").executeUpdate();
            em.createNamedQuery("Stock.deleteAllRows").executeUpdate();
            em.createNamedQuery("PortfolioValue.deleteAllRows").executeUpdate();
            em.createNamedQuery("User.deleteAllRows").executeUpdate();
            em.createNamedQuery("Currency.deleteAllRows").executeUpdate();
            em.createNamedQuery("Role.deleteAllRows").executeUpdate();
            
            user = new User("user", "testUser");
            admin = new User("admin", "testAdmin");
            both = new User("user_admin", "testBoth");
            
            userRole = new Role("user");
            adminRole = new Role("admin");
            
            c1 = new Currency("nok", "name_c1",0.0);
            c2 = new Currency("dkk", "name_c2",0.0);
            c3 = new Currency("usd", "name_c3",0.0);
            c4 = new Currency("code_c4", "name_c4",0.0);
            
            s1 = new Stock("s1","test1 INC.",c1,1000.0);
            s2 = new Stock("s2","test2 INC.",c2,2000.0);
            s3 = new Stock("s3","test3 INC.",c3,3000.0);
            s4 = new Stock("s4","test4 INC.",c4, 4000.0);
            
            
            g1 = new Group("Group1");
            g2 = new Group("Group2");
            g3 = new Group("Group3");
            g4 = new Group("Group4");
         
            
            
            t1 = new Transaction(s1,100,1000.1);
            t2 = new Transaction(s2,200,2000.2);
            t3 = new Transaction(s3,300,3000.3);
            t4 = new Transaction(s4,400,4000.4);
            
            t1 = new Transaction(s1, 100, 1000.1);
            t2 = new Transaction(s2, 200, 2000.2);
            t3 = new Transaction(s3, 300, 3000.3);
            t4 = new Transaction(s4, 400, 4000.4);

            pfv1 = new PortfolioValue(10000.0);
            pfv2 = new PortfolioValue(10000.0);
            pfv3 = new PortfolioValue(10000.0);
            pfv4 = new PortfolioValue(10000.0);
            //new date Hacked
            Date date1 = new Date(System.currentTimeMillis() - 4 * 24 * 60 * 60 * 1000);
            Date date2 = new Date(System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000);
            Date date3 = new Date(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000);
            Date date4 = new Date(System.currentTimeMillis() - 1 * 24 * 60 * 60 * 1000);
            pfv1.setDate(date1);
            pfv2.setDate(date2);
            pfv3.setDate(date3);
            pfv4.setDate(date4);
            
            user.addHistoricalPortfolioValue(pfv1);
            user.addHistoricalPortfolioValue(pfv2);
            user.addHistoricalPortfolioValue(pfv3);
            user.addHistoricalPortfolioValue(pfv4);
 
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
            
            
            user.addGroup(g1);
            user.addGroup(g2);
            user.addGroup(g3);
            both.addGroup(g4);
            
            g1.addTransaction(t1);
            g1.addTransaction(t2);
            g3.addTransaction(t1);
            g3.addTransaction(t2);
           
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
            
            em.persist(pfv1);
            em.persist(pfv2);
            em.persist(pfv3);
            em.persist(pfv4);
            
            
            em.persist(g1);
            em.persist(g2);
            em.persist(g3);
            em.persist(g4);
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
    public void testAddTransactionWithWrongSymbol() {
        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("units", 100);
        jsonBody.addProperty("boughtPrice", 10.10);
        jsonBody.addProperty("stockSymbol", "failSymbol");

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
    
    @Test
    public void testGetUser_username() {
        login(user.getUserName(), "testUser");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when().get("/stock")
                .then()
                .statusCode(200)
                .body("username", equalTo(user.getUserName())); 
    }
    
    @Test
    public void testGetUser_transactionsSize() {
        login(user.getUserName(), "testUser");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when().get("/stock")
                .then()
                .statusCode(200)
                .body("transactions", hasSize(2)); 
    }
    
    @Test
    public void testGetUser_totalPortFolioValue() {
        login(user.getUserName(), "testUser");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when().get("/stock")
                .then()
                .statusCode(200)
                .body("totalPortfolioValue", equalTo(500000.0f)); //f is added as json returns a float and NOT a double
    }
    
    @Test
    public void testGetUser_totalHistoricalPortFolioValue() {
        login(user.getUserName(), "testUser");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when().get("/stock")
                .then()
                .statusCode(200)
                .body("historicalPortFolioValue", hasSize(4)); 
    }
    
    //@Test
    public void testGetUser_news() {
        login(user.getUserName(), "testUser");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when().get("/stock")
                .then()
                .statusCode(200)
                .body("news", hasSize(4));
    }
    
    //@Test
    public void testGetUser_groups() {
        login(user.getUserName(), "testUser");
        List<GroupDTO> groups = given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when().get("/stock")
                .then()
                .statusCode(200)
                .extract().body().jsonPath().getList("groups", GroupDTO.class);
        assertThat(groups, hasItems(
            new GroupDTO(g1),
            new GroupDTO(g2)
        ));
    }
    
    @Test
    public void testAddGroup() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject inputJson = new JsonObject();
        inputJson.addProperty("name", "new_group");
        JsonArray jsonArray = new JsonArray();
        jsonArray.add(t1.getId());
        jsonArray.add(t2.getId());
        inputJson.add("transactionIds", jsonArray);
        login(user.getUserName(), "testUser");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .body(inputJson.toString())
                .when().post("/stock/group")
                .then()
                .body("id", is(greaterThan(0)));
    }
    
    @Test
    public void testDeleteGroup() {
        login(user.getUserName(), "testUser");
        given()
                .contentType("application/json")
                .pathParam("id", g1.getId())
                .header("x-access-token", securityToken)
                .when()
                .delete("/stock/group/{id}")
                .then()
                .statusCode(200)
                .body("msg", equalTo("Succesfully deleted group with ID: "+g1.getId()));
    }
}

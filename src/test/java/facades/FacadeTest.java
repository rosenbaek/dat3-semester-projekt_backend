package facades;


import entities.Currency;
import utils.EMF_Creator;
import entities.Role;
import entities.Stock;
import entities.Transaction;
import entities.User;
import errorhandling.API_Exception;
import java.io.IOException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

//Uncomment the line below, to temporarily disable this test
//@Disabled
public class FacadeTest {

    private static EntityManagerFactory emf;
    private static StockFacade stockFacade;
    private static User user,admin,both;
    private static Role userRole,adminRole;
    private static Stock s1,s2,s3,s4;
    private static Currency c1,c2,c3,c4;
    private static Transaction t1,t2,t3,t4;
    

    public FacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
       emf = EMF_Creator.createEntityManagerFactoryForTest();
       stockFacade = StockFacade.getStockFacade(emf);
    }

    
    // Setup the DataBase in a known state BEFORE EACH TEST
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
            
            s1 = new Stock("AAPL","Apple Inc.");
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


    // TODO: Delete or change this method 
    @Test
    public void testTrue() throws Exception {
        EntityManager em = emf.createEntityManager();
        User _both; 
        try {
            _both = em.find(User.class, both.getUserName());
        
        } finally{
            em.close();
        }
        
        
        assertEquals(_both.getUserName(), both.getUserName());
    }
    
    @Test
    public void testAddTransaction() {
        Transaction newTransaction = new Transaction(s1,111,c1,111.1);
        Transaction newTrans = stockFacade.addTransaction(newTransaction, user.getUserName());
        Assertions.assertNotNull(newTrans.getId());
    }
    
    
    @Test
    public void testGetCurrencyFromDatabase() throws API_Exception {
        Currency currency = stockFacade.getCurrencyFromDatabase(c1.getCode());
        assertEquals(currency.getCode(), c1.getCode());
    }
    
    @Test
    public void testGetCurrencyFromDatabaseWrongCurrency() {
        API_Exception error = Assertions.assertThrows(API_Exception.class, () -> {
            stockFacade.getCurrencyFromDatabase("fail");
        });
        assertEquals("Currency not found", error.getMessage());
    }
    
    @Test
    public void testGetStockFromApi() throws IOException, API_Exception {
        EntityManager em = emf.createEntityManager();
        Stock stock = stockFacade.getStockFromApi("NAS.OL");
        Stock stockFromDB = em.find(Stock.class, "NAS.OL");
        assertEquals(stock.getSymbol(), stockFromDB.getSymbol());
    }
    
    @Test
    public void testGetStockFromApiWrongSymbol() {
        API_Exception error = Assertions.assertThrows(API_Exception.class, () -> {
            stockFacade.getStockFromApi("Fail_symbol");
        });
        assertEquals("Stock symbol not found", error.getMessage());
    }
    

}

package facades;


import entities.Currency;
import utils.EMF_Creator;
import entities.Role;
import entities.Stock;
import entities.Transaction;
import entities.User;
import errorhandling.API_Exception;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.Utility;

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
            
            c1 = new Currency("nok", "name_c1",1.0);
            c2 = new Currency("dkk", "name_c2",1.0);
            c3 = new Currency("usd", "name_c3",0.0);
            c4 = new Currency("code_c4", "name_c4",1.0);
            
            s1 = new Stock("AAPL","Apple Inc.",c1,1000.0);
            s2 = new Stock("s2","test2 INC.",c2,2000.0);
            s3 = new Stock("s3","test3 INC.",c3,3000.0);
            s4 = new Stock("s4","test4 INC.",c4,4000.0);
         
            
            
            t1 = new Transaction(s1,100,1000.1);
            t2 = new Transaction(s2,200,2000.2);
            t3 = new Transaction(s3,300,3000.3);
            t4 = new Transaction(s4,400,4000.4);
 
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
        Transaction newTransaction = new Transaction(s1,111,111.1);
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
    public void testGetStockFromApi_correctSymbol() throws IOException, API_Exception {
        EntityManager em = emf.createEntityManager();
        List<String> symbols = new ArrayList<>();
        symbols.add("NAS.OL");
        List<Stock> stocks = stockFacade.getStockFromApi(symbols);
        Stock stockFromDB = em.find(Stock.class, "NAS.OL");
        assertEquals(stocks.get(0).getSymbol(), stockFromDB.getSymbol());
    }
    
    @Test
    public void testGetStockFromApi_currentPrice() throws IOException, API_Exception {
        List<String> symbols = new ArrayList<>();
        symbols.add("NAS.OL");
        List<Stock> stocks = stockFacade.getStockFromApi(symbols);
        Assertions.assertNotNull(stocks.get(0).getCurrentPrice());     
    }
    
    @Test
    public void testGetStockFromApiWrongSymbol() {
        List<String> symbols = new ArrayList<>();
        symbols.add("FAIL_SYMBOL");
        API_Exception error = Assertions.assertThrows(API_Exception.class, () -> {
            stockFacade.getStockFromApi(symbols);
        });
        assertEquals("Stock symbol not found", error.getMessage());
    }
    
    @Test
    public void testGetUser_username() throws IOException, API_Exception {
        User userFromDB = stockFacade.getUserData(user.getUserName());
        assertEquals(userFromDB.getUserName(), user.getUserName());     
    }
    
    @Test
    public void testGetUser_transactionsSize() throws IOException, API_Exception {
        User userFromDB = stockFacade.getUserData(user.getUserName());
        assertEquals(userFromDB.getTransactionList().size(), user.getTransactionList().size());     
    }
    
    @Test
    public void User_totalPortFolioValue() throws IOException, API_Exception {
        Double expected = (t1.getUnits()*s1.getCurrentPrice())+(t2.getUnits()*s2.getCurrentPrice());
        Double actual = Utility.calcTotalPortFolioValue(user);
        assertEquals(expected, actual);
    }
    
    

}

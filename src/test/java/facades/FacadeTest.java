package facades;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dtos.stock.GroupDTO;
import dtos.stock.NewsDTO;
import dtos.stock.ResultDTO;
import entities.Currency;
import entities.Group;
import entities.PortfolioValue;
import utils.EMF_Creator;
import entities.Role;
import entities.Stock;
import entities.Transaction;
import entities.User;
import errorhandling.API_Exception;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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
    private static UserFacade userFacade;
    private static User user,admin,both;
    private static Role userRole,adminRole;
    private static Stock s1,s2,s3,s4;
    private static Currency c1,c2,c3,c4;
    private static Transaction t1,t2,t3,t4;
    private static PortfolioValue pfv1,pfv2,pfv3,pfv4;
    private static Group g1,g2,g3,g4;
    

    public FacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
       emf = EMF_Creator.createEntityManagerFactoryForTest();
       stockFacade = StockFacade.getStockFacade(emf);
       userFacade = UserFacade.getUserFacade(emf);
    }

    
    // Setup the DataBase in a known state BEFORE EACH TEST
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
            
            c1 = new Currency("nok", "name_c1",1.0);
            c2 = new Currency("dkk", "name_c2",1.0);
            c3 = new Currency("usd", "name_c3",0.0);
            c4 = new Currency("code_c4", "name_c4",1.0);
            
            s1 = new Stock("AAPL","Apple Inc.",c1,1000.0);
            s2 = new Stock("s2","test2 INC.",c2,2000.0);
            s3 = new Stock("s3","test3 INC.",c3,3000.0);
            s4 = new Stock("s4","test4 INC.",c4,4000.0);
         
            g1 = new Group("Group1");
            g2 = new Group("Group2");
            g3 = new Group("Group3");
            g4 = new Group("Group4");
            
            t1 = new Transaction(s1,100,1000.1);
            t2 = new Transaction(s2,200,2000.2);
            t3 = new Transaction(s3,300,3000.3);
            t4 = new Transaction(s4,400,4000.4);
            
            pfv1 = new PortfolioValue(10000.0);
            pfv2 = new PortfolioValue(10000.0);
            pfv3 = new PortfolioValue(10000.0);
            pfv4 = new PortfolioValue(10000.0);
            //new date Hacked
            Date date1 = new Date(System.currentTimeMillis()-4*24*60*60*1000);
            Date date2 = new Date(System.currentTimeMillis()-3*24*60*60*1000);
            Date date3 = new Date(System.currentTimeMillis()-2*24*60*60*1000);
            Date date4 = new Date(System.currentTimeMillis()-1*24*60*60*1000);
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
        ResultDTO resultDTO = Utility.calcPortFolio(user.getTransactionList(),user.getCurrencyCode());
        assertEquals(expected, resultDTO.getTotalPortFolioValue());
    }
    
    @Test
    public void testGetAllStockSymbols() {
        int expected = 4;
        List<String> symbols = stockFacade.getAllStockSymbols();
        assertEquals(expected, symbols.size());
        
    }
    
    @Test
    public void testGetAllUserNames() {
        int expected = 3;
        List<String> userNames = stockFacade.getAllUserNames();
        assertEquals(expected, userNames.size());
        
    }
    
    @Test
    public void testAddPortFolioValueHistory() {
        int expected = user.getHistoricalPortfolioValues().size()+1;
        stockFacade.addPortfolioValueHistory(user.getUserName());
        EntityManager em = emf.createEntityManager();
        User newUser = em.find(User.class, user.getUserName());
        assertEquals(expected, newUser.getHistoricalPortfolioValues().size());
    }
    
    
    //@Test
    public void testNewsFromAPI() throws IOException, API_Exception {
        int expected = 4;
        List<NewsDTO> newsDTOs = stockFacade.getNewsFromApi();
        assertEquals(expected, newsDTOs.size());
    }
    
    @Test
    public void testGetGroupsFromUser() {
        EntityManager em = emf.createEntityManager();
        Transaction transaction = em.find(Transaction.class, t1.getId());
        assertEquals(2, transaction.getGroups().size());
        
    }

    @Test
    public void testAddGroup() throws API_Exception {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject inputJson = new JsonObject();
        inputJson.addProperty("name", "new_group");
        JsonArray jsonArray = new JsonArray();
        jsonArray.add(t1.getId());
        jsonArray.add(t2.getId());
        inputJson.add("transactionIds", jsonArray);
        
        GroupDTO groupDTO = gson.fromJson(inputJson, GroupDTO.class);
        
        Group group = stockFacade.addEditGroup(groupDTO, user.getUserName());
        Assertions.assertNotNull(group.getId());
    }
    
    @Test
    public void testEditGroup_transactionIncrease() throws API_Exception {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject inputJson = new JsonObject();
        inputJson.addProperty("name", "new_group_new");
        inputJson.addProperty("id", g1.getId());
        JsonArray jsonArray = new JsonArray();
        jsonArray.add(t1.getId());
        jsonArray.add(t2.getId());
        jsonArray.add(t3.getId());
        
        inputJson.add("transactionIds", jsonArray);

        GroupDTO groupDTO = gson.fromJson(inputJson, GroupDTO.class);
        
        Group group = stockFacade.addEditGroup(groupDTO, user.getUserName());
        
        
        
        assertEquals(g1.getTransactions().size()+1,group.getTransactions().size());
    }
    
    @Test
    public void testEditGroup_groupNameChange() throws API_Exception {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject inputJson = new JsonObject();
        inputJson.addProperty("name", "new_group_new");
        inputJson.addProperty("id", g1.getId());
        JsonArray jsonArray = new JsonArray();
        jsonArray.add(t1.getId());
        jsonArray.add(t2.getId());
        jsonArray.add(t3.getId());
        
        inputJson.add("transactionIds", jsonArray);

        GroupDTO groupDTO = gson.fromJson(inputJson, GroupDTO.class);
        
        Group group = stockFacade.addEditGroup(groupDTO, user.getUserName());
        
        
        
        Assertions.assertNotEquals(g1.getGroupName(),group.getGroupName());
        assertEquals(g1.getId(), group.getId());
    }
    
    @Test
    public void testDeleteGroup() throws API_Exception {
        int deleteId = g1.getId();
        stockFacade.deleteGroup(deleteId,g1.getUser().getUserName());
        EntityManager em = emf.createEntityManager();
        Group deletedGroup = em.find(Group.class,g1.getId());
        Assertions.assertNull(deletedGroup);
    }
    
    @Test
    public void testDeleteGroup_wrongUsername() throws API_Exception {
        int deleteId = g1.getId();
         API_Exception error = Assertions.assertThrows(API_Exception.class, () -> {
            stockFacade.deleteGroup(deleteId,admin.getUserName());
        });
        assertEquals("You can only delete your own groups", error.getMessage());
    }
    
    @Test
    public void testRemoveTransactions() throws API_Exception {
        EntityManager em = emf.createEntityManager();
        List<Integer> transactionIds = new ArrayList<>();
        transactionIds.add(t1.getId());
        stockFacade.removeTransactions(transactionIds, user.getUserName());
        User newUser = em.find(User.class, user.getUserName());
        assertEquals(user.getTransactionList().size() - transactionIds.size(), newUser.getTransactionList().size());
    }
    
    @Test
    public void testRemoveTransactions_Multiple() throws API_Exception {
        EntityManager em = emf.createEntityManager();
        List<Integer> transactionIds = new ArrayList<>();
        transactionIds.add(t1.getId());
        transactionIds.add(t2.getId());
        stockFacade.removeTransactions(transactionIds, user.getUserName());
        User newUser = em.find(User.class, user.getUserName());
        assertEquals(user.getTransactionList().size() - transactionIds.size(), newUser.getTransactionList().size());
    }
    
    @Test
    public void testRemoveTransactions_WrongUsername() throws API_Exception {
        List<Integer> transactionIds = new ArrayList<>();
        transactionIds.add(t1.getId());
        transactionIds.add(t4.getId());
        API_Exception error = Assertions.assertThrows(API_Exception.class, () -> {
            stockFacade.removeTransactions(transactionIds, user.getUserName());
        });
        assertEquals("You can only delete your own transactions. Nothing deleted", error.getMessage());
    }
    @Test
    public void testRemoveTransactions_IdNotExist() throws API_Exception {
        List<Integer> transactionIds = new ArrayList<>();
        transactionIds.add(t1.getId());
        transactionIds.add(999);
        API_Exception error = Assertions.assertThrows(API_Exception.class, () -> {
            stockFacade.removeTransactions(transactionIds, user.getUserName());
        });
        assertEquals("Transaction id 999 Caused an error. Nothing deleted.", error.getMessage());
    }
    
    @Test
    public void testEditUser_newCurrencyCode_emptyPass() throws API_Exception {
        User newUser = new User(user.getUserName(),null);
        newUser.setCurrencyCode(new Currency());
        newUser.getCurrencyCode().setCode("dkk");
        User updatedUser = userFacade.editUser(newUser);
        assertEquals("dkk", updatedUser.getCurrencyCode().getCode());
        assertEquals(user.getUserPass(),updatedUser.getUserPass());
    }
    
    @Test
    public void testEditUser_newCurrencyCode_newPass() throws API_Exception {
        User newUser = new User(user.getUserName(),"nytPassword");
        newUser.setCurrencyCode(new Currency());
        newUser.getCurrencyCode().setCode("dkk");
        User updatedUser = userFacade.editUser(newUser);
        assertEquals("dkk", updatedUser.getCurrencyCode().getCode());
        Assertions.assertNotEquals(user.getUserPass(),updatedUser.getUserPass());
    }
    
    @Test
    public void testGetAllCurrencies() {
        int expected = 4;
        int actual = stockFacade.getAllCurrencies().size();
        assertEquals(expected, actual);
    }
}

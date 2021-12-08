/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facades;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dtos.stock.CurrencyDTO;
import dtos.stock.GroupDTO;
import dtos.stock.HistoricalCurrencyDTO;
import dtos.stock.NewsDTO;
import dtos.stock.ResultDTO;
import dtos.stock.StockDTO;
import entities.Currency;
import entities.Group;
import entities.PortfolioValue;
import entities.Stock;
import entities.Transaction;
import entities.User;
import errorhandling.API_Exception;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import utils.Utility;
import utils.api.MakeOptions;

/**
 *
 * @author mikke
 */
public class StockFacade {
    private static EntityManagerFactory emf;
    private static StockFacade instance;
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private StockFacade() {
    }

    
    public User getUserData(String username) throws IOException, API_Exception {
        EntityManager em = emf.createEntityManager();
        Cache cache = em.getEntityManagerFactory().getCache();
        cache.evict(User.class); //Removes any users there is stored in the cache. Cache caused problems when deleting groups
        cache.evict(Group.class);
        User user;
        try {
            user = em.find(User.class, username);
            List<String> symbolsToBeUpdated = new ArrayList<>();
            List<String> currenciesToBeUpdated = new ArrayList<>();
            //loop through transactions
            user.getTransactionList().forEach(t->{
                long MAX_DURATION = MILLISECONDS.convert(5, MINUTES);

                Date now = new Date();
                Date previousStock = t.getStocksSymbol().getLastUpdated();
                Date previousCurrency = t.getStocksSymbol().getCurrency().getLastUpdated();
                long durationStock = now.getTime() - previousStock.getTime();
                long durationCurrency = now.getTime() - previousCurrency.getTime();

                if (durationStock >= MAX_DURATION) {
                    //tilføj symbol til liste
                    symbolsToBeUpdated.add(t.getStocksSymbol().getSymbol());
                }
                //USD will never be updated since it's the base currency in db. therefore we check below
                if (durationCurrency >= MAX_DURATION && !t.getStocksSymbol().getCurrency().getCode().equals("usd") ) {
                    //tilføj Currency til liste
                    currenciesToBeUpdated.add(t.getStocksSymbol().getCurrency().getCode());
                }
            });
            
            //opdater hvis der er nogle symboler der skal opdateres.
            if(symbolsToBeUpdated.size() > 0){
                getStockFromApi(symbolsToBeUpdated);
            }
            if (currenciesToBeUpdated.size() > 0) {
                //Update currencies from api with regard to DKK
                updateCurrenciesFromApi();
            }
            
        } finally {
            em.close();
        }
        return user;
    }
    
    
    public List<String> getAllUserNames(){
        List<String> usernames;
        EntityManager em = emf.createEntityManager();
        try{
            TypedQuery<String> query = em.createQuery("SELECT u.userName from User u", String.class);
            usernames = query.getResultList();
        } finally{
            em.close();
        }
        return usernames;
    }
    
    public void addPortfolioValueHistory(String username) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.find(User.class,username);
            ResultDTO resultDTO = Utility.calcPortFolio(user.getTransactionList(),user.getCurrencyCode());
            PortfolioValue tpfv = new PortfolioValue(resultDTO.getTotalPortFolioValue());
            
            boolean persist = true;
            for (PortfolioValue val : user.getHistoricalPortfolioValues()) {
                if (Utility.isSameDay(val.getDate(), new Date())) {
                   persist = false;
                }
            }
            
            if (persist) {
                user.addHistoricalPortfolioValue(tpfv);
                em.persist(tpfv); 
            }
            
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
    
    public static StockFacade getStockFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new StockFacade();
        }
        return instance;
    }
    
    public Transaction addTransaction(Transaction transaction, String username) {
        EntityManager em = emf.createEntityManager();
        Transaction t = transaction;
        try {
            em.getTransaction().begin();
            User user = em.find(User.class,username);
            user.addTransaction(t);
            em.persist(t);
            em.getTransaction().commit();
            return t;
        } finally {
            em.close();
        }
    }
    
    
    public void removeTransactions(List<Integer> transactionIds, String username) throws API_Exception {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            //Loop ids 
            for (Integer transactionId : transactionIds) {
                //inside loop get transaction from db
                
                Transaction t = em.find(Transaction.class, transactionId);
                if (t == null) {
                    throw new API_Exception("Transaction id "+transactionId+" Caused an error. Nothing deleted.");
                } else if (!(username.equals(t.getUser().getUserName()))) {
                    throw new API_Exception("You can only delete your own transactions. Nothing deleted");
                }
                em.remove(t);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
    
    
    public List<String> getAllStockSymbols(){
        EntityManager em = emf.createEntityManager();
        try{
            List<String> symbols;
            TypedQuery<String> query = em.createQuery("SELECT s.symbol from Stock s",String.class);
            symbols = query.getResultList();
            return symbols;
        } finally{
            em.close();
        }
    }
    
    public void updateCurrenciesFromApi() throws IOException{
        EntityManager em = emf.createEntityManager();
        String URL = "https://freecurrencyapi.net/api/v2/latest?apikey=23d576e0-4e8a-11ec-a99d-f5d85080afeb";
        MakeOptions makeOptions = new MakeOptions("GET");

        String res = Utility.fetchData(URL, makeOptions);
        
        try {
            JsonObject object = gson.fromJson(res, JsonObject.class);
            JsonObject data = gson.fromJson(object.get("data"), JsonObject.class);
            em.getTransaction().begin();
            for (Map.Entry<String, JsonElement> entry : data.entrySet()) {
                String currencyCode = entry.getKey();
                    Currency currency = em.find(Currency.class, currencyCode);
                    if(currency != null){
                        currency.setValue(entry.getValue().getAsDouble());
                    }
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.out.println("Error: "+ e.getMessage());
        }
    }
    
    public ArrayList<HistoricalCurrencyDTO> historicalCurrenciesFromApi(String baseCurrency) throws IOException {
        EntityManager em = emf.createEntityManager();
        ArrayList<HistoricalCurrencyDTO> listOfValues = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        LocalDate startDate = today.minusMonths(1);
        
        String dateTo = today.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String dateFrom = startDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String URL = "https://freecurrencyapi.net/api/v2/historical?apikey=23d576e0-4e8a-11ec-a99d-f5d85080afeb&date_from="+dateFrom+"&date_to="+dateTo+"&base_currency="+baseCurrency;
        MakeOptions makeOptions = new MakeOptions("GET");

        String res = Utility.fetchData(URL, makeOptions);
        LocalDate day_7 = today.minusDays(7);
        LocalDate day_14 = today.minusDays(14);
        try {
            JsonObject object = gson.fromJson(res, JsonObject.class);
            JsonObject data = gson.fromJson(object.get("data"), JsonObject.class);
            
            String day7String = day_7.format(DateTimeFormatter.ISO_LOCAL_DATE);
            String day14String = day_14.format(DateTimeFormatter.ISO_LOCAL_DATE);
            String monthString = startDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
            JsonObject todayObject = data.getAsJsonObject(dateTo);
            JsonObject day7 = data.getAsJsonObject(day7String);
            JsonObject day14 = data.getAsJsonObject(day14String);
            JsonObject month = data.getAsJsonObject(monthString);
            
            Map<String,HistoricalCurrencyDTO> dtos = new HashMap<String, HistoricalCurrencyDTO>();
            for (Map.Entry<String, JsonElement> todayEntry : todayObject.entrySet()) {
                String key = todayEntry.getKey().toLowerCase();
                Currency currency = em.find(Currency.class, key);
                if (currency != null) {
                    HistoricalCurrencyDTO dto = new HistoricalCurrencyDTO(currency);
                    dto.setValue(todayEntry.getValue().getAsDouble());
                    dtos.put(todayEntry.getKey(), dto);
                }

            }
            for (Map.Entry<String, JsonElement> entry7 : day7.entrySet()) {
                
                if (dtos.containsKey(entry7.getKey())) {
                    HistoricalCurrencyDTO dto = dtos.get(entry7.getKey());
                    dto.setDay7(entry7.getValue().getAsDouble());
                }
                
            }
            
            for (Map.Entry<String, JsonElement> entry14 : day14.entrySet()) {
                if (dtos.containsKey(entry14.getKey())) {
                    HistoricalCurrencyDTO dto = dtos.get(entry14.getKey());
                    dto.setDay14(entry14.getValue().getAsDouble());
                }

            }
            
            for (Map.Entry<String, JsonElement> entryMonth : month.entrySet()) {
                if (dtos.containsKey(entryMonth.getKey())) {
                    HistoricalCurrencyDTO dto = dtos.get(entryMonth.getKey());
                    dto.setLastMonth(entryMonth.getValue().getAsDouble());
                }
            }
            
            // Getting Collection of values from HashMap
            Collection<HistoricalCurrencyDTO> values = dtos.values();
            
            // Creating an ArrayList of values
            
            listOfValues = new ArrayList<>(values);
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return listOfValues;
    }
    
    public Currency getCurrencyFromDatabase(String currencyCode) throws API_Exception{
        EntityManager em = emf.createEntityManager();
        try {
            Currency currency = em.find(Currency.class, currencyCode);
            if (currency == null) {
                throw new API_Exception("Currency not found");
            }
            return currency;
        } finally {
            em.close();
        }
    }
    
    public List<Stock> getStockFromApi(List<String> symbols) throws IOException, API_Exception{
        EntityManager em = emf.createEntityManager();
        StringBuilder finalSymbolsString = new StringBuilder();
        symbols.forEach(x->finalSymbolsString.append(x+","));
        String URL = "https://stock-data-yahoo-finance-alternative.p.rapidapi.com/v6/finance/quote?symbols="+finalSymbolsString;
        MakeOptions makeOptions = new MakeOptions("GET");
        makeOptions.addHeaders("X-RapidAPI-Host", "stock-data-yahoo-finance-alternative.p.rapidapi.com");
        makeOptions.addHeaders("X-RapidAPI-Key", "28203dd768msh180dc4a2835a5abp1f1c73jsna6c0dcba0095");
        makeOptions.addHeaders("Accept", "application/json");
        
        String res = Utility.fetchData(URL, makeOptions);
        
        JsonObject object = gson.fromJson(res, JsonObject.class);
        JsonObject object1 = gson.fromJson(object.get("quoteResponse"), JsonObject.class);
        JsonArray jsonArray = gson.fromJson(object1.get("result"), JsonArray.class);
        
        List<Stock> stocks = new ArrayList<>();
        
        if (jsonArray.size() > 0) {
            for (JsonElement jsonElement : jsonArray) {
                StockDTO stockDTO = gson.fromJson(jsonElement, StockDTO.class);
                
                JsonObject element = jsonElement.getAsJsonObject();
                String currencyCode = element.get("currency").getAsString();
                
                
                em.getTransaction().begin();
                Currency currency = em.find(Currency.class, currencyCode);
                stockDTO.setCurrencyCode(currency);
                Stock stock = em.merge(stockDTO.getEntity()); //We use merge instead of persist because merge returns the managed object
                em.getTransaction().commit();

                stocks.add(stock);
            }
        } else {
            throw new API_Exception("Stock symbol not found");
        }
        
        return stocks;
    }
    
    
    public List<NewsDTO> getNewsFromApi() throws IOException, API_Exception {
        String URL = "https://seeking-alpha.p.rapidapi.com/news/v2/list?category=market-news::stocks&size=4";
        MakeOptions makeOptions = new MakeOptions("GET");
        makeOptions.addHeaders("X-RapidAPI-Host", "seeking-alpha.p.rapidapi.com");
        makeOptions.addHeaders("X-RapidAPI-Key", "28203dd768msh180dc4a2835a5abp1f1c73jsna6c0dcba0095");
        makeOptions.addHeaders("Accept", "application/json");
        

        String res = Utility.fetchData(URL, makeOptions);

        JsonObject object = gson.fromJson(res, JsonObject.class);
        JsonArray jsonArray = gson.fromJson(object.get("data"), JsonArray.class);

        List<NewsDTO> newsDTOs = new ArrayList<>();

        if (jsonArray.size() > 0) {
            for (JsonElement jsonElement : jsonArray) {
                JsonObject objectFromElement = jsonElement.getAsJsonObject();
                String title = objectFromElement.get("attributes").getAsJsonObject().get("title").getAsString();
                String url = objectFromElement.get("links").getAsJsonObject().get("canonical").getAsString();
                String urlImage = objectFromElement.get("links").getAsJsonObject().get("uriImage").getAsString();

                NewsDTO newsDTO = new NewsDTO(title,url,urlImage);
                newsDTOs.add(newsDTO);
            }
        } else {
            throw new API_Exception("Error fetching news");
        }

        return newsDTOs;
    }
   
    
    public Group addEditGroup(GroupDTO groupDTO, String username) throws API_Exception {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, username);
            List<Transaction> transactions = new ArrayList<>();
            
            
            //find transactions by id
            if (groupDTO.getTransactionIds() != null) {
                for (Integer id : groupDTO.getTransactionIds()) {
                    Transaction t = em.find(Transaction.class, id);
                    if (t == null) {
                        throw new API_Exception("Transaction not found");
                    } else {
                        transactions.add(t);
                    }
                }
            }
            
    
            Group group = groupDTO.getEntity();
            group = em.merge(group);
            group.setTransactions(transactions);
            if (group.getUser() == null) {
                user.addGroup(group);
            }
            
            em.getTransaction().commit();
            
            return group;
        } finally {
            em.close();
        }
    }
    
    public Group deleteGroup(int id, String username) throws API_Exception{
        EntityManager em = emf.createEntityManager();
        try {
            Group g = em.find(Group.class, id);
            if (g == null) {
                throw new API_Exception("Could not remove group with id: " + id);

            } else if (!(username.equals(g.getUser().getUserName()))) {
                throw new API_Exception("You can only delete your own groups");
            }
            em.getTransaction().begin();
            em.remove(g);
            em.getTransaction().commit();
            return g;
        } finally {
            em.close();
        }
        
    }
    
    public List<Currency> getAllCurrencies(){
        List<Currency> currencies;
        EntityManager em = emf.createEntityManager();
        try{
            TypedQuery<Currency> query = em.createQuery("SELECT c from Currency c", Currency.class);
            currencies = query.getResultList();
            return currencies;
        } finally{
            em.close();
        }
    }
}

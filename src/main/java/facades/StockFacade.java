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
import dtos.stock.StockDTO;
import entities.Currency;
import entities.Stock;
import entities.Transaction;
import entities.User;
import errorhandling.API_Exception;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import security.errorhandling.AuthenticationException;
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
        User user;
        try {
            user = em.find(User.class, username);
            List<String> symbolsToBeUpdated = new ArrayList<>();
            //loop through transactions
            user.getTransactionList().forEach(t->{
                long MAX_DURATION = MILLISECONDS.convert(5, MINUTES);

                Date now = new Date();
                Date previous = t.getStocksSymbol().getLastUpdated();
                long duration = now.getTime() - previous.getTime();

                if (duration >= MAX_DURATION) {
                    //tilføj symbol til liste
                    symbolsToBeUpdated.add(t.getStocksSymbol().getSymbol());
                }
            });
            
            //opdater hvis der er nogle symboler der skal opdateres.
            if(symbolsToBeUpdated.size() > 0){
                getStockFromApi(symbolsToBeUpdated);
            }
            
        } finally {
            em.close();
        }
        return user;
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
        makeOptions.addHeaders("X-RapidAPI-Key", "69a80a6d47msh72db9b5d84026b3p151955jsnd1872b4c005c");
        makeOptions.addHeaders("Accept", "application/json");
        
        String res = Utility.fetchData(URL, makeOptions);
        
        JsonObject object = gson.fromJson(res, JsonObject.class);
        JsonObject object1 = gson.fromJson(object.get("quoteResponse"), JsonObject.class);
        JsonArray jsonArray = gson.fromJson(object1.get("result"), JsonArray.class);
        
        List<Stock> stocks = new ArrayList<>();
        
        if (jsonArray.size() > 0) {
            for (JsonElement jsonElement : jsonArray) {
                StockDTO stockDTO = gson.fromJson(jsonElement, StockDTO.class);
                
                em.getTransaction().begin();
                Stock stock = em.merge(stockDTO.getEntity()); //We use merge instead of persist because merge returns the managed object
                em.getTransaction().commit();

                stocks.add(stock);
            }
        } else {
            throw new API_Exception("Stock symbol not found");
        }
        
        return stocks;
    }
    
    
}

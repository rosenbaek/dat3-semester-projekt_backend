/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.util.Properties;
import java.util.Set;
import com.google.gson.*;
import entities.Currency;
import entities.Transaction;
import entities.User;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import utils.api.MakeOptions;

/**
 *
 * @author tha
 */
public class Utility {
    private static Gson gson = new GsonBuilder().create();
    
    public static String fetchData(String _url, MakeOptions makeOptions) throws MalformedURLException, IOException {
        URL url = new URL(_url);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(makeOptions.getMethod());
        
        for (Map.Entry<String, String> set : makeOptions.getHeaders().entrySet()) {
            con.setRequestProperty(set.getKey(), set.getValue());
        }
        
        

        String res = new Scanner(con.getInputStream()).useDelimiter("\\Z").next();
        
        return res;
    }
    
    public static void printAllProperties() {
            Properties prop = System.getProperties();
            Set<Object> keySet = prop.keySet();
            for (Object obj : keySet) {
                    System.out.println("System Property: {" 
                                    + obj.toString() + "," 
                                    + System.getProperty(obj.toString()) + "}");
            }
    }
    
    public static void populateCurrency() throws IOException {
        EntityManagerFactory emf = EMF_Creator.createEntityManagerFactory();
        EntityManager em = emf.createEntityManager();
        try {      
            MakeOptions makeOptions = new MakeOptions("GET");
            String res = fetchData("https://cdn.jsdelivr.net/gh/fawazahmed0/currency-api@1/latest/currencies.json", makeOptions);
            Gson gson = new Gson();
            HashMap<String, String> map = gson.fromJson(res, HashMap.class);

            em.getTransaction().begin();
            for (Map.Entry<String, String> set : map.entrySet()) {
                em.persist(new Currency(set.getKey(), set.getValue(),0.0));
            }
            em.getTransaction().commit();         
        } finally {
            em.close();
        }
    }
    
    public static Double calcTotalPortFolioValue(List<Transaction> transactions, Currency userPreferredCurrency){
        Double result = 0.0;
        //Database base currency is USD!!!!!!
        
        
        for(Transaction t : transactions){
            Double currentPrice = t.getStocksSymbol().getCurrentPrice();
            
            String stockCurrencyCode = t.getStocksSymbol().getCurrency().getCode();
            Double stockCurrencyValue = t.getStocksSymbol().getCurrency().getValue();
            //convert to base currency
            if (!stockCurrencyCode.equals("usd") && stockCurrencyValue > 0.0) {    
                currentPrice = (currentPrice / stockCurrencyValue);
            }
            
            int units = t.getUnits();
            result = result + (currentPrice * units); 
        }
        
        //convert to userPreferredCurrency
        if (!userPreferredCurrency.getCode().equals("usd") && userPreferredCurrency.getValue() > 0.0) {
            result = (result * userPreferredCurrency.getValue());
        }
        return result;
    }
    
    public static boolean isSameDay(Date date1, Date date2) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        return fmt.format(date1).equals(fmt.format(date2));
    }
    
}

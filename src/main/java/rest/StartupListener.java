/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rest;

import facades.StockFacade;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import utils.EMF_Creator;

/**
 *
 * @author mikkel
 */
@WebListener
public class StartupListener implements ServletContextListener {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private ScheduledExecutorService sched;
    private StockFacade stockFacade;

    Runnable task2 = () -> {
        System.out.println("Task #2 is running"); 
        //Opdater alle currencies
        try{
            //Updates all currency values towards system default value
            stockFacade.updateCurrenciesFromApi();
            //Gets all stockSymbols in DB
            List<String> symbolsInDB = stockFacade.getAllStockSymbols();
            //Updates all stock's values
            stockFacade.getStockFromApi(symbolsInDB);
            
            //Loops through each user and updates their value (multi threaded for parallel run)
            ExecutorService executor = Executors.newCachedThreadPool();
            List<String> usernames = stockFacade.getAllUserNames();
                usernames.forEach(username ->{
                    executor.submit(new Runnable() {
                        @Override 
                        public void run() {
                            stockFacade.addPortfolioValueHistory(username);
                        }
                    });
            });
        } catch(Exception e){
            e.printStackTrace();
        }
    };
    
    @Override
    public void contextInitialized(ServletContextEvent event) {
        stockFacade = StockFacade.getStockFacade(EMF);
        sched = Executors.newSingleThreadScheduledExecutor();
        sched.scheduleAtFixedRate(task2, 0, 3, TimeUnit.MINUTES);
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        sched.shutdownNow();
    }

}

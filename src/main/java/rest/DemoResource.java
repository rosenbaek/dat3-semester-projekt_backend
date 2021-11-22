package rest;

import callables.ApiFetchCallable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import dtos.CombinedApiDTO;
import dtos.WeatherDTO;
import dtos.CurrencyApiDTO;
import entities.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import utils.EMF_Creator;
import utils.Utility;
import utils.api.MakeOptions;

/**
 * @author lam@cphbusiness.dk
 */
@Path("info")
public class DemoResource {
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    @Context
    private UriInfo context;

    @Context
    SecurityContext securityContext;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getInfoForAll() {
        return "{\"msg\":\"Hello anonymous\"}";
    }

    //Just to verify if the database is setup
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("all")
    public String allUsers() {

        EntityManager em = EMF.createEntityManager();
        try {
            TypedQuery<User> query = em.createQuery ("select u from User u",entities.User.class);
            List<User> users = query.getResultList();
            return "[" + users.size() + "]";
        } finally {
            em.close();
        }
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("threads")
    public void getFromThreads(@Suspended final AsyncResponse ar) {
        //Make options to get possibility to switch between methods and headers if needed.
        MakeOptions makeOptions = new MakeOptions("GET");
        
        //LinkedHashMap to ensure the correct order as opposite to normal hashmap
        //Key = URL to be fetched
        //Value = the options for the fetch
        LinkedHashMap<String, MakeOptions> urls = new LinkedHashMap<>();
        urls.put("https://cdn.jsdelivr.net/gh/fawazahmed0/currency-api@1/latest/currencies.json", makeOptions);
        urls.put("https://www.metaweather.com/api/location/search/?query=london", makeOptions);
        new Thread(() -> {
            List<String> results = new ArrayList<>();
            
            //Fetches data
            try {
              ExecutorService executor = Executors.newCachedThreadPool();
              List<Future<String>> futures = new ArrayList<>();
              for (Map.Entry<String, MakeOptions> url : urls.entrySet()) {
                Future<String> future = executor.submit(new ApiFetchCallable(url.getKey(), url.getValue()));
                futures.add(future);
              }
              
                //Get the results
                for (Future<String> future : futures) {
                    String str = future.get();
                    results.add(str);
                }
            } catch (Exception ex) {
                Logger.getLogger(DemoResource.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
            //Converts json data to desired DTO's
            CurrencyApiDTO currencyApiDTO = gson.fromJson(results.get(0), CurrencyApiDTO.class);
            //If resonsonse is in []
            WeatherDTO[] weatherDTOArray  = gson.fromJson(results.get(1), WeatherDTO[].class);
            
            //Merges DTO's to desired end result
            CombinedApiDTO combinedApiDTO = new CombinedApiDTO(weatherDTOArray[0],currencyApiDTO);
            
            //Returns data in json
            ar.resume(gson.toJson(combinedApiDTO));
        }).start();
    }

    
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("user")
    @RolesAllowed("user")
    public String getFromUser() {
        String thisuser = securityContext.getUserPrincipal().getName();
        return "{\"msg\": \"Hello to User: " + thisuser + "\"}";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("admin")
    @RolesAllowed("admin")
    public String getFromAdmin() {
        String thisuser = securityContext.getUserPrincipal().getName();
        return "{\"msg\": \"Hello to (admin) User: " + thisuser + "\"}";
    }
}
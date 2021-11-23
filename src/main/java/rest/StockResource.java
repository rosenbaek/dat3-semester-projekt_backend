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
import dtos.stock.AddTransactionDTO;
import entities.Currency;
import entities.Stock;
import entities.Transaction;
import entities.User;
import facades.StockFacade;
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
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import utils.EMF_Creator;
import utils.Utility;
import utils.api.MakeOptions;


@Path("stock")
public class StockResource {
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static final StockFacade stockFacade = StockFacade.getStockFacade(EMF);
    @Context
    private UriInfo context;

    @Context
    SecurityContext securityContext;

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @RolesAllowed("user")
    public Response addTransaction(String jsonString) {
        //Læg input JSON i DTO
        AddTransactionDTO inputDTO = gson.fromJson(jsonString, AddTransactionDTO.class);
        
        //Hent username ud fra token - sikrer at man kun kan tilføje til sin egen user
        String username = securityContext.getUserPrincipal().getName();
        
        //Tilføj så man kun kan tilføje med en user role
            //DONE
        
        //Tjek mod API om symbolet findes, hvis ikke - kast fejl.
            //Tjek om symbolet findes i stocks databasen
            //Opdater prisen hvis den gør, opret hvis ikke.
           
        //Find currency i databasen
            //Hvis ikke findes, kast fejl.
            
        //Tilføj transactionen til useren (facade lavet)
        //TODO: DELETE THIS IS DUMMY
        Stock s1 = new Stock("s1","test1 INC."); 
        Currency c4 = new Currency("code_c4","name_c4");
        
        Transaction inputT = new Transaction(s1,inputDTO.getUnits(),c4,inputDTO.getBoughtPrice());
        
        Transaction outputT = stockFacade.addTransaction(inputT, username);
        
        //Returner user DTO.
        AddTransactionDTO outPutDTO = new AddTransactionDTO(outputT);
        
        
        return Response.ok().entity(gson.toJson(outPutDTO)).build();
    }
}
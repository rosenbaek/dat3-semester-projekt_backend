package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.stock.AddTransactionDTO;
import dtos.user.UserDTO;
import entities.Currency;
import entities.Stock;
import entities.Transaction;
import entities.User;
import errorhandling.API_Exception;
import facades.StockFacade;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import utils.EMF_Creator;
import utils.Utility;


@Path("stock")
public class StockResource {
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static final StockFacade stockFacade = StockFacade.getStockFacade(EMF);
    @Context
    private UriInfo context;

    @Context
    SecurityContext securityContext;
    
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @RolesAllowed("user")
    public Response getUserData() throws IOException, API_Exception {
        //get username from token
        String username = securityContext.getUserPrincipal().getName();
        
        //Get user from database
        //Updates stock live value and currency values
        User user = stockFacade.getUserData(username);
        
        Double totalPortFolioValue = Utility.calcTotalPortFolioValue(user.getTransactionList(),user.getCurrencyCode());
      
        
        
        UserDTO userDTO = new UserDTO(user);
        userDTO.setTotalPortfolioValue(totalPortFolioValue);
        //userDTO.setNewsDTOs(stockFacade.getNewsFromApi());
        //return userDTO
        return Response.ok().entity(gson.toJson(userDTO)).build();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @RolesAllowed("user") //Tilføj så man kun kan tilføje med en user role
    public Response addTransaction(String jsonString) throws API_Exception, IOException {
        //Læg input JSON i DTO
        AddTransactionDTO inputDTO = gson.fromJson(jsonString, AddTransactionDTO.class);
        
        //Hent username ud fra token - sikrer at man kun kan tilføje til sin egen user
        String username = securityContext.getUserPrincipal().getName();
        
        //Find currency i databasen
        //Hvis ikke findes, kast fejl.
        //Currency currency = stockFacade.getCurrencyFromDatabase(inputDTO.getCurrencyCode());
        
        //Tjek mod API om symbolet findes, hvis ikke - kast fejl.
            //Tjek om symbolet findes i stocks databasen
            //Opdater prisen hvis den gør, opret hvis ikke.
            
        //Following 4 lines is made to make sure we only use 1 external API call for fetching prices for multiple stocks.
        //See more in getStockFromAPI method.
        List<String> symbols = new ArrayList<>();
        symbols.add(inputDTO.getStockSymbol());
        List<Stock> stocks = stockFacade.getStockFromApi(symbols);
        Stock stock = stocks.get(0);
        
        Transaction inputT = new Transaction(stock,inputDTO.getUnits(),inputDTO.getBoughtPrice());
        
        Transaction outputT = stockFacade.addTransaction(inputT, username);
        
        //Returner DTO.
        AddTransactionDTO outPutDTO = new AddTransactionDTO(outputT);
        
        
        return Response.ok().entity(gson.toJson(outPutDTO)).build();
    }
}
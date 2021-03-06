package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dtos.stock.AddTransactionDTO;
import dtos.stock.CurrencyDTO;
import dtos.stock.GroupDTO;
import dtos.stock.HistoricalCurrencyDTO;
import dtos.stock.ResultDTO;
import dtos.user.UserDTO;
import edu.emory.mathcs.backport.java.util.Arrays;
import entities.Currency;
import entities.Group;
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
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
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
    @RolesAllowed({"user","admin"})
    public Response getUserData() throws IOException, API_Exception {
        //get username from token
        String username = securityContext.getUserPrincipal().getName();
        
        //Get user from database
        //Updates stock live value and currency values
        User user = stockFacade.getUserData(username);
        
        ResultDTO resultDTO = Utility.calcPortFolio(user.getTransactionList(),user.getCurrencyCode());
      
        
        
        UserDTO userDTO = new UserDTO(user);
        userDTO.setTotalPortfolioValue(resultDTO.getTotalPortFolioValue());
        userDTO.setProfit(resultDTO.getProfitValue());
        userDTO.setNewsDTOs(stockFacade.getNewsFromApi());
        //return userDTO
        return Response.ok().entity(gson.toJson(userDTO)).build();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @RolesAllowed("user") //Tilf??j s?? man kun kan tilf??je med en user role
    public Response addTransaction(String jsonString) throws API_Exception, IOException {
        //L??g input JSON i DTO
        AddTransactionDTO inputDTO = gson.fromJson(jsonString, AddTransactionDTO.class);
        
        //Hent username ud fra token - sikrer at man kun kan tilf??je til sin egen user
        String username = securityContext.getUserPrincipal().getName();
        
        //Find currency i databasen
        //Hvis ikke findes, kast fejl.
        //Currency currency = stockFacade.getCurrencyFromDatabase(inputDTO.getCurrencyCode());
        
        //Tjek mod API om symbolet findes, hvis ikke - kast fejl.
            //Tjek om symbolet findes i stocks databasen
            //Opdater prisen hvis den g??r, opret hvis ikke.
            
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
    
    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    @RolesAllowed("user")
    public Response deleteGroup(@QueryParam("ids") List<String> ids) throws API_Exception {
        String username = securityContext.getUserPrincipal().getName();
        
        System.out.println("ids: " + ids.toString());
        
        List<Integer> integerIds = new ArrayList<>();
        
        ids.forEach(val ->{integerIds.add(Integer.parseInt(val));});
        //Delete
        stockFacade.removeTransactions(integerIds, username);

        //Create response
        JsonObject response = new JsonObject();
        response.addProperty("code", 200);
        response.addProperty("msg", "Succesfully deleted transactions with IDs: " + ids.toString());

        return Response.ok().entity(gson.toJson(response)).build();
    }
    
    
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @RolesAllowed("user")
    @Path("group")
    public Response addEditGroup(String jsonString) throws API_Exception, IOException {
        //L??g input JSON i DTO
        GroupDTO inputDTO = gson.fromJson(jsonString, GroupDTO.class);

        //Hent username ud fra token - sikrer at man kun kan tilf??je til sin egen user
        String username = securityContext.getUserPrincipal().getName();
        
        //Kald facade som skal finde, opdatere og returnere user
        Group group = stockFacade.addEditGroup(inputDTO, username);
        
        GroupDTO gdto = new GroupDTO(group);

        return Response.ok().entity(gson.toJson(gdto)).build();
    }
    
    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    @RolesAllowed("user")
    @Path("group/{id}")
    public Response deleteGroup(@PathParam("id") int id) throws API_Exception{
        String username = securityContext.getUserPrincipal().getName();
        
        //Delete
        Group deletedGroup = stockFacade.deleteGroup(id,username);
        
        //Create response
        JsonObject response = new JsonObject();
        response.addProperty("code", 200);
        response.addProperty("msg", "Succesfully deleted group with ID: "+deletedGroup.getId());
        
        return Response.ok().entity(gson.toJson(response)).build();
    }
    
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @RolesAllowed({"user","admin"})
    @Path("currencies")
    public Response getAllCurrencies() {
        List<CurrencyDTO> currencies = CurrencyDTO.getCurrencyDTOs(stockFacade.getAllCurrencies());
        return Response.ok().entity(gson.toJson(currencies)).build();
    }
    
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @RolesAllowed({"user", "admin"})
    @Path("historical/currencies/{baseCurrency}")
    public Response getAllHistoricalCurrencies(@PathParam("baseCurrency") String baseCurrency) throws IOException {
        List<HistoricalCurrencyDTO> currencies = stockFacade.historicalCurrenciesFromApi(baseCurrency);
        return Response.ok().entity(gson.toJson(currencies)).build();
    }
}
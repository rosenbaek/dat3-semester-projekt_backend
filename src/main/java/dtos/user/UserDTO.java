/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dtos.user;

import dtos.stock.AddTransactionDTO;
import dtos.stock.CurrencyDTO;
import dtos.stock.GroupDTO;
import dtos.stock.NewsDTO;
import dtos.stock.PortfolioValueDTO;
import dtos.stock.TransactionDTO;
import entities.Currency;
import entities.User;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mikke
 */
public class UserDTO {
    private String username;
    private Double totalPortfolioValue;
    private Double profLoss;
    private String defaultCurrency;
    private List<PortfolioValueDTO> historicalPortFolioValue = new ArrayList<>();
    private String password;
    private List<RoleDTO> roles = new ArrayList<>();
    private List<GroupDTO> groups = new ArrayList<>();
    private List<TransactionDTO> transactions = new ArrayList<>();
    private List<NewsDTO> news = new ArrayList<>();
    
    
    public UserDTO(User user) {
        this.username = user.getUserName();
        user.getRoleList().forEach(role->this.roles.add(new RoleDTO(role)));
        user.getTransactionList().forEach(transaction -> this.transactions.add(new TransactionDTO(transaction)));
        if (user.getCurrencyCode() != null) {
            this.defaultCurrency = user.getCurrencyCode().getCode();
        }
        user.getHistoricalPortfolioValues().forEach(pfv->this.historicalPortFolioValue.add(new PortfolioValueDTO(pfv)));
        if(!(user.getGroups().isEmpty())){
            user.getGroups().forEach(group->this.groups.add(new GroupDTO(group)));
        }
    }

    public UserDTO() {
    }

    public List<NewsDTO> getNewsDTOs() {
        return news;
    }

    public void setNewsDTOs(List<NewsDTO> newsDTOs) {
        this.news = newsDTOs;
    }

    
    public User getEntity(){
        User user = new User(this.username, this.password);
        if(this.defaultCurrency != null){
            user.setCurrencyCode(new Currency());
            user.getCurrencyCode().setCode(this.defaultCurrency);
        }
        this.roles.forEach(roleDTO->user.addRole(roleDTO.getEntity()));
        return user;
    }

    public void setTotalPortfolioValue(Double totalPortfolioValue) {
        this.totalPortfolioValue = totalPortfolioValue;
    }

    public String getDefaultCurrency() {
        return defaultCurrency;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public Double getProfit() {
        return profLoss;
    }

    public void setProfit(Double Profit) {
        this.profLoss = Profit;
    }
    
    
    
    
   
    
}

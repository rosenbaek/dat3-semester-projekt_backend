/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dtos.user;

import dtos.stock.AddTransactionDTO;
import dtos.stock.TransactionDTO;
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
    private String password;
    private List<RoleDTO> roles = new ArrayList<>();
    private List<TransactionDTO> transactions = new ArrayList<>();
    
    public UserDTO(User user) {
        this.username = user.getUserName();
        user.getRoleList().forEach(role->this.roles.add(new RoleDTO(role)));
        user.getTransactionList().forEach(transaction -> this.transactions.add(new TransactionDTO(transaction)));
    }

    public UserDTO() {
    }

    
    public User getEntity(){
        User user = new User(this.username, this.password);
        this.roles.forEach(roleDTO->user.addRole(roleDTO.getEntity()));
        return user;
    }

    public void setTotalPortfolioValue(Double totalPortfolioValue) {
        this.totalPortfolioValue = totalPortfolioValue;
    }
    
    
}

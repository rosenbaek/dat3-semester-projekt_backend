/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import utils.Utility;

/**
 *
 * @author mikke
 */
@Entity
@Table(name = "portfolio_groups")
@NamedQuery(name = "Group.deleteAllRows", query = "DELETE from Group")
public class Group implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "units")
    private String groupName;
    
    @JoinColumn(name = "users_user_name", referencedColumnName = "user_name")
    @ManyToOne
    private User user;
    
    
    @JoinTable(name = "groups_transactions", joinColumns = {
        @JoinColumn(name = "transaction_id", referencedColumnName = "id")}, 
        inverseJoinColumns = {
        @JoinColumn(name = "group_id", referencedColumnName = "id")})
    @ManyToMany
    private List<Transaction> transactions = new ArrayList<>();

    public Group() {
    }

    public Group(String groupName) {
        this.groupName = groupName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }
    
    
    public Double getValue (){
        Double result = Utility.calcTotalPortFolioValue(this.transactions, this.user.getCurrencyCode());
        return result;
    }
    
    public List<Integer> getTransactionIds (){
        List<Integer> result = new ArrayList<>();
        this.transactions.forEach(t->{
            result.add(t.getId());
        
        });
        return result;
    }
        
    

    

   

    
}

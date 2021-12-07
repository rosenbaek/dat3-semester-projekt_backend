package entities;

import dtos.user.UserDTO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;
import org.mindrot.jbcrypt.BCrypt;

@Entity
@Table(name = "users")
@NamedQuery(name = "User.deleteAllRows", query = "DELETE from User")
public class User implements Serializable {

    @Size(max = 255)
    @Column(name = "user_pass")
    private String userPass;
    
    @OneToMany(mappedBy = "user")
    private List<Transaction> transactionList = new ArrayList<>();
    
    @JoinColumn(name = "currencies_code", referencedColumnName = "code")
    @ManyToOne
    private Currency currencyCode;

    private static final long serialVersionUID = 1L;
    
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "user_name", length = 25)
    private String userName;
    
    @JoinTable(name = "user_roles", joinColumns = {
        @JoinColumn(name = "user_name", referencedColumnName = "user_name")}, inverseJoinColumns = {
        @JoinColumn(name = "role_name", referencedColumnName = "role_name")})
    @ManyToMany
    private List<Role> roleList = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<PortfolioValue> historicalPortfolioValues = new ArrayList<>();
    
    @OneToMany(mappedBy = "user")
    private List<Group> groups = new ArrayList<>();

    public List<String> getRolesAsStrings() {
        if (roleList.isEmpty()) {
            return null;
        }
        List<String> rolesAsStrings = new ArrayList<>();
        roleList.forEach((role) -> {
            rolesAsStrings.add(role.getRoleName());
        });
        return rolesAsStrings;
    }

    public User() {
    }

    //pw is plain password
    public boolean verifyPassword(String pw) {
        return (BCrypt.checkpw(pw, userPass));
    }

    public User(String userName, String userPass) {
        if (userPass != null) {
            this.userPass = BCrypt.hashpw(userPass, BCrypt.gensalt());
        }
        this.userName = userName;
    }

    public List<PortfolioValue> getHistoricalPortfolioValues() {
        return historicalPortfolioValues;
    }

    public void addHistoricalPortfolioValue(PortfolioValue portfolioValue) {
        this.historicalPortfolioValues.add(portfolioValue);
        portfolioValue.setUser(this);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPass() {
        return this.userPass;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }

    public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }

    public void addRole(Role userRole) {
        roleList.add(userRole);
    }

    public List<Transaction> getTransactionList() {
        return transactionList;
    }

    public void addTransaction(Transaction transaction) {
        this.transactionList.add(transaction);
        transaction.setUsersUserName(this);
    }

    public Currency getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(Currency currencyCode) {
        this.currencyCode = currencyCode;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void addGroup(Group group) {
       
        this.groups.add(group);
        group.setUser(this);
    }
    
    public void updateUser (User user){
        if(user.getUserPass() != null){
            this.userPass = user.getUserPass();
        }
        this.currencyCode = user.getCurrencyCode();
    }
    

}

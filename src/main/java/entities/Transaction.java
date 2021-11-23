/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author mikke
 */
@Entity
@Table(name = "transactions")
@NamedQuery(name = "Transaction.deleteAllRows", query = "DELETE from Transaction")
public class Transaction implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "units")
    private Integer units;
    @Column(name = "bought_price")
    private Double boughtPrice;
    @JoinColumn(name = "currencies_code", referencedColumnName = "code")
    @ManyToOne
    private Currency currencyCode;
    @JoinColumn(name = "stocks_symbol", referencedColumnName = "symbol")
    @ManyToOne
    private Stock stockSymbol;
    @JoinColumn(name = "users_user_name", referencedColumnName = "user_name")
    @ManyToOne
    private User user;

    public Transaction() {
    }

    public Transaction(Integer id) {
        this.id = id;
    }

    public Transaction(Stock stock, int units, Currency currency, Double boughtPrice) {
        this.stockSymbol = stock;
        this.units = units;
        this.currencyCode = currency;
        this.boughtPrice = boughtPrice;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUnits() {
        return units;
    }

    public void setUnits(Integer units) {
        this.units = units;
    }

    public Double getBoughtPrice() {
        return boughtPrice;
    }

    public void setBoughtPrice(Double boughtPrice) {
        this.boughtPrice = boughtPrice;
    }

    public Currency getCurrenciesCode() {
        return currencyCode;
    }

    public void setCurrenciesCode(Currency currenciesCode) {
        this.currencyCode = currenciesCode;
    }

    public Stock getStocksSymbol() {
        return stockSymbol;
    }

    public void setStocksSymbol(Stock stocksSymbol) {
        this.stockSymbol = stocksSymbol;
    }

    public User getUsersUserName() {
        return user;
    }

    public void setUsersUserName(User usersUserName) {
        this.user = usersUserName;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Transaction)) {
            return false;
        }
        Transaction other = (Transaction) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Transactions[ id=" + id + " ]";
    }
    
}

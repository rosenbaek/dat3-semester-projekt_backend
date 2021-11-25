/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author mikke
 */
@Entity
@Table(name = "stocks")
@NamedQuery(name = "Stock.deleteAllRows", query = "DELETE from Stock")
public class Stock implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "symbol")
    private String symbol;
    @Column(name = "name")
    private String name;

    @JoinColumn(name = "currencies_code", referencedColumnName = "code")
    @ManyToOne
    private Currency currency;
    

    @Column(name = "current_price")
    private Double currentPrice;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_updated")
    private Date lastUpdated;

    @OneToMany(mappedBy = "stockSymbol")
    private List<Transaction> transactionsList = new ArrayList<>();

    public Stock() {
    }

    public Stock(String symbol, String name, Currency currency, Double currentPrice) {
        this.symbol = symbol;
        this.name = name;
        this.currency = currency;
        this.currentPrice = currentPrice;
        this.lastUpdated = new Date();
    }

    public Double getCurrentPrice() {
        return currentPrice;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public Currency getCurrency() {
        return currency;
    }

   

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlTransient
    public List<Transaction> getTransactionsList() {
        return transactionsList;
    }

    public void setTransactionsList(List<Transaction> transactionsList) {
        this.transactionsList = transactionsList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (symbol != null ? symbol.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Stock)) {
            return false;
        }
        Stock other = (Stock) object;
        if ((this.symbol == null && other.symbol != null) || (this.symbol != null && !this.symbol.equals(other.symbol))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Stocks[ symbol=" + symbol + " ]";
    }

}

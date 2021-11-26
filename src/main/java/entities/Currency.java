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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author mikke
 */
@Entity
@Table(name = "currencies")
@NamedQuery(name = "Currency.deleteAllRows", query = "DELETE from Currency")
public class Currency implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "code")
    private String code;
    @Column(name = "name")
    private String name;
    @OneToMany(mappedBy = "currency")
    private List<Stock> stockList = new ArrayList<>();
    @OneToMany(mappedBy = "currencyCode")
    private List<User> userList = new ArrayList<>();
    @Column(name = "value")
    private Double value;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_updated")
    private Date lastUpdated;    

    public Currency() {
    }

    public Currency(String code, String name, Double value) {
        this.code = code;
        this.name = name;
        this.value = value;
        this.lastUpdated = new Date();
    }

    public Double getValue() {
        return value;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setValue(Double value) {
        this.value = value;
        this.lastUpdated = new Date();
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Stock> getStockList() {
        return stockList;
    }

    public void setStockList(List<Stock> stockList) {
        this.stockList = stockList;
    }

    

    

    @XmlTransient
    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (code != null ? code.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Currency)) {
            return false;
        }
        Currency other = (Currency) object;
        if ((this.code == null && other.code != null) || (this.code != null && !this.code.equals(other.code))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Currencies[ code=" + code + " ]";
    }
    
}

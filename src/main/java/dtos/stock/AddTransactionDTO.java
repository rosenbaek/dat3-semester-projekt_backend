/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dtos.stock;

import entities.Transaction;

/**
 *
 * @author mikke
 */
public class AddTransactionDTO {
    private Integer id;
    private Integer units;
    private Double boughtPrice;
    private String stockSymbol;
    private String currencyCode;

    public Integer getUnits() {
        return units;
    }

    public Double getBoughtPrice() {
        return boughtPrice;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public AddTransactionDTO(Transaction transaction) {
        if(transaction.getId() != null){
            this.id = transaction.getId();
        }
        this.units = transaction.getUnits();
        this.boughtPrice = transaction.getBoughtPrice();
        this.stockSymbol = transaction.getStocksSymbol().getSymbol();
        this.currencyCode = transaction.getCurrenciesCode().getCode();
    }
    
    
    
    
}

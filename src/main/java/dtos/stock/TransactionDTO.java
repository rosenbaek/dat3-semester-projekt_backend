/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dtos.stock;

import entities.Transaction;

/**
 *
 * @author christianrosenbaek
 */
public class TransactionDTO {
    private int id;
    private StockDTO stock;
    private int units;
    private double boughtPrice;
    private CurrencyDTO currency;

    public TransactionDTO(Transaction transaction) {
        if (transaction.getId() != null) {
            this.id = transaction.getId();
        }
        this.stock = new StockDTO(transaction.getStocksSymbol());
        this.units = transaction.getUnits();
        this.boughtPrice = transaction.getBoughtPrice();
        this.currency = new CurrencyDTO(transaction.getCurrenciesCode());
    }

    
}

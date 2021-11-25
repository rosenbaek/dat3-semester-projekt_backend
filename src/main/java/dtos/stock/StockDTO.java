/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dtos.stock;

import entities.Stock;
import java.util.Date;

/**
 *
 * @author christianrosenbaek
 */
public class StockDTO {
    private String symbol;
    private String shortName;
    private Double regularMarketPrice;
    private Date lastUpdated;

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return shortName;
    }
    
    public Stock getEntity() {
        return new Stock(symbol,shortName,regularMarketPrice);
    }

    public StockDTO(Stock stock) {
        this.symbol = stock.getSymbol();
        this.shortName = stock.getName();
        this.regularMarketPrice = stock.getCurrentPrice();
        this.lastUpdated = stock.getLastUpdated();
    }
    
    
}

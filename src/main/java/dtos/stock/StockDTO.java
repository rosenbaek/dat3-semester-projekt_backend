/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dtos.stock;

import entities.Stock;

/**
 *
 * @author christianrosenbaek
 */
public class StockDTO {
    private String symbol;
    private String shortName;

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return shortName;
    }
    
    public Stock getEntity() {
        return new Stock(symbol,shortName);
    }
}

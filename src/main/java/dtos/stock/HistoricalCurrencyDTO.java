/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dtos.stock;

import entities.Currency;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author christianrosenbaek
 */
public class HistoricalCurrencyDTO {
    private String code;
    private String name;
    private Double value;
    private Double day7;
    private Double day14;
    private Double lastMonth;
    

    public HistoricalCurrencyDTO(Currency currency) {
        this.code = currency.getCode();
        this.name = currency.getName();
        this.value = currency.getValue();
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

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Double getDay7() {
        return day7;
    }

    public void setDay7(Double day7) {
        this.day7 = day7;
    }

    public Double getDay14() {
        return day14;
    }

    public void setDay14(Double day14) {
        this.day14 = day14;
    }

    public Double getLastMonth() {
        return lastMonth;
    }

    public void setLastMonth(Double lastMonth) {
        this.lastMonth = lastMonth;
    }

    

    
    
}

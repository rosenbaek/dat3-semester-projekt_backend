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
public class CurrencyDTO {
    private String code;
    private String name;
    private Double value;

    public CurrencyDTO(Currency currency) {
        this.code = currency.getCode();
        this.name = currency.getName();
        this.value = currency.getValue();
    }
    
    
    
    
    public Currency getEntity() {
        return new Currency(this.code, this.name, this.value);
    }
    
    public static List<CurrencyDTO> getCurrencyDTOs (List<Currency> currencies){
        List<CurrencyDTO> currencyDTos = new ArrayList<>();
        currencies.forEach(c ->{
            currencyDTos.add(new CurrencyDTO(c));
        });
        return currencyDTos;
    }
}

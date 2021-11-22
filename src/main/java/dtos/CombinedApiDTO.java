/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dtos;

import java.util.HashMap;
/**
 *
 * @author christianrosenbaek
 */
public class CombinedApiDTO {
    private WeatherDTO weather;
    private CurrencyApiDTO currencies;

    public CombinedApiDTO(WeatherDTO weather, CurrencyApiDTO currencies) {
        this.weather = weather;
        this.currencies = currencies;
    }
    
}

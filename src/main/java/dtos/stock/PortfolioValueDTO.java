/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dtos.stock;

import entities.PortfolioValue;

/**
 *
 * @author mikkel
 */
public class PortfolioValueDTO {
    private Integer id;
    private String date;
    private Double value;

    public PortfolioValueDTO(PortfolioValue pfv) {
        if(pfv.getId() != null){
            this.id = pfv.getId();
        }
        this.date = pfv.getDate().toString();
        this.value = pfv.getValue();
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
    
    
    
    
}

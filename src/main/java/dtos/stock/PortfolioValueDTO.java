/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dtos.stock;

import entities.PortfolioValue;
import java.text.DateFormat;
import java.util.Date;

/**
 *
 * @author mikkel
 */
public class PortfolioValueDTO {
    private Integer id;
    private Date date;
    private Double value;

    public PortfolioValueDTO(PortfolioValue pfv) {
        if(pfv.getId() != null){
            this.id = pfv.getId();
        }
        this.date = pfv.getDate();
        this.date.setHours(1);
        this.value = pfv.getValue();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

   

    
    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
    
    
    
    
}

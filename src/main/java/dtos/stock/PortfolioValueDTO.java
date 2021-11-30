package dtos.stock;

import entities.PortfolioValue;
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
        this.date.setHours(1); //to make sure that time is not 00:00 otherwise confusion if which day it is
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

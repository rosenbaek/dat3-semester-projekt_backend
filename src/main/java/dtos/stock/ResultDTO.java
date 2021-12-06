/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dtos.stock;

/**
 *
 * @author mikkel
 */
public class ResultDTO {
    private Double TotalPortFolioValue;
    private Double ProfitValue;

    public ResultDTO(Double TotalPortFolioValue, Double ProfitValue) {
        this.TotalPortFolioValue = TotalPortFolioValue;
        this.ProfitValue = ProfitValue;
    }

    

    public Double getTotalPortFolioValue() {
        return TotalPortFolioValue;
    }

    public void setTotalPortFolioValue(Double TotalPortFolioValue) {
        this.TotalPortFolioValue = TotalPortFolioValue;
    }

    public Double getProfitValue() {
        return ProfitValue;
    }

    public void setProfitValue(Double ProfitValue) {
        this.ProfitValue = ProfitValue;
    }
    
    
    
}

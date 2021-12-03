/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dtos.stock;

import entities.Group;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author mikkel
 */
public class GroupDTO {
    private Integer id;
    private String name;
    private Double value;
    private List<Integer> transactionIds;

    public GroupDTO(Group group) {
        if(group.getId() != null){
            this.id = group.getId();
        }
        this.name = group.getGroupName();
        this.value = group.getValue();
        this.transactionIds = group.getTransactionIds();
    }

    public Group getEntity() {
        Group group = new Group(this.name);
        if (this.id != null) {
            group.setId(this.id);
        }
        return group;
    }
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public List<Integer> getTransactionIds() {
        return transactionIds;
    }

    public void setTransactionIds(List<Integer> transactionIds) {
        this.transactionIds = transactionIds;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + Objects.hashCode(this.id);
        hash = 71 * hash + Objects.hashCode(this.name);
        hash = 71 * hash + Objects.hashCode(this.value);
        hash = 71 * hash + Objects.hashCode(this.transactionIds);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GroupDTO other = (GroupDTO) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        if (!Objects.equals(this.transactionIds, other.transactionIds)) {
            return false;
        }
        return true;
    }
    
    
    
    
    
}

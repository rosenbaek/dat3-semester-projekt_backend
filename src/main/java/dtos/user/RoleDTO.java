/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dtos.user;

import entities.Role;

/**
 *
 * @author mikke
 */
public class RoleDTO {
    private String rolename;

    public RoleDTO(Role role) {
        this.rolename = role.getRoleName();
    }
    
    
    
    public Role getEntity(){
       return new Role(this.rolename);
    }
}

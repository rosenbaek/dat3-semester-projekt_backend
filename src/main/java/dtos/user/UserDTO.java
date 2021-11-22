/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dtos.user;

import entities.User;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mikke
 */
public class UserDTO {
    private String username;
    private String password;
    private List<RoleDTO> roles = new ArrayList<>();

    public UserDTO(User user) {
        this.username = user.getUserName();
        user.getRoleList().forEach(role->this.roles.add(new RoleDTO(role)));
    }

    public UserDTO() {
    }

    
    public User getEntity(){
        User user = new User(this.username, this.password);
        this.roles.forEach(roleDTO->user.addRole(roleDTO.getEntity()));
        return user;
    }
}

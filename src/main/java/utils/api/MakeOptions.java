/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils.api;

import java.util.HashMap;

/**
 *
 * @author christianrosenbaek
 */
public class MakeOptions {
    private String method;
    private HashMap<String, String> headers;

    public MakeOptions(String method) {
        this.method = method;
        headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("User-Agent", "server");
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public void addHeaders(String key, String value) {
        if(this.headers.containsKey(key)){
            this.headers.replace(key, value);
        } else {
            this.headers.put(key, value);
        } 
    }
}

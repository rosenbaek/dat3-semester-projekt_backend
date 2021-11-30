/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dtos.stock;

/**
 *
 * @author christianrosenbaek
 */
public class NewsDTO {
    private String title;
    private String link;
    private String urlImage;

    public NewsDTO(String title, String link, String urlImage) {
        this.title = title;
        this.link = link;
        this.urlImage = urlImage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }
    
    
    
}

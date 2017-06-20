package org.rajmoh.radio.pojo;

public class Album {
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTimeago() {
        return timeago;
    }

    public void setTimeago(String timeago) {
        this.timeago = timeago;
    }

    public String getAlbumArt() {
        return albumArt;
    }

    public void setAlbumArt(String albumArt) {
        this.albumArt = albumArt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String timeago;
    private String albumArt;
    private String description;
 
    public Album() {
    }
 
    public Album(String title, String timeago,String albumArt,String description) {
        this.title=title;
        this.timeago=timeago;
        this.albumArt=albumArt;
        this.description=description;
    }
 


}
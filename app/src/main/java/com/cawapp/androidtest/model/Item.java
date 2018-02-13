package com.cawapp.androidtest.model;

import java.io.Serializable;



public class Item implements Serializable{
    private String name;
    private String text;
    private String image;
    private String bigimage;
    private boolean selected = false;

    public Item(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public Item(String name,String text, String image) {
        this.name = name;
        this.text = text;
        this.image = image;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setText(String text){
        this.text = text;
    }

    public String getText(){
        return text;
    }

    public void setImage(String image){
        this.image = image;
    }

    public String getImage(){
        return image;
    }

    public void setBigImage(String bigimage){
        this.bigimage = bigimage;
    }

    public String getBigImage(){
        return bigimage;
    }

    public void setSelected(boolean selected){
        this.selected = selected;
    }

    public boolean isSelected(){
        return selected;
    }

}

package com.vmax.demo.nativeCSRecycler;

import android.graphics.drawable.Drawable;

/**
 * Created by prathameshs on 07-04-2017.
 */

public class BlogModel {
    Drawable blogImage;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Drawable getBlogImage() {
        return blogImage;
    }

    public void setBlogImage(Drawable blogImage) {
        this.blogImage = blogImage;
    }

    String title,author,date;
}

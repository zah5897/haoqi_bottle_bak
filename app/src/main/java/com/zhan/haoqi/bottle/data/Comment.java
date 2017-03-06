package com.zhan.haoqi.bottle.data;

/**
 * Created by zah on 2017/1/13.
 */

public class Comment {
    private long id;
    private long bottle_id;
    private long author_user_id;
    private String comment_time;
    private String content;
    private User user;

    private Comment at_comment;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getBottle_id() {
        return bottle_id;
    }

    public void setBottle_id(long bottle_id) {
        this.bottle_id = bottle_id;
    }

    public long getAuthor_user_id() {
        return author_user_id;
    }

    public void setAuthor_user_id(long author_user_id) {
        this.author_user_id = author_user_id;
    }

    public String getComment_time() {
        return comment_time;
    }

    public void setComment_time(String comment_time) {
        this.comment_time = comment_time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Comment getAt_comment() {
        return at_comment;
    }

    public void setAt_comment(Comment at_comment) {
        this.at_comment = at_comment;
    }
}

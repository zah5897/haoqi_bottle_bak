package com.zhan.haoqi.bottle.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.zhan.haoqi.bottle.data.type.BottleType;

import java.util.Date;

/**
 * Created by zah on 2016/11/30.
 */

public class Bottle implements Parcelable {
    private long id;
    private String content;
    private String image;
    private String create_time;
    private BottleType type;
    private User sender;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }


    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public BottleType getType() {
        return type;
    }

    public void setType(BottleType type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.content);
        dest.writeString(this.image);
        dest.writeString(this.create_time);
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
        dest.writeParcelable(this.sender, flags);
    }

    public Bottle() {
    }

    protected Bottle(Parcel in) {
        this.id = in.readLong();
        this.content = in.readString();
        this.image = in.readString();
        this.create_time = in.readString();
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : BottleType.values()[tmpType];
        this.sender = in.readParcelable(User.class.getClassLoader());
    }

    public static final Parcelable.Creator<Bottle> CREATOR = new Parcelable.Creator<Bottle>() {
        @Override
        public Bottle createFromParcel(Parcel source) {
            return new Bottle(source);
        }

        @Override
        public Bottle[] newArray(int size) {
            return new Bottle[size];
        }
    };
}

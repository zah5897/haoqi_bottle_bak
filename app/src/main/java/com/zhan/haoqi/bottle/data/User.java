package com.zhan.haoqi.bottle.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zah on 2016/10/21.
 */
public class User implements Parcelable {
    public String nick_name;
    public String mobile;
    public String avatar;
    public String token;
    public int gender;
    public long id;
    public String birthday;
    public int integral; //积分


    public User() {
    }

    protected User(Parcel in) {
        nick_name = in.readString();
        mobile = in.readString();
        avatar = in.readString();
        token = in.readString();
        gender = in.readInt();
        id = in.readLong();
        birthday = in.readString();
        integral = in.readInt();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(nick_name);
        parcel.writeString(mobile);
        parcel.writeString(avatar);
        parcel.writeString(token);
        parcel.writeInt(gender);
        parcel.writeLong(id);
        parcel.writeString(birthday);
        parcel.writeInt(integral);
    }
}

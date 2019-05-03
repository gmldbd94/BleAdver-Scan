package org.techtown.smartcity;

import android.os.Parcel;
import android.os.Parcelable;

public class AdvertiseInfo implements Parcelable {
    String birth;
    String num;
    String gender;


    public AdvertiseInfo(String birth_, String num_, String gender_){
        birth = birth_;
        num = num_;
        gender = gender_;

    }

    public AdvertiseInfo(Parcel src) {
        birth = src.readString();
        num = src.readString();
        gender = src.readString();

    }

    public static final Creator<AdvertiseInfo> CREATOR = new Creator<AdvertiseInfo>() {
        @Override
        public AdvertiseInfo createFromParcel(Parcel in) {
            return new AdvertiseInfo(in);
        }

        @Override
        public AdvertiseInfo[] newArray(int size) {
            return new AdvertiseInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(birth);
        dest.writeString(gender);
        dest.writeString(num);
    }
}

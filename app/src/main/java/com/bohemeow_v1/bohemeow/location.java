package com.bohemeow_v1.bohemeow;


import android.os.Parcel;
import android.os.Parcelable;

public class location implements Parcelable {

    double lat;
    double lng;

    public location(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public location(Parcel src){
        lat = src.readDouble();
        lng = src.readDouble();
    }

    // Parcel -> location
    public static final Creator<location> CREATOR = new Creator<location>() {
        @Override
        public location createFromParcel(Parcel in) {
            return new location(in);
        }

        @Override
        public location[] newArray(int size) {
            return new location[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }


    // location -> Parcel
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(lat);
        dest.writeDouble(lng);
    }

}
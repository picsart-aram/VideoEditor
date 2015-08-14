package com.generatingmain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Arman on 5/6/15.
 */
public class EffectChoser implements Parcelable {
    String name;

    public EffectChoser(String nm){
        this.name = nm;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
    }

    private EffectChoser(Parcel in) {
        this.name = in.readString();

    }

    public static final Creator<EffectChoser> CREATOR = new Creator<EffectChoser>() {
        public EffectChoser createFromParcel(Parcel source) {
            return new EffectChoser(source);
        }

        public EffectChoser[] newArray(int size) {
            return new EffectChoser[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String title) {
        this.name = title;
    }

    @Override
    public String toString() {
        return name;
    }

}

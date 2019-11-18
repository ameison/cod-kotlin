package app.conectados.android.models;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

@SuppressLint("ParcelCreator")
public class Mensaje implements Parcelable {

    @SerializedName("id")
    public Integer customerId;
    public Integer providerId;
    public String providerName;
    public String customerName;
    public String body;
    public String date;
    public Ad ad;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
package app.conectados.android.models;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

@SuppressLint("ParcelCreator")
public class Llamada implements Parcelable {

    @SerializedName("id")
    public Integer ad_id;
    public String ad_name;
    public String ad_price;

    public Integer provider_id;
    public String provider_name;
    public String provider_phone;
    public String call_date;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
package app.conectados.android.models;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

@SuppressLint("ParcelCreator")
public class AdPics implements Parcelable {

    @SerializedName("id") public Integer id;
    @SerializedName("path") public String path;
    @SerializedName("active") public boolean active;
    @SerializedName("ads_id") public int ads_id;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }


}

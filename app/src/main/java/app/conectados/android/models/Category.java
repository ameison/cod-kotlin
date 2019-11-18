package app.conectados.android.models;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressLint("ParcelCreator")
public class Category implements Parcelable {

    @SerializedName("id") public int id;
    @SerializedName("name")  public String name;
    @SerializedName("mipmapName")  public String mipmapName;
    public List<SubCategory> subCategorias;

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {}


}


package app.conectados.android.models;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("ParcelCreator")
public class Ad implements Parcelable {

    @SerializedName("id") public Integer id;
    @SerializedName("name") public String name;
    @SerializedName("description") public String description;
    @SerializedName("price") public String price;
    @SerializedName("active") public boolean active;

    @SerializedName("category_id") public int categoryId;
    @SerializedName("sub_category_id") public int subCategoryId;
    @SerializedName("provider_id") public String providerId;
    @SerializedName("ad_pics") public List<AdPics> adPics;

    public Category category;
    public SubCategory subCategory;
    public Map<Integer, String> picsBase64 = new HashMap<>();

    public int contadorFotos;
    public String mipmapid;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    public String isValidForm() {
        if(this.categoryId == 0 ) return "Debes seleccionar una categoría";
        if(this.subCategoryId == 0 ) return "Debes seleccionar una subcategoría";
        if(this.name.trim().length() < 5 ) return "Debes ingresar un titulo de anuncio mayor a 5 caracteres";
        if(this.description.trim().length() < 10 ) return "Debes ingresar una descripción de anuncio mayor a 10 caracteres";
        if(this.price.trim().length() < 4 ) return "Debes especificar el precio (4 caracteres como mínimo)";
        if(contadorFotos == 0) return "Debes ingresar al menos una foto";
        return "true";
    }

}
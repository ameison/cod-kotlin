package app.conectados.android.models;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;


@SuppressLint("ParcelCreator")
public class Provider implements Parcelable {

    @SerializedName("id") public Integer id;
    @SerializedName("firstname") public String firstname;
    @SerializedName("surname") public String surname;
    @SerializedName("phone") public String phone;
    @SerializedName("rating") public String rating;

    @SerializedName("login_type") public String login_type;
    @SerializedName("email") public String email;
    @SerializedName("password") public String password;
    @SerializedName("device_id") public String device_id;
    @SerializedName("device_type") public String device_type;

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {}

    @Override
    public String toString() {
        return "Provider{" +
                "id=" + id +
                ", firstname='" + firstname + '\'' +
                ", surname='" + surname + '\'' +
                ", phone='" + phone + '\'' +
                ", rating='" + rating + '\'' +
                ", login_type='" + login_type + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", device_id='" + device_id + '\'' +
                ", device_type='" + device_type + '\'' +
                '}';
    }
}

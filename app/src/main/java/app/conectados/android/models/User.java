package app.conectados.android.models;

public class User {

    private String firstname;
    private String surname;
    private String plateNumber;
    private String deviceMame;
    private String firebaseToken;

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getDeviceMame() {
        return deviceMame;
    }

    public void setDeviceMame(String deviceMame) {
        this.deviceMame = deviceMame;
    }

    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }
}

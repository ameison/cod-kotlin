package app.conectados.android.custom.dialogs;

import app.conectados.android.R;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class GeolitoDialogCustom extends Dialog {

    public Activity c;
    public Dialog d;
    public Button btn_listo;

    public GeolitoDialogCustom(Activity a) {
        super(a);
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_geolito);
        btn_listo = findViewById(R.id.btn_listo);
        btn_listo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeolitoDialogCustom.this.dismiss();
            }
        });
    }


}
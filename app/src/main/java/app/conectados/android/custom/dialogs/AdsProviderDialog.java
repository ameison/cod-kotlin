package app.conectados.android.custom.dialogs;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;

import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import app.conectados.android.models.Ad;
import app.conectados.android.models.Provider;

public class AdsProviderDialog extends DialogFragment {

    private ArrayList<Ad> listaAds = new ArrayList<>();
    private Provider provider = new Provider();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final RelativeLayout root = new RelativeLayout(getActivity());
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        if(getArguments() != null){
            listaAds = getArguments().getParcelableArrayList("listaAds");
            provider = getArguments().getParcelable("provider");
        }

        AdsProviderDialogCustom dialog = new AdsProviderDialogCustom(getActivity(), provider, listaAds);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(root);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return dialog;
    }

}
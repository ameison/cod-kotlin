package app.conectados.android.custom.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import java.util.List;

import app.conectados.android.R;
import app.conectados.android.models.SubCategory;
import app.conectados.android.utils.ViewUtils;
import app.conectados.android.custom.adapters.SubCategoryListAdapter;

public class SubCategoryDialogRadio extends DialogFragment {

    private String title = null;
    private SubCategory subCategory = null;
    private List<SubCategory> listaSubCategories;
    private AlertPositiveListener alertPositiveListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        b.setTitle(title);
        SubCategoryListAdapter myAdapter = new SubCategoryListAdapter(getActivity(), listaSubCategories );
        myAdapter.onCheckListener(new SubCategoryListAdapter.CheckListener() {
            @Override
            public void onChecked(SubCategory subcategory) {
                SubCategoryDialogRadio.this.subCategory = subcategory ;
            }
        });

        b.setSingleChoiceItems(myAdapter, -1, null);
        b.setNegativeButton(R.string.cancelar, null);
        b.setPositiveButton(R.string.aceptar, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(subCategory != null){
                    alertPositiveListener.onPositiveClick(subCategory);
                }else{
                    ViewUtils.message(getContext(), getString(R.string.debe_seleccionar_una_cat));
                }
            }
        });

        return b.create();
    }

    public interface AlertPositiveListener {
        void onPositiveClick(SubCategory subcategory);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setListaSubCategories(List<SubCategory> listaSubCategories) {
        this.listaSubCategories = listaSubCategories;
    }

    public void setAlertPositiveListener(AlertPositiveListener alertPositiveListener) {
        this.alertPositiveListener = alertPositiveListener;
    }

}
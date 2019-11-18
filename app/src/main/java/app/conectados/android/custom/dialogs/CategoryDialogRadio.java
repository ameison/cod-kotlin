package app.conectados.android.custom.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import androidx.fragment.app.DialogFragment;
import java.util.List;
import app.conectados.android.R;
import app.conectados.android.models.Category;
import app.conectados.android.utils.ViewUtils;
import app.conectados.android.custom.adapters.CategoryListAdapter;

public class CategoryDialogRadio extends DialogFragment {

    private String TAG = this.getClass().getSimpleName() ;
    private String title = null;
    private Category category = null;
    private List<Category> listaCategories;
    private AlertPositiveListener alertPositiveListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        b.setTitle(title);

        if(getArguments() != null){
            category = getArguments().getParcelable("categorySelected");
            if(category != null){
                Log.d(TAG, "category >>> "+category.toString());
            }
        }

        CategoryListAdapter myAdapter = new CategoryListAdapter(getActivity(), listaCategories );
        myAdapter.onCheckListener(new CategoryListAdapter.CheckListener() {
            @Override
            public void onChecked(Category category) {
                CategoryDialogRadio.this.category = category ;
            }
        });

        if(category != null){
            Log.d(TAG, "categorySelected != null ");
            b.setSingleChoiceItems(myAdapter, category.id - 1, null);
        }else{
            b.setSingleChoiceItems(myAdapter, -1, null);
        }


        b.setNegativeButton(R.string.cancelar, null);
        b.setPositiveButton(R.string.aceptar, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(category != null){
                    alertPositiveListener.onPositiveClick(category);
                }else{
                    ViewUtils.message(getContext(), getString(R.string.debe_seleccionar_una_cat));
                }
            }
        });

        return b.create();
    }

    public interface AlertPositiveListener {
        void onPositiveClick(Category category);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setListaCategories(List<Category> listaCategories) {
        this.listaCategories = listaCategories;
    }

    public void setAlertPositiveListener(AlertPositiveListener alertPositiveListener) {
        this.alertPositiveListener = alertPositiveListener;
    }

}
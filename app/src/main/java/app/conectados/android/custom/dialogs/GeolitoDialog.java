package app.conectados.android.custom.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import app.conectados.android.models.Category;

public class GeolitoDialog extends DialogFragment {

    private String title = null;
    private Category category = null;
    private AlertPositiveListener alertPositiveListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setStyle(DialogFragment.STYLE_NO_FRAME, 0);
        GeolitoDialogCustom cc = new GeolitoDialogCustom(getActivity());
        return cc;
    }

    public interface AlertPositiveListener {
        void onPositiveClick(Category category);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAlertPositiveListener(AlertPositiveListener alertPositiveListener) {
        this.alertPositiveListener = alertPositiveListener;
    }

}
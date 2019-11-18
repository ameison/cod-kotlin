package app.conectados.android.views.about;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import app.conectados.android.BuildConfig;
import app.conectados.android.R;
import app.conectados.android.utils.ViewUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutAct extends AppCompatActivity {

    @BindView(R.id.aboutText) TextView aboutText;
    @BindView(R.id.versioName) TextView versioName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(getText(R.string.op_acercade));
    }

    @Override
    protected void onStart() {
        super.onStart();
        aboutText.setText(R.string.about  );
        versioName.setText(BuildConfig.VERSION_NAME);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ViewUtils.hideKeyboard(this);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}

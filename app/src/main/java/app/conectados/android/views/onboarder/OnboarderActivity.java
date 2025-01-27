package app.conectados.android.views.onboarder;

import android.animation.ArgbEvaluator;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import app.conectados.android.R;
import java.util.List;


public abstract class OnboarderActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {
    private Integer[] colors;
    private CircleIndicatorView circleIndicatorView;
    private ViewPager viewPagerOnboarder;
    private OnboarderPagerAdapter onboarderAdapter;
    private Button btnSkip, btnFinish, btnNext;
    private FrameLayout buttonsLayout;
    private FloatingActionButton fab;
    private View divider;
    private ArgbEvaluator evaluator;

    private boolean shouldDarkenButtonsLayout = false;
    private boolean shouldUseFloatingActionButton = false;
    private OnboarderPageChangeListener onboarderPageChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarder);
        setStatusBackgroundColor();
        hideActionBar();

        circleIndicatorView = findViewById(R.id.indicator_circle);
        btnNext = findViewById(R.id.button_next);
        btnSkip = findViewById(R.id.button_skip);
        btnFinish = findViewById(R.id.button_finish);
        buttonsLayout = findViewById(R.id.layout_buttons);
        fab = findViewById(R.id.fab);
        divider = findViewById(R.id.divider);
        viewPagerOnboarder = findViewById(R.id.viewpager_onboarder);

        viewPagerOnboarder.addOnPageChangeListener(this);
        btnNext.setOnClickListener(this);
        btnSkip.setOnClickListener(this);
        btnFinish.setOnClickListener(this);
        fab.setOnClickListener(this);
        evaluator = new ArgbEvaluator();
    }

    public void initOnboardingPages(List<OnboarderPage> pages) {
        onboarderAdapter = new OnboarderPagerAdapter(this, pages);
        viewPagerOnboarder.setAdapter(onboarderAdapter);
        colors = ColorsArrayBuilder.getPageBackgroundColors(this, pages);
        circleIndicatorView.setPageIndicators(pages.size());
    }

    public void setInactiveIndicatorColor(int color) {
        this.circleIndicatorView.setInactiveIndicatorColor(color);
    }

    public void setActiveIndicatorColor(int color) {
        this.circleIndicatorView.setActiveIndicatorColor(color);
    }

    public void shouldDarkenButtonsLayout(boolean shouldDarkenButtonsLayout) {
        this.shouldDarkenButtonsLayout = shouldDarkenButtonsLayout;
    }

    public void setDividerColor(@ColorInt int color) {
        if (!this.shouldDarkenButtonsLayout)
            this.divider.setBackgroundColor(color);
    }

    public void setDividerHeight(int heightInDp) {
        if (!this.shouldDarkenButtonsLayout)
            this.divider.getLayoutParams().height =
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, heightInDp,
                            getResources().getDisplayMetrics());
    }

    public void setDividerVisibility(int dividerVisibility) {
        this.divider.setVisibility(dividerVisibility);
    }

    private int darkenColor(@ColorInt int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.9f;
        return Color.HSVToColor(hsv);
    }

    public void setStatusBackgroundColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black_transparent));
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        boolean isInLastPage = viewPagerOnboarder.getCurrentItem() == onboarderAdapter.getCount() - 1;
        if (i == R.id.button_next || i == R.id.fab && !isInLastPage) {
            viewPagerOnboarder.setCurrentItem(viewPagerOnboarder.getCurrentItem() + 1);
        } else if (i == R.id.button_skip) {
            onSkipButtonPressed();
        } else if (i == R.id.button_finish || i == R.id.fab && isInLastPage) {
            onFinishButtonPressed();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (position < (onboarderAdapter.getCount() - 1) && position < (colors.length - 1)) {
            viewPagerOnboarder.setBackgroundColor((Integer) evaluator.evaluate(positionOffset, colors[position], colors[position + 1]));
            if (shouldDarkenButtonsLayout) {
                buttonsLayout.setBackgroundColor(darkenColor((Integer) evaluator.evaluate(positionOffset, colors[position], colors[position + 1])));
                divider.setVisibility(View.GONE);
            }
        } else {
            viewPagerOnboarder.setBackgroundColor(colors[colors.length - 1]);
            if (shouldDarkenButtonsLayout) {
                buttonsLayout.setBackgroundColor(darkenColor(colors[colors.length - 1]));
                divider.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onPageSelected(int position) {
        int lastPagePosition = onboarderAdapter.getCount() - 1;
        circleIndicatorView.setCurrentPage(position);
        btnNext.setVisibility(position == lastPagePosition && !this.shouldUseFloatingActionButton ? View.GONE : View.VISIBLE);
        btnFinish.setVisibility(position == lastPagePosition && !this.shouldUseFloatingActionButton ? View.VISIBLE : View.GONE);
        //if (this.shouldUseFloatingActionButton)
        //    this.fab.setImageResource(position == lastPagePosition ? R.drawable.ic_done_white_24dp : R.drawable.ic_arrow_forward_white_24dp);
        if (onboarderPageChangeListener != null)
            onboarderPageChangeListener.onPageChanged(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void hideActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    public void setOnboarderPageChangeListener(OnboarderPageChangeListener onboarderPageChangeListener) {
        this.onboarderPageChangeListener = onboarderPageChangeListener;
    }

    //<editor-fold desc="Buttons">
    public void setSkipButtonTitle(CharSequence title) {
        this.btnSkip.setText(title);
    }

    public void setSkipButtonHidden() {
        this.btnSkip.setVisibility(View.GONE);
    }

    public void setSkipButtonTitle(@StringRes int titleResId) {
        this.btnSkip.setText(titleResId);
    }

    public void setFinishButtonTitle(CharSequence title) {
        this.btnFinish.setText(title);
    }

    public void setFinishButtonTitle(@StringRes int titleResId) {
        this.btnFinish.setText(titleResId);
    }

    public void shouldUseFloatingActionButton(boolean shouldUseFloatingActionButton) {
        this.shouldUseFloatingActionButton = shouldUseFloatingActionButton;

        if (shouldUseFloatingActionButton) {
            //this.fab.setVisibility(View.VISIBLE);
            this.setDividerVisibility(View.GONE);
            this.shouldDarkenButtonsLayout(false);
            this.btnFinish.setVisibility(View.GONE);
            this.btnSkip.setVisibility(View.GONE);
            this.btnNext.setVisibility(View.GONE);
            this.btnNext.setFocusable(false);
            this.buttonsLayout.getLayoutParams().height =
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 96,
                            getResources().getDisplayMetrics());
        }
    }

    public void setPageTransformer(ViewPager.PageTransformer pageTransformer) {
        viewPagerOnboarder.setPageTransformer(true, pageTransformer);
    }

    public void setFinishButtonTextColor(@ColorRes int color) {
        this.btnFinish.setTextColor(ContextCompat.getColor(this, color));
    }

    public void setNextButtonTextColor(@ColorRes int color) {
        this.btnNext.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        this.btnNext.setTextColor(ContextCompat.getColor(this, color));
    }

    public void setSkipButtonTextColor(@ColorRes int color) {
        this.btnSkip.setTextColor(ContextCompat.getColor(this, color));
    }

    public void setFinishButtonBackgroundColor(@ColorRes int color) {
        this.btnFinish.setBackgroundColor(ContextCompat.getColor(this, color));
    }

    public void setSkipButtonBackgroundColor(@ColorRes int color) {
        this.btnSkip.setBackgroundColor(ContextCompat.getColor(this, color));
    }

    public void setNextButtonBackgroundColor(@ColorRes int color) {
        this.btnNext.setBackgroundColor(ContextCompat.getColor(this, color));
    }

    public void setNextButtonTitle(CharSequence title) {
        this.btnNext.setText(title);
        this.btnNext.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }

    public void setNextButtonTitle(@StringRes int titleResId) {
        this.btnNext.setText(titleResId);
        this.btnNext.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }

    public void setNextButtonIcon(@DrawableRes int drawableResId) {
        this.btnNext.setText(null);
        this.btnNext.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, drawableResId);
    }

    protected void onSkipButtonPressed() {
        viewPagerOnboarder.setCurrentItem(onboarderAdapter.getCount());
    }

    abstract public void onFinishButtonPressed();

    //</editor-fold>

}

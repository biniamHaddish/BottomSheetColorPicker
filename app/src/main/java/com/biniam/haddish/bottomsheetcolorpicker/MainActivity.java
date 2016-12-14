package com.biniam.haddish.bottomsheetcolorpicker;

import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import com.biniam.haddish.bscolorpickerlib.ColorCollection;
import com.biniam.haddish.bscolorpickerlib.ColorPickerBottomSheetDialogFragment;
import com.biniam.haddish.bscolorpickerlib.ColorPickerUtil;
import com.biniam.haddish.bscolorpickerlib.ColorStateDrawable;

public class MainActivity extends AppCompatActivity implements ColorPickerUtil.OnColorSelectedListener {


    private RelativeLayout settingRelativeLayout;
    private ColorCollection colorCollection;
    private int mSelectedColor;
    private int[] mColors ;
    private ColorPickerUtil.OnColorSelectedListener  onColorSelectedListener;
    ColorPickerBottomSheetDialogFragment    dialogFragment;
    private SharedPreferences mCustomPrefernce;
    public static String CUSTOM_PREFERNCES = "CUSTOM_PREFERNCES";
    private int  Themecolor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        colorCollection = new ColorCollection();
        mColors = colorCollection.First_Collection;
        /*mSelectedColor = colorCollection.First_Collection[0];*/
        mCustomPrefernce = getSharedPreferences(CUSTOM_PREFERNCES, 0);
        mSelectedColor    = mCustomPrefernce.getInt(getString(R.string.pref_theme_color_key), colorCollection.First_Collection[0]);
        Themecolor        = mCustomPrefernce.getInt(getString(R.string.pref_theme_color_key), colorCollection.First_Collection[0]);
        ChangeTheme(Themecolor);
        onColorSelectedListener = this;
        settingRelativeLayout=(RelativeLayout)findViewById(R.id.themRelativeLayout);
        settingRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intBottomSheetDialog();
            }
        });

    }
    private void ChangeTheme(@NonNull  int color){
        if (getSupportActionBar() != null)
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ColorStateDrawable.getPressedColor(color));
            getWindow().setNavigationBarColor(color);
        }

    }
    @Override
    public void onColorSelected(@NonNull int color){
        mCustomPrefernce.edit().putInt(getString(R.string.pref_theme_color_key), color).apply();
        mSelectedColor= color;
            ChangeTheme(color);

    }

    @Override
    protected void onResume() {
        super.onResume();
        onColorSelectedListener=this;
        if (dialogFragment!=null) {
            dialogFragment.setOnColorSelectedListener(onColorSelectedListener);
        }
        mCustomPrefernce.getInt(getString(R.string.pref_theme_color_key), colorCollection.First_Collection[0]);
    }


    public void intBottomSheetDialog(){
        dialogFragment = ColorPickerBottomSheetDialogFragment.newInstance(
                R.string.ColorPickerTitle,
                mColors,mSelectedColor
                ,5
                ,1);
        dialogFragment.setTitleColor(mSelectedColor);
        dialogFragment.setOnColorSelectedListener(onColorSelectedListener);
        dialogFragment.show(getSupportFragmentManager(), dialogFragment.getTag());
    }


}

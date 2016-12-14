package com.biniam.haddish.bscolorpickerlib;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.UiThread;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.widget.TextView;

/**
 * Created by biniam on 12/11/16.
 */

public class ColorPickerBottomSheetDialogFragment extends BottomSheetDialogFragment implements ColorPickerUtil.OnColorSelectedListener {


    private Dialog dialog;
    private int style;
    private ColorCollection colorCollection;
    protected BottomSheetDialog mbottomSheetDialog;
    protected String[] mColorContentDescriptions = null;

    protected int mSelectedColor;
    private int[] mColors;
    private ColorPaletteTable colorPaletteTable;
    protected static final String KEY_SELECTED_COLOR = "selected_color";
    protected static final String KEY_TITLE_ID = "title_id";
    protected static final String KEY_COLUMNS = "columns";
    protected static final String KEY_SIZE = "size";
    protected static final String KEY_COLORS = "colors";
    protected int COLOR_CHOOSER_RES_TITLE = R.string.ColorPickerTitle;
    protected static final String KEY_TITLE_COLOR = "title_color";
    protected static final String KEY_CLICK_LISTNER = "click_listner";
    public static final int SIZE_LARGE = 1;
    public static final int SIZE_SMALL = 2;
    protected int mColumns;
    protected int mSize;
    private TextView ColorPickerTitle;
    View contentView;
    protected String title;
    private int rTitle;
    protected int titleColor;
    protected ColorPickerUtil.OnColorSelectedListener mListener;

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback
            = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }

        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {

        }
    };

    public ColorPickerBottomSheetDialogFragment() {
        // Empty Constructor
    }

    public static ColorPickerBottomSheetDialogFragment newInstance(int titleResId,
                                                                   int[] colors,
                                                                   int selectedColor,
                                                                   int columns,
                                                                   int size) {
        ColorPickerBottomSheetDialogFragment ret = new ColorPickerBottomSheetDialogFragment();
        ret.initialize(titleResId, colors, selectedColor, columns, size);

        return ret;
    }

    public void initialize(int titleResId, int[] colors, int selectedColor, int columns, int size) {
        setArguments(titleResId, selectedColor, columns, size);
        setColors(colors, selectedColor);
        initTitle(titleResId);
    }

    private void initTitle(@NonNull int title) {
        this.rTitle = title;
    }

    public void setColors(int[] colors, int selectedColor) {
        if (mColors != colors || mSelectedColor != selectedColor) {
            mColors = colors;
            mSelectedColor = selectedColor;
            refreshPalette();
        }
    }

    public void setArguments(int titleResId, int mselectedColor, int columns, int size) {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_TITLE_ID, titleResId);
        bundle.putInt(KEY_COLUMNS, columns);
        bundle.putInt(KEY_SIZE, size);
        bundle.putInt(KEY_TITLE_COLOR, mselectedColor);
        setArguments(bundle);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            COLOR_CHOOSER_RES_TITLE = getArguments().getInt(KEY_TITLE_ID);
            mColumns = getArguments().getInt(KEY_COLUMNS);
            mSize = getArguments().getInt(KEY_SIZE);
            mSelectedColor = getArguments().getInt(KEY_TITLE_COLOR);

        }
        if (savedInstanceState != null && mSelectedColor != 0) {
            mColors = savedInstanceState.getIntArray(KEY_COLORS);
            mSelectedColor = (Integer) savedInstanceState.getSerializable(KEY_TITLE_COLOR);
        }
    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    public void setOnColorSelectedListener(ColorPickerUtil.OnColorSelectedListener listener) {
        mListener = listener;
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        this.style = style;
        this.dialog = dialog;
        initBottomsheet();
    }

    private void initBottomsheet(){
        colorCollection = new ColorCollection();
        mColors = ColorCollection.First_Collection;

        contentView = View.inflate(getContext(), R.layout.bottom_sheet_fragment, null);
        /*get the view Reference */

        dialog.setContentView(contentView);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        /*we have to create the color table here*/
        colorPaletteTable = (ColorPaletteTable) contentView.findViewById(R.id.color_picker);
        ColorPickerTitle = (TextView) contentView.findViewById(R.id.Text_View_Color_picker_Title);

            /*setting the Title here*/
        if (title != null && titleColor != 0) {
            ColorPickerTitle.setText(title);
            ColorPickerTitle.setTextColor(titleColor);
        } else if (rTitle != 0 && titleColor != 0) {
            ColorPickerTitle.setText(rTitle);
            ColorPickerTitle.setTextColor(titleColor);
        }
        colorPaletteTable.init(mSize, mColumns, this);
        if (mColors != null) {
            showPaletteView();
        }
        /*end of the Color board */
        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
    }

    @UiThread

    public void setTitle(@NonNull CharSequence title) {
        if (title != null) {
            this.title = (String) title;
        }
    }

    @UiThread
    public void setTitle(@StringRes int title) {
        this.rTitle = title;
    }
    @UiThread
    public void setTitleColor(int color) {
        this.titleColor = color;

    }

    private void showPaletteView() {
        refreshPalette();
        colorPaletteTable.setVisibility(View.VISIBLE);
    }

    private void refreshPalette() {
        if (colorPaletteTable != null && mColors != null) {
            colorPaletteTable.drawPalette(mColors, mSelectedColor, mColorContentDescriptions);
        }
    }

    @Override
    public void onColorSelected(int color) {

        if (mListener != null) {
            mListener.onColorSelected(color);
        }

        if (getTargetFragment() instanceof ColorPickerUtil.OnColorSelectedListener) {
            final ColorPickerUtil.OnColorSelectedListener listener =
                    (ColorPickerUtil.OnColorSelectedListener) getTargetFragment();
            listener.onColorSelected(color);
        }

        if (color != mSelectedColor) {
            mSelectedColor = color;
            // Redraw palette to show checkmark on newly selected color before dismissing.
            colorPaletteTable.drawPalette(mColors, mSelectedColor);
        }

        dismiss();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_COLORS, titleColor);
        outState.putSerializable(KEY_SELECTED_COLOR, mColors);
        outState.putSerializable(KEY_TITLE_ID, COLOR_CHOOSER_RES_TITLE);
        outState.putSerializable(KEY_TITLE_COLOR, this.titleColor);

    }

    /*test Ground here below*/
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            titleColor = (Integer) savedInstanceState.getSerializable(KEY_TITLE_COLOR);
            ColorPickerTitle.setTextColor(titleColor);
        }
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initBottomsheet();
    }

}

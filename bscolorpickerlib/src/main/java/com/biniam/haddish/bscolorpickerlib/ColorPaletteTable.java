package com.biniam.haddish.bscolorpickerlib;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

/**
 * Created by biniam on 12/11/16.
 */

public class ColorPaletteTable extends TableLayout{

    public ColorPickerUtil.OnColorSelectedListener mOnColorSelectedListener;

    private String mDescription;
    private String mDescriptionSelected;

    private int mSwatchLength;
    private int mMarginSize;
    private int mNumColumns;

    public ColorPaletteTable(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ColorPaletteTable(Context context) {
        super(context);
    }

    /**
     * Initialize the size, columns, and listener.  Size should be a pre-defined size (SIZE_LARGE
     * or SIZE_SMALL) from .
     */
    public void init(int size, int columns, ColorPickerUtil.OnColorSelectedListener listener) {
        mNumColumns = columns;
        Resources res = getResources();
        if (size == ColorPickerBottomSheetDialogFragment.SIZE_SMALL) {
            mSwatchLength = res.getDimensionPixelSize(R.dimen.color_swatch_large);
            mMarginSize   = res.getDimensionPixelSize(R.dimen.color_swatch_margins_large);
        } else {
            mSwatchLength = res.getDimensionPixelSize(R.dimen.color_swatch_small);
            mMarginSize   = res.getDimensionPixelSize(R.dimen.color_swatch_margins_small);
        }
        mOnColorSelectedListener = listener;

        mDescription = res.getString(R.string.color_swatch_description);
        mDescriptionSelected = res.getString(R.string.color_swatch_description_selected);
    }

    private TableRow createTableRow() {
        TableRow row = new TableRow(getContext());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT);
        row.setLayoutParams(params);
        return row;
    }


    public void drawPalette(int[] colors, int selectedColor) {
        drawPalette(colors, selectedColor, null);
    }


    public void drawPalette(int[] colors, int selectedColor, String[] colorContentDescriptions) {
        if (colors == null) {
            return;
        }

        this.removeAllViews();
        int tableElements = 0;
        int rowElements = 0;
        int rowNumber = 0;

        // Fills the table with swatches based on the array of colors.
        TableRow row = createTableRow();
        for (int color : colors) {
            View colorSwatch = createColorSwatch(color, selectedColor);
            setSwatchDescription(rowNumber, tableElements, rowElements, color == selectedColor, colorSwatch, colorContentDescriptions);
            addSwatchToRow(row, colorSwatch, rowNumber);

            tableElements++;
            rowElements++;
            if (rowElements == mNumColumns) {
                addView(row);
                row = createTableRow();
                rowElements = 0;
                rowNumber++;
            }
        }

        // Create blank views to fill the row if the last row has not been filled.
        if (rowElements > 0) {
            while (rowElements != mNumColumns) {
                addSwatchToRow(row, createBlankSpace(), rowNumber);
                rowElements++;
            }
            addView(row);
        }
    }

    /**
     * Add a content description to the specified swatch view. Because the colors get added in a
     * snaking form, every other row will need to compensate for the fact that the colors are added
     * in an opposite direction from their left->right/top->bottom order, which is how the system
     * will arrange them for accessibility purposes.
     */
    private void setSwatchDescription(int rowNumber, int index, int rowElements, boolean selected,
                                      View swatch, String[] contentDescriptions) {
        String description;
        if (contentDescriptions != null && contentDescriptions.length > index) {
            description = contentDescriptions[index];
        } else {
            int accessibilityIndex;
            if (rowNumber % 2 == 0) {
                // We're in a regular-ordered row
                accessibilityIndex = index + 1;
            } else {
                // We're in a backwards-ordered row.
                int rowMax = ((rowNumber + 1) * mNumColumns);
                accessibilityIndex = rowMax - rowElements;
            }

            if (selected) {
                description = String.format(mDescriptionSelected, accessibilityIndex);
            } else {
                description = String.format(mDescription, accessibilityIndex);
            }
        }
        swatch.setContentDescription(description);
    }


    private static void addSwatchToRow(TableRow row, View swatch, int rowNumber) {
        if (rowNumber % 2 == 0) {
            row.addView(swatch);
        } else {
            row.addView(swatch, 0);
        }
    }

    /**
     * Creates a blank space to fill the row.
     */
    private ImageView createBlankSpace() {
        ImageView view = new ImageView(getContext());
        TableRow.LayoutParams params = new TableRow.LayoutParams(mSwatchLength, mSwatchLength);
        params.setMargins(mMarginSize, mMarginSize, mMarginSize, mMarginSize);
        view.setLayoutParams(params);
        return view;
    }

    /**
     * Creates a color swatch.
     */
    private ColorPickerUtil createColorSwatch(int color, int selectedColor) {
        ColorPickerUtil view = new ColorPickerUtil(getContext(), color,
                color == selectedColor, mOnColorSelectedListener);
        TableRow.LayoutParams params = new TableRow.LayoutParams(mSwatchLength, mSwatchLength);
        params.setMargins(mMarginSize, mMarginSize, mMarginSize, mMarginSize);
        view.setLayoutParams(params);
        return view;
    }

}

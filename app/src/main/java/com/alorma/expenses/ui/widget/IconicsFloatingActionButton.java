package com.alorma.expenses.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;

import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;

/**
 * Created by bernat.borras on 31/12/15.
 */
public class IconicsFloatingActionButton extends FloatingActionButton {
    public IconicsFloatingActionButton(Context context) {
        super(context);
        init();
    }

    public IconicsFloatingActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IconicsFloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            setImageDrawable(new IconicsDrawable(getContext())
                    .icon(MaterialDesignIconic.Icon.gmi_plus)
                    .color(Color.WHITE)
                    .paddingDp(2));
        }
    }

}

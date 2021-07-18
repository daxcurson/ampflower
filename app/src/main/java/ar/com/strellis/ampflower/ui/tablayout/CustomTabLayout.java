package ar.com.strellis.ampflower.ui.tablayout;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import ar.com.strellis.ampflower.Config;
import ar.com.strellis.ampflower.R;
import ar.com.strellis.ampflower.data.ThemePref;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import static ar.com.strellis.ampflower.ui.Constant.THEME_LIGHT;
public class CustomTabLayout extends SmartTabLayout {

    public CustomTabLayout(Context context) {
        super(context);
    }

    public CustomTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTabLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected TextView createDefaultTabView(CharSequence title) {
        TextView textView = super.createDefaultTabView(title);
        ThemePref themePref = new ThemePref(getContext());
        Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.custom_font);
        textView.setTypeface(typeface);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.tab_text_size));
        if (Config.ENABLE_RTL_MODE) {
            textView.setPadding(36, 24, 36, 24);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 0);
            textView.setLayoutParams(params);
        }
        if (themePref.getCurrentTheme() == THEME_LIGHT) {
            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.tabTextColor));
        }
        return textView;
    }

}

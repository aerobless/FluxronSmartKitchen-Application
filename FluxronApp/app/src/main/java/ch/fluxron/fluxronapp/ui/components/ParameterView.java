package ch.fluxron.fluxronapp.ui.components;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import ch.fluxron.fluxronapp.R;

public class ParameterView extends RelativeLayout {

    public ParameterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.component_parameter_view, this, true);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ParameterView);
        String myText = a.getString(R.styleable.ParameterView_paramName);
        

    }

    public ParameterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.component_parameter_view, this, true);
    }
}

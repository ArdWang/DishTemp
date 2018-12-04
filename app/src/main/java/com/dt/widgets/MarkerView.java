
package com.dt.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;

import com.dt.R;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;


/**
 * Custom implementation of the MarkerView.
 * 
 * @author Philipp Jahoda
 */
public class MarkerView extends com.github.mikephil.charting.components.MarkerView {

    private TextView tvContent;

    public MarkerView(Context context, int layoutResource) {
        super(context, layoutResource);

        tvContent = findViewById(R.id.tvContent);
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @SuppressLint("SetTextI18n")
    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        if (e instanceof CandleEntry) {
            CandleEntry ce = (CandleEntry) e;
            //原来的
            tvContent.setText("" + Utils.formatNumber(ce.getHigh(), 1, false));
        } else {
            //后面修改的
            tvContent.setText("" + e.getY());
        }

        super.refreshContent(e, highlight);
    }




    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}

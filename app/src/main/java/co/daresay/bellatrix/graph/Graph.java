package co.daresay.bellatrix.graph;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import co.daresay.bellatrix.MainActivity;
import co.daresay.bellatrix.R;

public class Graph extends LinearLayout {

    public OnBarClickListener onBarClickListener;

    public Graph(Context context) {
        this(context, null);
    }

    public Graph(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(HORIZONTAL);

        if (isInEditMode()) {
            MainActivity.initGraph(this);
        }
    }

    public void setData(List<GraphData> data) {
        removeAllViews();
        for (int i = 0; i < data.size(); ++i) {
            addItem(data.get(i), i);
        }
    }

    private void addItem(final GraphData graphData, final int position) {
        final View item = createItem();

        item.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setSelectedItem(position, graphData);
            }
        });

        float graphHeightPx = getResources().getDisplayMetrics().density * graphHeightDp;
        float barHeight = graphHeightPx * graphData.value;

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, (int) barHeight);
        lp.gravity = Gravity.BOTTOM;
        lp.weight = 1;
        this.addView(item, lp);

        TextView barLabel = item.findViewById(R.id.bar_label);
        barLabel.setText(graphData.label);

        ImageView barBar = item.findViewById(R.id.bar);

        barBar.setImageResource(R.color.bar_color);
    }

    public void setSelectedItem(int position, GraphData graphData) {

        for (int i = 0; i <getChildCount(); ++i) {
            ImageView bar = getChildAt(i).findViewById(R.id.bar);
            if (i == position) {
                bar.setImageResource(R.color.bar_color_selected);
            } else {
                bar.setImageResource(R.color.bar_color);
            }
        }

        if (onBarClickListener != null) {
            onBarClickListener.onBarClick(graphData, position);
        }

    }

    private View createItem() {
        return LayoutInflater.from(getContext()).inflate(R.layout.bar, this, false);
    }

    int graphHeightDp = 98;

}

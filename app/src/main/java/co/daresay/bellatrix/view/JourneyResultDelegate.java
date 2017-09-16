package co.daresay.bellatrix.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.isalldigital.reclaim.AdapterDelegate;
import com.isalldigital.reclaim.DisplayableCell;
import com.isalldigital.reclaim.annotations.ReclaimAdapterDelegate;

import co.daresay.bellatrix.R;


@ReclaimAdapterDelegate(JourneyResultDelegate.Cell.class)
public class JourneyResultDelegate extends AdapterDelegate {

    public static class Cell implements DisplayableCell {
        public String text1;
        public String text2;
        public View.OnClickListener onClickListener;

        public Cell(String text1, String text2, View.OnClickListener l) {
            this.text1 = text1;
            this.text2 = text2;
            this.onClickListener = l;
        }
    }

    static class ViewHolder extends ViewHolderDelegate {
        TextView left;
        TextView right;

        public ViewHolder(View itemView) {
            super(itemView);
            left = (TextView) itemView.findViewById(R.id.left);
            right = (TextView) itemView.findViewById(R.id.right);
        }

        @Override
        public boolean needsItemDecoration() {
            return false;
        }
    }

    @NonNull
    @Override
    public ViewHolderDelegate onCreateViewHolder(LayoutInflater inflater, ViewGroup parent) {
        return new ViewHolder(inflater.inflate(R.layout.adapter_delegate_journey_result, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DisplayableCell item, @NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder vh = (ViewHolder) holder;
        Cell cell = (Cell) item;
        vh.left.setText(cell.text1);
        vh.right.setText(cell.text2);
//        vh.button.setOnClickListener(cell.onClickListener);
    }
}
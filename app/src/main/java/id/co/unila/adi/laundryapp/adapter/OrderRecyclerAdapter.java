package id.co.unila.adi.laundryapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.co.unila.adi.laundryapp.R;
import id.co.unila.adi.laundryapp.helpers.OnItemClickListener;
import id.co.unila.adi.laundryapp.model.PersonOrder;

/**
 * Created by japra_awok on 14/03/2017.
 */

public class OrderRecyclerAdapter extends RecyclerView.Adapter<OrderRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<PersonOrder> oderList;
    private OnItemClickListener clickListener;

    public void setClickListener(OnItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public OrderRecyclerAdapter(Context context, List<PersonOrder> oderList) {
        this.context = context;
        this.oderList = oderList;
    }

    @Override
    public OrderRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.person_order_item_row, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(OrderRecyclerAdapter.ViewHolder holder, int position) {
        PersonOrder personOrder = oderList.get(position);
        holder.tvOrderId.setText(String.valueOf(personOrder.id));
        holder.tvStatus.setText(personOrder.status);
        holder.tvOrderDate.setText(personOrder.created_at);
    }

    @Override
    public int getItemCount() {
        return oderList == null ? 0 : oderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.tvOrderId) TextView tvOrderId;
        @BindView(R.id.tvStatus) TextView tvStatus;
        @BindView(R.id.tvOrderDate) TextView tvOrderDate;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onClick(view, getAdapterPosition());
        }
    }
}

package com.android.therevgo.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.therevgo.R;
import com.android.therevgo.database.ContactBean;

import java.util.List;

/**
 * Created by shubham on 27/11/16.
 */
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder>{

    private final List<ContactBean> mValues;
    private final OnItemLongClickListener mListener ;

    public interface OnItemLongClickListener {
        void onItemLongClick(int pos, View view);
    }

    public ContactAdapter(List<ContactBean> mValues, OnItemLongClickListener mListner) {
        this.mValues = mValues;
        this.mListener = mListner ;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_info, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.mBean = mValues.get(position);
        holder.mNameView.setText(mValues.get(position).getName());
        holder.mNumberView.setText(mValues.get(position).getNumber());

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mListener.onItemLongClick(position,v);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public final TextView mNumberView;
        public ContactBean mBean ;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.name);
            mNumberView = (TextView) view.findViewById(R.id.no);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }
}

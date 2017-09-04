package com.darvin.cmritattendancemanager.adapters;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.darvin.cmritattendancemanager.R;
import com.darvin.cmritattendancemanager.fragments.InternalFragment;


public class InternalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private Cursor mCursor;

    public InternalAdapter() {
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View menuItemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.row_internal, parent, false);
        return new InternalHolder(menuItemLayoutView);    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        InternalHolder internalHolder = (InternalHolder) holder;
        internalHolder.subjectName.setText(mCursor.getString(InternalFragment.INDEX_SUBJECT));
        internalHolder.ia1.setText(mCursor.getString(InternalFragment.INDEX_IA1));
        internalHolder.ia2.setText(mCursor.getString(InternalFragment.INDEX_IA2));
        internalHolder.ia3.setText(mCursor.getString(InternalFragment.INDEX_IA3));
        internalHolder.total.setText(mCursor.getString(InternalFragment.INDEX_TOTAL));
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }
    private class InternalHolder extends RecyclerView.ViewHolder {
        TextView subjectName, ia1, ia2, ia3, total;

        InternalHolder(View v) {
            super(v);
            subjectName =  v.findViewById(R.id.rw_subject);
            ia1 =  v.findViewById(R.id.rw_ia1);
            ia2 =  v.findViewById(R.id.rw_ia2);
            ia3 =  v.findViewById(R.id.rw_ia3);
            total =  v.findViewById(R.id.rw_total);
        }
    }
}
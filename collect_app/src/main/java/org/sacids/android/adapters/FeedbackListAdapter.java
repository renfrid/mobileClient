package org.sacids.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.sacids.android.R;
import org.sacids.android.models.Feedback;

import java.util.List;

/**
 * Created by Renfrid Ngolongolo on 2/11/2016.
 */
public class FeedbackListAdapter extends BaseAdapter {

    private Context context;
    private List<Feedback> feedbackList;

    public FeedbackListAdapter(Context context, List<Feedback> feedback) {
        this.context = context;
        this.feedbackList = feedback;
    }

    @Override
    public int getCount() {
        return feedbackList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = li.inflate(R.layout.single_message, null);

        Feedback msg = feedbackList.get(position);

        TextView tv = (TextView) view.findViewById(R.id.tv_message);
        tv.setText(msg.getMessage());

        tv = (TextView) view.findViewById(R.id.tv_date_sent);
        tv.setText(msg.getDateCreated().toString());


        return view;
    }
}

package org.sacids.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.sacids.android.R;
import org.sacids.android.models.SurveyForm;

import java.util.List;

/**
 * Created by Renfrid-Sacids on 2/22/2016.
 */

public class SurveyFormListAdapter extends BaseAdapter {
    private Context context;
    private List<SurveyForm> myFormList;

    public SurveyFormListAdapter(Context context, List<SurveyForm> myFormList) {
        this.context = context;
        this.myFormList = myFormList;
    }

    @Override
    public int getCount() {
        return myFormList.size();
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
        View view = li.inflate(R.layout.single_form_info, null);

        SurveyForm mForm = myFormList.get(position);

        TextView tv = (TextView) view.findViewById(R.id.tv_form_name);
        tv.setText(mForm.getDisplayName());

        tv = (TextView) view.findViewById(R.id.tv_form_short_description);
        tv.setText(mForm.getDisplaySubText());

        tv = (TextView) view.findViewById(R.id.tv_form_status);
        tv.setText(mForm.getStatus());
        // change color depending on the form current form status

        return view;
    }
}
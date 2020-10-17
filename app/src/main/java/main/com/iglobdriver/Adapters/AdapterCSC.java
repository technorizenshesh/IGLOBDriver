package main.com.iglobdriver.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import java.util.ArrayList;

import main.com.iglobdriver.Fragments.ModelCSC;
import main.com.iglobdriver.Interfaces.onClickCSC;
import main.com.iglobdriver.R;

public class AdapterCSC extends BaseAdapter implements Filterable {
    private onClickCSC listener;
    Context mContext;
    private ArrayList<ModelCSC>originalData = null;
    private ArrayList<ModelCSC>data = null;
    private ItemFilter mFilter = new ItemFilter();
    public AdapterCSC(Context mContext, ArrayList<ModelCSC> data, onClickCSC clickCSC) {
        this.mContext = mContext;
        this.data = data;
        this.originalData = data;
        this.listener=clickCSC;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView= LayoutInflater.from(mContext).inflate(R.layout.layout_csc,parent,false);
        TextView tv_prefix=convertView.findViewById(R.id.tv_prefix);
        TextView tv_name=convertView.findViewById(R.id.tv_name);
        tv_prefix.setText(data.get(position).getName().substring(0,1));
        tv_name.setText(data.get(position).getName());
        convertView.setOnClickListener(v->listener.Result(data.get(position)));
        return convertView;
    }
    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final ArrayList<ModelCSC> list = originalData;

            int count = list.size();
            final ArrayList<ModelCSC> nlist = new ArrayList<ModelCSC>(count);

            ModelCSC filterableString ;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i);
                if (filterableString.getName().toLowerCase().startsWith(filterString.toLowerCase())) {
                    nlist.add(filterableString);
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            data = (ArrayList<ModelCSC>) results.values;
            notifyDataSetChanged();
        }

    }
}

package app.conectados.android.custom.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.appcompat.widget.AppCompatRadioButton;

import java.util.HashMap;
import java.util.List;
import app.conectados.android.R;
import app.conectados.android.models.SubCategory;

public class SubCategoryListAdapter extends BaseAdapter {

    private List<SubCategory> subcategoryList;
    private LayoutInflater layoutInflater;
    private CheckListener checkListener;
    private int selectedPosition = -1;

    public SubCategoryListAdapter(Activity activity, List<SubCategory> subcategoryList){
        this.subcategoryList = subcategoryList;
        layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void onCheckListener(CheckListener checkListener) {
        this.checkListener = checkListener;
    }

    @Override
    public int getCount() {
        return subcategoryList.size();
    }

    @Override
    public Object getItem(int i) {
        return subcategoryList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

	private static class ViewHolder{
        AppCompatRadioButton checkOption;
	}

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder = new ViewHolder();
        View vi = view;

        if(vi == null){
            vi = layoutInflater.inflate(R.layout.list_rowcategory,null);
            viewHolder.checkOption =  vi.findViewById(R.id.checkOption);
            viewHolder.checkOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HashMap<String, Object> mapi = (HashMap<String, Object>)v.getTag();
                    selectedPosition = (int)mapi.get("position");
                    SubCategory catSelected = (SubCategory) mapi.get("subcategory");
                    checkListener.onChecked(catSelected);
                    notifyDataSetChanged();
                }
            });
            vi.setTag(viewHolder);
        }else {
           viewHolder= (ViewHolder) vi.getTag();
        }

        HashMap<String, Object> mapp = new HashMap<>();
        mapp.put("position", position);
        mapp.put("subcategory", subcategoryList.get(position));

        viewHolder.checkOption.setText(subcategoryList.get(position).name);
        viewHolder.checkOption.setTag(mapp);
        viewHolder.checkOption.setChecked(position == selectedPosition);

        return vi;
    }

    public interface CheckListener {
        void onChecked(SubCategory subcategoryName);
    }
}
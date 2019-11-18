package app.conectados.android.custom.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatRadioButton;

import app.conectados.android.R;
import app.conectados.android.models.Category;
import app.conectados.android.utils.ImageUtils;
import java.util.HashMap;
import java.util.List;

public class CategoryListAdapter extends BaseAdapter {

    private List<Category> categoryList;
    private LayoutInflater layoutInflater;
    private CheckListener checkListener;
    private Activity mActivity;
    private int selectedPosition = -1;

    public CategoryListAdapter(Activity activity, List<Category> categoryList){
        this.mActivity = activity;
        this.categoryList = categoryList;
        layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void onCheckListener(CheckListener checkListener) {
        this.checkListener = checkListener;
    }

    private static class ViewHolder{
        ImageView icon;
        TextView name;
        AppCompatRadioButton checkOption;
    }

    public interface CheckListener {
        void onChecked(Category categoryName);
    }

    @Override
    public void notifyDataSetChanged(){
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return categoryList.size();
    }

    @Override
    public Object getItem(int i) {
        return categoryList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder = new ViewHolder();
        View vi = view;

        if(vi == null){
            vi = layoutInflater.inflate(R.layout.list_rowcategory,null);
            viewHolder.icon =  vi.findViewById(R.id.icon);
            viewHolder.name =  vi.findViewById(R.id.name);
            viewHolder.checkOption = vi.findViewById(R.id.checkOption);
            viewHolder.checkOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HashMap<String, Object> mapi = (HashMap<String, Object>)v.getTag();
                    selectedPosition = (int)mapi.get("position");
                    Category catSelected = (Category) mapi.get("category");
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
        mapp.put("category", categoryList.get(position));

        viewHolder.icon.setImageResource(ImageUtils.getMipMapId(mActivity, categoryList.get(position).mipmapName));
        viewHolder.name.setText(categoryList.get(position).name);
        viewHolder.checkOption.setTag(mapp);
        viewHolder.checkOption.setChecked(position == selectedPosition);

        return vi;
    }


}
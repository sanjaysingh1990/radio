package org.rajmoh.radio.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.rajmoh.radio.R;
import org.rajmoh.radio.callbacks.CategorySelectedCallback;
import org.rajmoh.radio.pojo.CategoryModel;
import org.rajmoh.radio.utils.Colors;

import java.util.ArrayList;
import java.util.List;


public class RecyclerCategoryAdapter extends RecyclerView.Adapter<RecyclerCategoryAdapter.CustomViewHolder> {
    private List<CategoryModel> mCategoryList;
    private Context mContext;
    private Colors[] colors;
    private CategorySelectedCallback mCategorySelected;

    public RecyclerCategoryAdapter(Context context, ArrayList<CategoryModel> mCategoryList, CategorySelectedCallback mCategorySelected) {
        this.mCategoryList = mCategoryList;
        this.mContext = context;
        colors = Colors.values();
        this.mCategorySelected = mCategorySelected;

    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View viewONE = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        CustomViewHolder rowONE = new CustomViewHolder(viewONE);
        return rowONE;


    }


    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        holder.mCategory.setBackgroundResource(R.drawable.shape_circle);  //drawable id
        holder.mCategory.setText(mCategoryList.get(position).getCate_name().substring(0, 1));
        GradientDrawable gd = (GradientDrawable) holder.mCategory.getBackground().getCurrent();
        gd.setColor(Color.parseColor(colors[position].getColorCode())); //set color
        holder.mCategoryName.setText(mCategoryList.get(position).getCate_name());
        setAnimation(holder.container, position);

    }

    private void setAnimation(LinearLayout container, int position) {
        Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
        container.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return mCategoryList.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView mCategory;
        TextView mCategoryName;
        LinearLayout container;

        public CustomViewHolder(View itemView) {
            super(itemView);
            mCategory = (TextView) itemView.findViewById(R.id.text_category);
            mCategoryName = (TextView) itemView.findViewById(R.id.text_category_name);
            container = (LinearLayout) itemView.findViewById(R.id.container);
            //font 1
            Typeface font = Typeface.createFromAsset(mContext.getAssets(), "fonts/font1.ttf");
            mCategory.setTypeface(font);
           //font 2
            Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/roboto_italic.ttf");
            mCategoryName.setTypeface(tf);



            mCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mCategorySelected != null) {
                        mCategorySelected.categorySelected(mCategoryList.get(getAdapterPosition()).getCate_name());
                    }
                }
            });
        }
    }
}
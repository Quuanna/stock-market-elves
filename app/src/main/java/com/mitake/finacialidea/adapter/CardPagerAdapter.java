package com.mitake.finacialidea.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;

import com.mitake.finacialidea.R;
import com.mitake.finacialidea.data.constant.UserSelectType;
import com.mitake.finacialidea.viewPagercards.CardAdapter;
import com.mitake.finacialidea.data.constant.CardItem;

import java.util.ArrayList;
import java.util.List;

public class CardPagerAdapter extends PagerAdapter implements CardAdapter {

    private final List<CardView> mViews;
    private final List<CardItem> mData;
    private float mBaseElevation;
    private final SelectResultListener mListener;

    public interface SelectResultListener {
        void onUserSelectResult(UserSelectType type);
    }

    public CardPagerAdapter(SelectResultListener listener) {
        mData = new ArrayList<>();
        mViews = new ArrayList<>();
        mListener = listener;
    }

    public void addCardItem(CardItem item) {
        mViews.add(null);
        mData.add(item);
    }

    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return mViews.get(position);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.item_card_adapter, container, false);
        container.addView(view);
        bind(mData.get(position), view);

        CardView cardView = (CardView) view.findViewById(R.id.cardView);

        if (mBaseElevation == 0) {
            mBaseElevation = cardView.getCardElevation();
        }

        cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);
        mViews.set(position, cardView);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mViews.set(position, null);
    }

    private void bind(CardItem item, View view) {
        TextView titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        TextView tvContentProfit = (TextView) view.findViewById(R.id.tvContentProfit);
        TextView tvContentTarget = (TextView) view.findViewById(R.id.tvContentTarget);
        TextView tvContentSkills = (TextView) view.findViewById(R.id.tvContentSkills);

        Button btnConfirm = (Button) view.findViewById(R.id.btnConfirm);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        titleTextView.setText(item.getTitle());
        tvContentProfit.setText(item.getProfit());
        tvContentTarget.setText(item.getTarget());
        tvContentSkills.setText(item.getHave_skills());

        switch (item.getSelectType()) {
            case VALUE_TYPE:
                imageView.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.image_value_type));
                break;
            case GROWTH_TYPE:
                imageView.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.image_growth_type));
                break;
            case STABLE_INVESTMENT:
                imageView.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.image_stable_investment));
                break;
        }

        btnConfirm.setOnClickListener(v ->
                mListener.onUserSelectResult(item.getSelectType())
        );
    }

}

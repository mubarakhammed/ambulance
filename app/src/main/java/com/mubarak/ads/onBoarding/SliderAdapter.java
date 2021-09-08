package com.mubarak.ads.onBoarding;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.mubarak.ads.R;



public class SliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context){
        this.context = context;
    }

    //Arrays
    public int[] slide_images = {
            R.drawable.emergencycall,
            R.drawable.hospital,
            R.drawable.handcuff
    };
    public String[] slide_headings = {
            "Get Quick Ambulance",
            "Locate Nearby Hospitals",
            "Report Case"
    };

    public String[] slide_descs = {
            "With one tap you can get nearby ambulance dispatched to your emergency center. No sign or much stress. Just tap and wait",
            "Connect with hospitals in your community. Get access to easy and mobile healthcare   ",
            "A very great addition to this app is getting the hospital around with their emergency line with one tap to call them."

    };


    @Override
    public int getCount() {
        return slide_headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == (LinearLayout) o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout, container, false);

        ImageView slideImageView = (ImageView) view.findViewById(R.id.slideImage);
        TextView slideHeading = (TextView) view.findViewById(R.id.slideHead);
        TextView slideDesc = (TextView) view.findViewById(R.id.slideDescription);


        slideImageView.setImageResource(slide_images[position]);
        slideHeading.setText(slide_headings[position]);
        slideDesc.setText(slide_descs[position]);

        container.addView(view);

        return view;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout)object);
    }
}

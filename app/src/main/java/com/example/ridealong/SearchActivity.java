package com.example.ridealong;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;

import com.example.ridealong.fragments.DetailsFragment;
import com.example.ridealong.fragments.PickLocationFragment;
import com.example.ridealong.fragments.ResultsFragment;
import com.example.ridealong.misc.ViewPagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity  implements TabLayoutMediator.TabConfigurationStrategy {

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    ArrayList<String> titles;
    FloatingActionButton fabNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        tabLayout = findViewById(R.id.tabLayoutSearch);
        fabNav = findViewById(R.id.fabSearch);

        fabNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = tabLayout.getSelectedTabPosition();

                if (pos == 0){
                    TabLayout.Tab tab = tabLayout.getTabAt(1);
                    tab.select();
                }
                else if (pos == 1){
                    TabLayout.Tab tab = tabLayout.getTabAt(2);
                    tab.select();
                }

                else if (pos == 2){
                    TabLayout.Tab tab = tabLayout.getTabAt(3);
                    tab.select();
                }
            }
        });


        viewPager2 = findViewById(R.id.viewpagerSearch);
        viewPager2.setUserInputEnabled(false);
        titles = new ArrayList<String>();
        titles.add("FROM");
        titles.add("TO");
        titles.add("DETAILS");
        titles.add("RESULTS");

        setViewPagerAdapter();


        new TabLayoutMediator(tabLayout, viewPager2, this).attach();

    }
    public void setViewPagerAdapter(){

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);

        Bundle b1 = new Bundle();
        Bundle b2 = new Bundle();

        b1.putBoolean("isOrigin", true);
        b2.putBoolean("isOrigin", false);

        ArrayList<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(PickLocationFragment.newInstance(true));
        fragmentList.add(PickLocationFragment.newInstance(false));
        fragmentList.add(new DetailsFragment());
        fragmentList.add(new ResultsFragment());

        viewPagerAdapter.setFragments(fragmentList);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {

                if (position == 0) fabNav.setImageResource(R.drawable.ic_baseline_arrow_forward_ios_24);
                if (position == 1) fabNav.setImageResource(R.drawable.ic_baseline_arrow_forward_ios_24);
                if (position == 2) fabNav.setImageResource(R.drawable.ic_baseline_search_24);
                if (position == 3) fabNav.setImageResource(R.drawable.ic_baseline_search_24);


                super.onPageSelected(position);
            }
        });

        viewPager2.setAdapter(viewPagerAdapter);
    }

    @Override
    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
        tab.setText(titles.get(position));

    }
}
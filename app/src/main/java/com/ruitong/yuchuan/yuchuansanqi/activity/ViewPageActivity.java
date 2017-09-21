package com.ruitong.yuchuan.yuchuansanqi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.ruitong.yuchuan.yuchuansanqi.R;
import com.ruitong.yuchuan.yuchuansanqi.adapter.FourViewPagerAdapter;
import com.ruitong.yuchuan.yuchuansanqi.manager.GlobalManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewPageActivity extends AppCompatActivity {

    //    public static final int RESULT_DETAIL = 001;
    private static String ARG_LITE_ID = "arg_lite_id";
    private static String ARG_ENTER_TYPE = "arg_enter_type";


    @BindView(R.id.tab_sure)
    TabLayout mTabLayoutSure;
    @BindView(R.id.view_pager_sure)
    ViewPager mViewPagerFour;

    private AppCompatActivity mActivity;
    private Context mContext;
    private String mType;
    private FourViewPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_page);

        ButterKnife.bind(this);

        mActivity = this;
        mContext = this;
        getIntentData();

        initViewPagerAndTabs();
    }


    private void initViewPagerAndTabs() {

        mViewPagerFour.setOffscreenPageLimit(4);//设置viewpager预加载页面数

        mPagerAdapter = new FourViewPagerAdapter(getSupportFragmentManager(), mType);

        mViewPagerFour.setAdapter(mPagerAdapter);  // 给Viewpager设置适配器
        if (GlobalManager.ENTERTYPE_AIS.equals(mType)) {
            mViewPagerFour.setCurrentItem(0); // 设置当前显示在哪个页面}
        }
        if (GlobalManager.ENTERTYPE_GPS.equals(mType)) {
            mViewPagerFour.setCurrentItem(1); // 设置当前显示在哪个页面}
        }
        if (GlobalManager.ENTERTYPE_COMPASS.equals(mType)) {
            mViewPagerFour.setCurrentItem(2); // 设置当前显示在哪个页面}
        }
        if (GlobalManager.ENTERTYPE_SETTING.equals(mType)) {
            mViewPagerFour.setCurrentItem(3); // 设置当前显示在哪个页面}
        }

        mTabLayoutSure.setupWithViewPager(mViewPagerFour);
    }

    public static void actionStart(Context context,String type) {
        Intent intent = new Intent(context, ViewPageActivity.class);
        intent.setType(type);
        context.startActivity(intent);
    }

    /**
     * 得到从上一个activity中拿到的数据
     */
    private void getIntentData() {
        Intent intent = getIntent();
        mType = intent.getType();
    }
}

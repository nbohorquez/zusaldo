package com.zuliaworks.zusaldo;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class Pestanas extends FragmentStatePagerAdapter 
                      implements ViewPager.OnPageChangeListener {
    // Variables
    private final Context mContext;
    private final ViewPager mViewPager;
    private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

    // Constructores
    public Pestanas(Activity activity, ViewPager pager) {
        super(((SherlockFragmentActivity)activity).getSupportFragmentManager());
        mContext = activity;
        mViewPager = pager;
        mViewPager.setAdapter(this);
        // If you use an OnPageChangeListener with your view pager you should 
        // set it in the indicator rather than on the pager directly.
        //mViewPager.setOnPageChangeListener(this);
    }
    
    // Funciones
    public void agregarPestana(CharSequence name, Class<?> clss, Bundle args) {
        TabInfo info = new TabInfo(name, clss, args);
        mTabs.add(info);
        notifyDataSetChanged();
    }
    
    public Fragment obtenerFragmento(Integer posicion) {
        return (Fragment) instantiateItem(mViewPager, posicion);
    }

    // Implementacion de interfaces
    @Override
    public int getCount() {
        return mTabs.size();
    }

    @Override
    public Fragment getItem(int position) {
        TabInfo info = mTabs.get(position);
        Fragment f = Fragment.instantiate(
            mContext, info.clss.getName(), info.args
        );
        return f; 
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabs.get(position).name;
    }
    
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                ((MainActivity)mContext).mostrarMenuContextual(obtenerFragmento(0));
                break;
            case 1:
                ((MainActivity)mContext).quitarMenuContextual();
                break;
            case 2:
                break;
            default:
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    // Tipos anidados
    static final class TabInfo {
        private final CharSequence name;
        private final Class<?> clss;
        private final Bundle args;

        TabInfo(CharSequence _name, Class<?> _class, Bundle _args) {
            name = _name;
            clss = _class;
            args = _args;
        }
    }
}
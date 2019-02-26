package com.bigcreate.zyfw.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class FragmentAdapter(fm: FragmentManager, val list: List<Fragment>) : FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Fragment {
        return list[position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return list[position].arguments?.getString("title", "")
    }
}
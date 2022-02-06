package com.meleshkomd.funnyanimationapp

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.meleshkomd.funnyanimationapp.ui.HotSectionFragment
import com.meleshkomd.funnyanimationapp.ui.LatestSectionFragment
import com.meleshkomd.funnyanimationapp.ui.TopSectionFragment

class PagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                LatestSectionFragment()
            }
            1 -> TopSectionFragment()
            else -> {
                return HotSectionFragment()
            }
        }
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "Последние"
            1 -> "Лучшие"
            else -> {
                return "Горячие"
            }
        }
    }

}
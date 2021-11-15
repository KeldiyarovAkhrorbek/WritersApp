package com.projects.writers.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.projects.writers.fragments.subfr.ClassicFragment
import com.projects.writers.fragments.subfr.UzbekFragment
import com.projects.writers.fragments.subfr.WorldFragment

class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                ClassicFragment()
            }
            1 -> {
                UzbekFragment()
            }
            else -> {
                WorldFragment()
            }
        }
    }

}
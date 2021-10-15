package myapps.myportfolio.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import myapps.myportfolio.fragments.AssetsFragment
import myapps.myportfolio.fragments.SummaryFragment

class SummaryPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    companion object{
        const val NUM_PAGES = 2
    }

    override fun getItem(position: Int): Fragment = when(position){
        0 -> SummaryFragment()
        1 -> AssetsFragment()
        else -> SummaryFragment()
    }

    override fun getCount() : Int = NUM_PAGES

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    override fun getPageTitle(position: Int): CharSequence? = when(position) {
        0 -> "Summary"
        1 -> "Assets"
        else -> "Unknown"
    }
}
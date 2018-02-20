package dmitriy.deomin.cit_mich.pager

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter


class adapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> return News()
            1 -> return Tovar_nedeli()
            2 -> return Kategoris()
            3 -> return Info()
        }
        return null
    }

    override fun getCount(): Int {
        return 4
    }
}
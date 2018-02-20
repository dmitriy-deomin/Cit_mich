package dmitriy.deomin.cit_mich.pager

import android.support.v4.app.Fragment
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import dmitriy.deomin.cit_mich.R


class News : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val result = inflater!!.inflate(R.layout.news, container, false)
        return result
    }

}
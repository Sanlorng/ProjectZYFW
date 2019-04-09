package com.bigcreate.zyfw.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.activities.MainActivity

/**
 * A simple [Fragment] subclass.
 */
class ExploreFragment : Fragment(),MainActivity.ChildFragment {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_explore, container, false)
    }

    override fun onLoginSuccess() {

    }

}

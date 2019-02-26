package com.bigcreate.zyfw.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.activities.LoginActivity


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MyPageFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MyPageFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class MyPageFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_page, container, false)
        val button = view.findViewById<Button>(R.id.button)
        button.setOnClickListener {
            startActivity(Intent(activity?.baseContext, LoginActivity::class.java))
        }
        return view
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                MyPageFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }

    }
}

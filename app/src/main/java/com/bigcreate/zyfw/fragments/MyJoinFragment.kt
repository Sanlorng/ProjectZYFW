package com.bigcreate.zyfw.fragments


import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager

import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.adapter.FavoriteListAdapter
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.datasource.FavoriteListDataSource
import com.bigcreate.zyfw.datasource.JoinedListDataSource
import com.bigcreate.zyfw.models.Project
import com.bigcreate.zyfw.viewmodel.NetworkStateViewModel
import kotlinx.android.synthetic.main.fragment_my_favorite.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MyJoinFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyJoinFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var networkStateViewModel: NetworkStateViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        arguments?.putString("title","加入的项目")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_favorite, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        networkStateViewModel = ViewModelProviders.of(this).get(NetworkStateViewModel::class.java)
        listMyFavorite.layoutManager = LinearLayoutManager(listMyFavorite.context)
        refreshList()
    }
    private fun refreshList() {
        val adapter = FavoriteListAdapter()
        adapter.submitList(PagedList.Builder<Int, Project>(JoinedListDataSource(networkStateViewModel.state), PagedList.Config.Builder()
                .setPageSize(10)
                .setPrefetchDistance(20)
                .build())
                .setNotifyExecutor {
                    Handler(Looper.getMainLooper()).post(it)
                }.setFetchExecutor {
                    Attributes.backgroundExecutors.execute(it)
                }.build())
        listMyFavorite.adapter = adapter
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MyJoinFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                MyJoinFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                        putString("title","加入的项目")
                    }
                }
    }
}

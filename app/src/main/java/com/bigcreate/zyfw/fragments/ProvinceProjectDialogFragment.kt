package com.bigcreate.zyfw.fragments

import android.content.Context
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.models.ProvinceProject
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_province_project_dialog.*


/**
 *
 * A fragment that shows a list of items as a modal bottom sheet.
 *
 * You can show this modal bottom sheet from your activity like this:
 * <pre>
 *    ProvinceProjectDialogFragment.newInstance(30).show(supportFragmentManager, "dialog")
 * </pre>
 *
 * You activity (or fragment) needs to implement [ProvinceProjectDialogFragment.Listener].
 */
class ProvinceProjectDialogFragment : BottomSheetDialogFragment() {
    private var mListener: Listener? = null
    var pronviceProject: ProvinceProject? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_province_project_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        pronviceProject?.apply {
            titleProvinceProject.text = projectTitle
            placeProvinceProject.text = "地点：$projectPlace"
            serviceTypeProvinceProject.text = "服务类型：$serviceType"
            serviceObjectProvinceProject.text = "服务对象：$serviceObject"
            recruitDateProvinceProject.text = "招募时间：$recruitDate"
            projectDateProvinceProject.text = "项目时间：$projectDate"
            releaseDateProvinceProject.text = "发布时间：$releaseDate"
            serviceTimeProvinceProject.text = "服务时间：$serviceTime"
            guaranteeProvinceProject.text = "福利：$guarantee"
            contactsProvinceProject.text = "联系人：$contacts"
            contactInforProvinceProject.text = "联系方式：$contactInfor"
            Glide.with(this@ProvinceProjectDialogFragment)
                    .load(imgSrc)
                    .into(imageProvinceProject)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
//        val parent = parentFragment
//        if (parent != null) {
//            mListener = parent as Listener
//        } else {
//            mListener = context as Listener
//        }
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    interface Listener {
        fun onItemClicked(position: Int)
    }


    companion object {
        fun newInstance(itemCount: Int): ProvinceProjectDialogFragment =
                ProvinceProjectDialogFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_ITEM_COUNT, itemCount)
                    }
                }

    }
}

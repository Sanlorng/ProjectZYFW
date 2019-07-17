package com.bigcreate.zyfw.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.IntegerRes
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import com.bigcreate.library.*
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.base.*
import com.bigcreate.zyfw.models.InitPersonInfoRequest
import com.bigcreate.zyfw.mvp.user.UserInfoImpl
import com.bilibili.boxing.Boxing
import com.bilibili.boxing.BoxingMediaLoader
import com.bilibili.boxing.loader.IBoxingCallback
import com.bilibili.boxing.loader.IBoxingMediaLoader
import com.bilibili.boxing.model.config.BoxingConfig
import com.bilibili.boxing_impl.ui.BoxingActivity
import com.bumptech.glide.Glide
import com.google.gson.JsonObject
import com.tencent.map.geolocation.TencentLocation
import com.tencent.map.geolocation.TencentLocationListener
import com.tencent.map.geolocation.TencentLocationManager
import com.tencent.map.geolocation.TencentLocationRequest
import kotlinx.android.synthetic.main.fragment_setup_info.*
import java.io.File

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the [SetupInfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class SetupInfoFragment : Fragment(), TencentLocationListener, UserInfoImpl.View {
    private var param1: String? = null
    private var param2: String? = null
    private var tencentLocation: TencentLocation? = null
    private var listAddress = ArrayList<String>()
    private val userInfoImpl = UserInfoImpl(this)
    private var avatarFile: File? = null
    private val boxImpl = object : IBoxingMediaLoader {
        override fun displayRaw(img: ImageView, absPath: String, width: Int, height: Int, callback: IBoxingCallback?) {
            Glide.with(this@SetupInfoFragment)
                    .load(absPath)
                    .into(img)
        }

        override fun displayThumbnail(img: ImageView, absPath: String, width: Int, height: Int) {
            Glide.with(this@SetupInfoFragment)
                    .load(absPath)
                    .into(img)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_setup_info, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        val isSetup = activity?.intent?.type
//        val isEditMode = activity?.intent?.getBooleanExtra("isEditMode",false).valueOrNotNull
        if (isSetup == null || isSetup != "setupInfo") {
            appCompactActivity?.run {
                setSupportActionBar(toolbarSetupInfo)
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
            }
            toolbarSetupInfo.setNavigationOnClickListener {
                if (isSetup == null)
                    fragmentManager!!.popBackStack()
                else
                    activity?.finish()
            }
        }
        toolbarSetupInfo.requestApplyInsets()
        BoxingMediaLoader.getInstance().init(boxImpl)
        imageAvatarSetupInfo.setOnClickListener {
//            val intent = Intent(Intent.ACTION_GET_CONTENT)
//            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageType)
//            startActivityForResult(intent, photoResult)
            Boxing.of(BoxingConfig(BoxingConfig.Mode.SINGLE_IMG))
                    .withIntent(context!!,BoxingActivity::class.java)
                    .start(this,RequestCode.SELECT_IMAGE)
        }
        chipGroupGenderTypeSetupInfo.setOnCheckedChangeListener { _, i ->
            Log.e("check", chipGroupGenderTypeSetupInfo.checkedChipId.toString())
            Log.e("i", i.toString())
        }
        layoutPhoneSetupInfo.editText?.append(Attributes.username)
        buttonSubmitSetupInfo.setOnClickListener {
            Log.d("click", "click ok")
            if (layoutNickSetupInfo.editText!!.isEmpty() || layoutAddressSetupInfo.editText!!.isEmpty() ||
                    layoutPhoneSetupInfo.editText!!.isEmpty() || avatarFile == null || chipGroupGenderTypeSetupInfo.checkedChipId == -1 || chipGroupUserTypeSetupInfo.checkedChipId == -1)
                Toast.makeText(context!!, "你还有尚未填写的资料，请填好后重试", Toast.LENGTH_SHORT).show()
            else {
                progressSetupInfo.visibility = View.VISIBLE
                textUserTypeSetupInfo.isEnabled = false
                textGenderSetupInfo.isEnabled = false
                layoutNickSetupInfo.isEnabled = false
                layoutPhoneSetupInfo.isEnabled = false
                layoutAddressSetupInfo.isEnabled = false
                buttonSubmitSetupInfo.isEnabled = false
                chipQuickLocaleSetupInfo.isEnabled = false
                val userInfo = Attributes.loginUserInfo!!
                userInfoImpl.doInitUserInfo(InitPersonInfoRequest(userInfo.username,
                        layoutNickSetupInfo.editText!!.string().trim(),
                        getIndexForChip(chipGroupGenderTypeSetupInfo.checkedChipId),
                        getIndexForChip(chipGroupUserTypeSetupInfo.checkedChipId),
                        layoutAddressSetupInfo.editText!!.string().trim(),
                        layoutPhoneSetupInfo.editText!!.string().trim(), userInfo.token))
            }
        }
        chipQuickLocaleSetupInfo.setOnClickListener {
            Log.d("is click", "is click")
            val popupMenu = PopupMenu(context!!, chipQuickLocaleSetupInfo)
            popupMenu.menu.run {
                if (tencentLocation != null) {
                    this.clear()
                    listAddress.clear()
                    if (tencentLocation!!.address != "")
                        listAddress.add(tencentLocation!!.address)
                    else
                        tencentLocation!!.run {
                            Log.d("address is null", "address")
                            listAddress.add((province + city + district + town + village + street + streetNo).split("Unknown").first())
                        }

                    tencentLocation!!.poiList.forEach {
                        listAddress.add(it.address)
                        it.address.logIt("address")
                    }
                    for (i in 0 until listAddress.size) {
                        add(Menu.NONE, Menu.FIRST + i, i, listAddress[i])
                    }
                    Log.d("size", tencentLocation!!.poiList.size.toString())
                } else {
                    Toast.makeText(context!!, "无法获取地理位置", Toast.LENGTH_SHORT).show()
                }
            }
            popupMenu.setOnMenuItemClickListener {
                val i = it.itemId - Menu.FIRST
                tencentLocation?.run {
                    layoutAddressSetupInfo.editText?.text?.run {
                        clear()
                        append(listAddress[i])


                    }
                }
                true
            }
            popupMenu.show()
            Log.d("is show", "show")
        }


        val tencentLocation = TencentLocationManager.getInstance(context)
        val request = TencentLocationRequest.create()
        request?.run {
            requestLevel = TencentLocationRequest.REQUEST_LEVEL_POI
            isAllowCache = true
            interval = 15000
            isAllowGPS = true
            isAllowDirection = true
        }
        tencentLocation.requestLocationUpdates(request, this)
        super.onActivityCreated(savedInstanceState)
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


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SetupInfoFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                SetupInfoFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

    override fun onStatusUpdate(p0: String?, p1: Int, p2: String?) {
    }

    override fun onLocationChanged(p0: TencentLocation?, p1: Int, p2: String?) {
        p0?.run {
            tencentLocation = this
        }
    }

    override fun onResume() {
        activity?.window?.run {
            translucentSystemUI(true)
        }
        super.onResume()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Boxing.getResult(data)?.run {
//            data.data?.run {
                Log.d("url", get(0).path)
                when (requestCode) {
                    RequestCode.SELECT_IMAGE -> {
//                        imageView_setup.setImageBitmap(context!!.getBitmapFromUri(this).roundBitmap)
//                        imageView_setup.scaleType = ImageView.ScaleType.CENTER_CROP
                        textAvatarSetupInfo.text = "更换照片"
                        Glide.with(this@SetupInfoFragment)
                                .load(get(0).path)
                                .circleCrop()
                                .into(imageAvatarSetupInfo)
                        avatarFile = File(get(0).path)
                    }
                }
//            }
        }
        super.onActivityResult(requestCode, resultCode, data)

    }

    override fun getViewContext() = context!!
    override fun onInitUserInfoFailed(jsonObject: JsonObject) {
        context!!.toast("设置信息失败")
    }

    override fun onInitUserInfoSuccess(jsonObject: JsonObject) {

        val userInfo = Attributes.loginUserInfo!!
        userInfo.apply {
            userInfoImpl.doSetupAvatar(
                    avatarFile!!, token, username)
        }

    }

    override fun onNetworkFailed() {
        context!!.toast("网络连接失败")
    }

    override fun onRequesting() {

    }

    override fun onRequestFinished() {

    }

    override fun onUpdateUserInfoFailed(jsonObject: JsonObject) {

    }

    override fun onUpdateUserInfoSuccess(jsonObject: JsonObject) {
        activity?.finish()
    }

    override fun onSetupAvatarSuccess() {
        activity?.apply {
            setResult(ResultCode.OK)
            finish()
        }
    }

    override fun onSetupAvatarFailed() {
        context?.toast("头像上传失败")
    }

    private fun getIndexForChip(@IntegerRes id: Int): Int {
        return when (id) {
            R.id.chipIdStudentSetupInfo, R.id.chipMaleSetupInfo -> 1
            R.id.chipIdTeacherSetupInfo, R.id.chipFemaleSetupInfo -> 2
            R.id.chipIdOtherSetupInfo -> 3
            else -> 0
        }
    }
}

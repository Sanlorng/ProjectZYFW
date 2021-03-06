package com.bigcreate.zyfw.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.IntegerRes
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bigcreate.library.*
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.base.*
import com.bigcreate.zyfw.models.InitPersonInfoRequest
import com.bigcreate.zyfw.models.UpdateInfoRequest
import com.bigcreate.zyfw.models.UserInfo
import com.bigcreate.zyfw.mvp.user.GetUserInfoImpl
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
class SetupInfoFragment(private val infoType:String,private val schoolName: String) : Fragment(), TencentLocationListener, UserInfoImpl.View{
    private var param1: String? = null
    private var param2: String? = null
    private var tencentLocation: TencentLocation? = null
    private var listAddress = ArrayList<String>()
    private val userInfoImpl = UserInfoImpl(this)
    private var avatarFile: File? = null
    private var sexId = -1
    private var identifyId = -1
    private val boxImpl = object : IBoxingMediaLoader {
        override fun displayRaw(img: ImageView, absPath: String, width: Int, height: Int, callback: IBoxingCallback?) {
            Glide.with(img.context)
                    .load(absPath)
                    .into(img)
        }

        override fun displayThumbnail(img: ImageView, absPath: String, width: Int, height: Int) {
            Glide.with(img.context)
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
        }else if (isSetup == "setupInfo") {

            activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {

                }
            })
        }
//        toolbarSetupInfo.requestApplyInsets()
        toolbarSetupInfo.paddingStatusBar()
        BoxingMediaLoader.getInstance().init(boxImpl)
        imageAvatarSetupInfo.setOnClickListener {
            //            val intent = Intent(Intent.ACTION_GET_CONTENT)
//            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageType)
//            startActivityForResult(intent, photoResult)
            Boxing.of(BoxingConfig(BoxingConfig.Mode.SINGLE_IMG))
                    .withIntent(context!!, BoxingActivity::class.java)
                    .start(this, RequestCode.SELECT_IMAGE)
        }
//        chipGroupGenderTypeSetupInfo.setOnCheckedChangeListener { _, i ->
//            Log.e("check", chipGroupGenderTypeSetupInfo.checkedChipId.toString())
//            Log.e("i", i.toString())
//        }
        dropdownSex.setAdapter(ArrayAdapter(context!!,R.layout.dropdown_menu_popup_item, arrayOf("男性","女性")))
//        dropdownSex.text.append("男性")
        dropdownSex.setOnItemClickListener { parent, view, position, id ->
            sexId = position + 1
        }
//        dropdownSex.setSelection(0)
        dropdownIdentify.setAdapter(ArrayAdapter(context!!,R.layout.dropdown_menu_popup_item, arrayOf("学生","老师","其他")))
//        dropdownIdentify.text.append("学生")

        dropdownIdentify.setOnItemClickListener { parent, view, position, id ->
            identifyId = position + 1
        }
//        dropdownIdentify.setSelection(0)
        layoutPhoneSetupInfo.editText?.append(Attributes.username)
        buttonSubmitSetupInfo.setOnClickListener {
            Log.d("click", "click ok")
            if (layoutNickSetupInfo.editText!!.isEmpty() || layoutAddressSetupInfo.editText!!.isEmpty() || layoutUserEmailSetupInfo.editText!!.isEmpty()||
                    layoutPhoneSetupInfo.editText!!.isEmpty() || (avatarFile == null && infoType.startsWith("setupInfo")) || identifyId == -1 && infoType.startsWith("setupInfo") || sexId == -1 && infoType.startsWith("setupInfo"))
                Toast.makeText(context!!, "你还有尚未填写的资料，请填好后重试", Toast.LENGTH_SHORT).show()
            else {
                showProgress(true)
                val userInfo = Attributes.loginUserInfo!!
                if (infoType.startsWith("updateInfo")) {
                    userInfoImpl.doUpdateUserInfo(UpdateInfoRequest(
                            layoutAddressSetupInfo.editText!!.string().trim(),
                            layoutPhoneSetupInfo.editText!!.string().trim(), Attributes.userId, userInfo.token, layoutUserEmailSetupInfo.editText!!.string().trim()))
                }else {
                    userInfoImpl.doInitUserInfo(InitPersonInfoRequest(userInfo.username,
                            layoutNickSetupInfo.editText!!.string().trim(),
                            sexId,
                            identifyId,
                            layoutAddressSetupInfo.editText!!.string().trim(),
                            layoutPhoneSetupInfo.editText!!.string().trim(), userInfo.token, Attributes.userId, layoutUserEmailSetupInfo.editText!!.string().trim(),schoolName))
                }
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
        dealInfoType()
        super.onActivityCreated(savedInstanceState)
    }

    private fun showProgress(boolean: Boolean) {
        progressSetupInfo.isVisible = boolean
//        textUserTypeSetupInfo.isEnabled = boolean.not()
//        textGenderSetupInfo.isEnabled = boolean.not()
        if (infoType.startsWith("setupInfo")) {
            layoutDropdownIdentify.isEnabled = boolean.not()
            layoutDropdownSex.isEnabled = boolean.not()
        }
        layoutNickSetupInfo.isEnabled = boolean.not()
        layoutPhoneSetupInfo.isEnabled = boolean.not()
        layoutAddressSetupInfo.isEnabled = boolean.not()
        layoutUserEmailSetupInfo.isEnabled = boolean.not()
        buttonSubmitSetupInfo.isEnabled = boolean.not()
        chipQuickLocaleSetupInfo.isEnabled = boolean.not()
//        chipGroupUserTypeSetupInfo.isActivated = boolean.not()
//        chipGroupGenderTypeSetupInfo.isActivated = boolean.not()
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
                SetupInfoFragment("","").apply {
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
            if (avatarFile != null) {
                userInfoImpl.doSetupAvatar(
                        avatarFile!!, token, userId)
            }
        }

    }

    override fun onNetworkFailed() {
        context!!.toast("网络连接失败")
    }

    override fun onRequesting() {
        showProgress(true)
    }

    override fun onRequestFinished() {
        showProgress(false)
    }

    override fun onUpdateUserInfoFailed(jsonObject: JsonObject) {

    }

    override fun onUpdateUserInfoSuccess(jsonObject: JsonObject) {
        //activity?.finish()
        toast("更新成功")
        if (avatarFile != null) {
            val userInfo = Attributes.loginUserInfo!!
            userInfo.apply {
                if (avatarFile != null) {
                    userInfoImpl.doSetupAvatar(
                            avatarFile!!, token, userId)
                }
            }
        }else {
//            GetUserInfoImpl(object : GetUserInfoImpl.View {
//                override fun getViewContext(): Context {
//                    return context!!
//                }
//
//                override fun onGetUserInfoFailed() {
//
//                }
//
//                override fun onGetUserInfoSuccess(userInfo: UserInfo) {
//
//                }
//
//                override fun onUserInfoIsEmpty() {
//
//                }
//            })
        }
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

//    private fun getIndexForChip(@IntegerRes id: Int): Int {
//        return when (id) {
//            R.id.chipIdStudentSetupInfo, R.id.chipMaleSetupInfo -> 1
//            R.id.chipIdTeacherSetupInfo, R.id.chipFemaleSetupInfo -> 2
//            R.id.chipIdOtherSetupInfo -> 3
//            else -> 0
//        }
//    }

    private fun dealInfoType() {
        when {
            infoType.startsWith("setupInfo") -> {

            }
            infoType.startsWith("updateInfo") -> {
                showInfo()
                layoutDropdownSex.isEnabled = false
                layoutDropdownIdentify.isEnabled = false
            }
            infoType.startsWith("showInfo") -> {
                showInfo()
            }
        }
    }

    private fun showInfo() {
        Attributes.userInfo?.apply {
            Glide.with(imageAvatarSetupInfo.context)
                    .load(userHeadPictureLink)
                    .circleCrop()
                    .into(imageAvatarSetupInfo)
            textAvatarSetupInfo.text = "更换个人头像"
            layoutUserEmailSetupInfo.editText?.text?.clear()
            layoutUserEmailSetupInfo.editText?.text?.append(userEmail)
            layoutNickSetupInfo.editText?.text?.clear()
            layoutNickSetupInfo.editText?.text?.append(userNick)
            layoutAddressSetupInfo.editText?.text?.clear()
            layoutAddressSetupInfo.editText?.text?.append(userAddress)
            if (userSex == "男") {
//                chipGroupGenderTypeSetupInfo.check(R.id.chipMaleSetupInfo)
                dropdownSex.text.clear()
                dropdownSex.text.append("男性")
                sexId = 1
//                dropdownSex.setSelection(0)
            }else {
                dropdownSex.text.clear()
                dropdownSex.text.append("女性")
//                dropdownSex.setSelection(1)
            }

            when (userIdentify) {
                "学生" -> {
                    dropdownIdentify.text.clear()
                    dropdownIdentify.text.append("学生")
//                    dropdownIdentify.setSelection(0)
                }
                "老师" -> {
                    dropdownIdentify.text.clear()
                    dropdownIdentify.text.append("老师")
//                    dropdownIdentify.setSelection(1)
                }
                else -> {
                    dropdownIdentify.text.clear()
                    dropdownIdentify.text.append("其他")
//                    dropdownIdentify.setSelection(2)
                }
            }

//            if (i)
            //layoutNickSetupInfo.editText.cane
        }
    }
}

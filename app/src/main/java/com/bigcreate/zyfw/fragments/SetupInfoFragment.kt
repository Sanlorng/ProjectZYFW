package com.bigcreate.zyfw.fragments

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
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
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.base.appCompactActivity
import com.bigcreate.zyfw.models.InitPersonInfoRequest
import com.bigcreate.zyfw.mvp.user.UserInfoContract
import com.bigcreate.zyfw.mvp.user.UserInfoImpl
import com.google.gson.JsonObject
import com.tencent.map.geolocation.TencentLocation
import com.tencent.map.geolocation.TencentLocationListener
import com.tencent.map.geolocation.TencentLocationManager
import com.tencent.map.geolocation.TencentLocationRequest
import kotlinx.android.synthetic.main.fragment_setup_info.*
import java.io.File

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SetupInfoFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [SetupInfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class SetupInfoFragment : Fragment(), TencentLocationListener, UserInfoContract.NetworkView {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private var tencentLocation: TencentLocation? = null
    private var listAdress = ArrayList<String>()
    private val userInfoImpl = UserInfoImpl(this)
    val photoResult = 3
    val imageType = "image/*"
    private var avatarFile: File? = null
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
        activity?.intent?.putExtra("type", "setupInfo")
        val isSetup = activity?.intent?.getStringExtra("type")
        if (isSetup == null || isSetup != "setupInfo") {
            appCompactActivity?.run {
                setSupportActionBar(toolbar_setup_info)
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
            }
            toolbar_setup_info.setNavigationOnClickListener {
                fragmentManager!!.popBackStack()
            }
        }
        imageView_setup.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageType)
            startActivityForResult(intent, photoResult)
        }
        chipGroupGenderType.setOnCheckedChangeListener { chipGroup, i ->
            Log.e("check", chipGroupGenderType.checkedChipId.toString())
            Log.e("i", i.toString())
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1)
            phone_input_setup_info.autofillHints?.set(0, value = Attributes.loginUserInfo!!.username)
        else
            phone_input_setup_info.editText?.append(Attributes.loginUserInfo!!.username)
        ok_button_setup_info.setOnClickListener {
            Log.d("click", "click ok")
            //val response = WebKit.okClient.postRequest()
            if (nick_name_setup_info.editText!!.isEmpty() || address_input_setup_info.editText!!.isEmpty() ||
                    phone_input_setup_info.editText!!.isEmpty() || avatarFile == null || chipGroupGenderType.checkedChipId == -1 || chipGroupUserType.checkedChipId == -1)
                Toast.makeText(context!!, "你还有尚未填写的资料，请填好后重试", Toast.LENGTH_SHORT).show()
            else {

//                activity?.myApplication?.loginUser?.run {
                progressBar2.visibility = View.VISIBLE
                textView7.isEnabled = false
                gender_text_setup_info.isEnabled = false
                nick_name_setup_info.isEnabled = false
                phone_input_setup_info.isEnabled = false
                address_input_setup_info.isEnabled = false
//                    gender_spinner_setup_info.isEnabled = false
//                    identity_spinner_setup_info.isEnabled = false
                ok_button_setup_info.isEnabled = false
                chip.isEnabled = false
                val userInfo = Attributes.loginUserInfo!!
                userInfoImpl.doInitUserInfo(InitPersonInfoRequest(userInfo.username,
                        nick_name_setup_info.editText!!.string().trim(),
                        getIndexForChip(chipGroupGenderType.checkedChipId),
                        getIndexForChip(chipGroupUserType.checkedChipId),
                        address_input_setup_info.editText!!.string().trim(),
                        phone_input_setup_info.editText!!.string().trim(), userInfo.token))
//                    Thread {
//                        //TODO
////                        val setupInfoRequire = InfoRequire(name, nick_name_setup_info.editText!!.string(), gender_spinner_setup_info.selectedItem as String,
////                                identity_spinner_setup_info.selectedItem as String, address_input_setup_info.editText!!.string(), phone_input_setup_info.editText!!.string(),imageString)
////                        val data = WebKit.gson.toJson(setupInfoRequire)
//                        val data = ""
//                        Log.d("json", data)
//                        val response = WebKit.okClient.postRequest(WebInterface.SETUPINFO_URL, WebKit.mediaJson, data)?.string()
//                        response?.run {
//                            Log.d("this",this)
//                            val model = WebKit.gson.fromJson(this, InfoResponse::class.java)
//                            when (model.stateCode) {
//                                200 -> {
//                                    activity?.runOnUiThread {
//                                        Toast.makeText(context!!,"信息设置成功！",Toast.LENGTH_SHORT).show()
//                                        activity!!.finish()
//                                    }
//                                }
//                                else -> {
//                                    activity?.runOnUiThread {
//                                        Toast.makeText(context!!,"信息设置失败!请重试",Toast.LENGTH_SHORT).show()
//
//                                    }
//                                }
//                            }
//                            activity?.runOnUiThread {
//                                progressBar2.visibility = NetworkView.GONE
//                                nick_name_setup_info.isEnabled = true
//                                phone_input_setup_info.isEnabled = true
//                                address_input_setup_info.isEnabled = true
////                                gender_spinner_setup_info.isEnabled = true
////                                identity_spinner_setup_info.isEnabled = true
//                                ok_button_setup_info.isEnabled = true
//                                textView7.isEnabled = true
//                                gender_text_setup_info.isEnabled = true
//                                chip.isEnabled = true
//
//                                //activity!!.finish()
//                            }
//                            }
//                        }.start()
//                    }

            }
        }
//        username_setup_info.text = activity?.myApplication?.loginUser?.name
        chip.setOnClickListener {
            Log.d("is click", "is click")
            val popupMenu = PopupMenu(context!!, chip)
            popupMenu.menu.run {
                if (tencentLocation != null) {
                    this.clear()
                    listAdress.clear()
                    if (tencentLocation!!.address != "")
                        listAdress.add(tencentLocation!!.address)
                    else
                        tencentLocation!!.run {
                            Log.d("adress is null", "address")
                            listAdress.add((province + city + district + town + village + street + streetNo).split("Unknown").first())
                        }

                    tencentLocation!!.poiList.forEach {
                        listAdress.add(it.address)
                        it.address.logIt("address")
                    }
                    for (i in 0 until listAdress.size) {
                        add(Menu.NONE, Menu.FIRST + i, i, listAdress[i])
                    }
                    Log.d("size", tencentLocation!!.poiList.size.toString())
                } else {
                    Toast.makeText(context!!, "无法获取地理位置", Toast.LENGTH_SHORT).show()
                }
            }
            popupMenu.setOnMenuItemClickListener {
                val i = it.itemId - Menu.FIRST
                tencentLocation?.run {
                    address_input_setup_info.editText?.text?.run {
                        clear()
                        append(listAdress[i])


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
// TODO: Rename method, update argument and hook method into UI event

    override fun onDetach() {
        super.onDetach()
        listener = null
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
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SetupInfoFragment.
         */
        // TODO: Rename and change types and number of parameters
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
            transucentSystemUI(true)
        }
        super.onResume()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        data?.run {
            data.data?.run {
                Log.d("url", this.path)
                when (requestCode) {
                    photoResult -> {
                        imageView_setup.setImageBitmap(context!!.getBitmapFromUri(this))
                        imageView_setup.scaleType = ImageView.ScaleType.CENTER_CROP
                        textView_add_photo_setup.text = "更换照片"
                        avatarFile = File(context!!.getPath(this))
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)

    }

    override fun getViewContext() = context!!
    override fun onInitUserInfoFailed(jsonObject: JsonObject) {
        context!!.toast("设置信息失败")
    }

    override fun onInitUserInfoSuccess(jsonObject: JsonObject) {

        val userInfo = Attributes.loginUserInfo!!
        userInfo.token = jsonObject.getAsJsonObject("data").get("newToken").asString
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
        activity?.finish()
    }

    override fun onSetupAvatarFailed() {
        context?.toast("头像上传失败")
    }

    private fun getIndexForChip(@IntegerRes id: Int): Int {
        return when (id) {
            R.id.chipIdStudent, R.id.chipMaleSetupInfo -> 1
            R.id.chipIdTeacher, R.id.chipFemaleSetupInfo -> 2
            R.id.chipIdOther -> 3
            else -> 0
        }
    }
}

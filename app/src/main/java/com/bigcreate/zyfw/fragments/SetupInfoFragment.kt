package com.bigcreate.zyfw.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import com.bigcreate.library.*
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.base.WebInterface
import com.bigcreate.zyfw.base.appCompactActivity
import com.bigcreate.zyfw.base.myApplication
import com.bigcreate.zyfw.models.InfoRequire
import com.bigcreate.zyfw.models.InfoResponse
import com.tencent.map.geolocation.TencentLocation
import com.tencent.map.geolocation.TencentLocationListener
import com.tencent.map.geolocation.TencentLocationManager
import com.tencent.map.geolocation.TencentLocationRequest
import kotlinx.android.synthetic.main.activity_release_project.*
import kotlinx.android.synthetic.main.fragment_setup_info.*
import kotlin.math.log

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
class SetupInfoFragment : Fragment(),TencentLocationListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private var tencentLocation: TencentLocation? = null
    private var listAdress = ArrayList<String>()
    private var imageString = ""
    val PHOTORESULT = 3
    val IMAGE_UNSPECIFIED = "image/*"
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
        activity?.intent?.putExtra("type","setup_info")
        val isSetup = activity?.intent?.getStringExtra("type")
        if (isSetup == null || isSetup != "setup_info") {
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
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,IMAGE_UNSPECIFIED)
            startActivityForResult(intent,PHOTORESULT)
        }
        ok_button_setup_info.setOnClickListener {
            Log.d("click","click ok")
            //val response = WebKit.okClient.postRequest()
            if (nick_name_setup_info.editText!!.isEmpty()||address_input_setup_info.editText!!.isEmpty()||phone_input_setup_info.editText!!.isEmpty()||imageString =="")
                Toast.makeText(context!!,"你还有尚未填写的资料，请填好后重试",Toast.LENGTH_SHORT).show()
            else {
                activity?.myApplication?.loginUser?.run {
                    progressBar2.visibility = View.VISIBLE
                    textView7.isEnabled = false
                    gender_text_setup_info.isEnabled = false
                    nick_name_setup_info.isEnabled = false
                    phone_input_setup_info.isEnabled = false
                    address_input_setup_info.isEnabled = false
                    gender_spinner_setup_info.isEnabled = false
                    identity_spinner_setup_info.isEnabled = false
                    ok_button_setup_info.isEnabled = false
                    chip.isEnabled = false
                    Thread {
                        val setupInfoRequire = InfoRequire(name, nick_name_setup_info.editText!!.string(), gender_spinner_setup_info.selectedItem as String,
                                identity_spinner_setup_info.selectedItem as String, address_input_setup_info.editText!!.string(), phone_input_setup_info.editText!!.string(),imageString)
                        val data = WebKit.gson.toJson(setupInfoRequire)
                        Log.d("json", data)
                        val response = WebKit.okClient.postRequest(WebInterface.SETUPINFO_URL, WebKit.mediaJson, data)?.string()
                        response?.run {
                            Log.d("this",this)
                            val model = WebKit.gson.fromJson(this, InfoResponse::class.java)
                            when (model.stateCode) {
                                "200" -> {
                                    activity?.runOnUiThread {
                                        Toast.makeText(context!!,"信息设置成功！",Toast.LENGTH_SHORT).show()
                                        activity!!.finish()
                                    }
                                }
                                else -> {
                                    activity?.runOnUiThread {
                                        Toast.makeText(context!!,"信息设置失败!请重试",Toast.LENGTH_SHORT).show()

                                    }
                                }
                            }
                            activity?.runOnUiThread {
                                progressBar2.visibility = View.GONE
                                nick_name_setup_info.isEnabled = true
                                phone_input_setup_info.isEnabled = true
                                address_input_setup_info.isEnabled = true
                                gender_spinner_setup_info.isEnabled = true
                                identity_spinner_setup_info.isEnabled = true
                                ok_button_setup_info.isEnabled = true
                                textView7.isEnabled = true
                                gender_text_setup_info.isEnabled = true
                                chip.isEnabled = true

                                //activity!!.finish()
                            }
                            }
                        }.start()
                    }

            }
        }
        username_setup_info.text = activity?.myApplication?.loginUser?.name
        chip.setOnClickListener {
            Log.d("is click","is click")
                val popupMenu = PopupMenu(context!!,chip)
                popupMenu.menu.run {
                    if (tencentLocation != null) {
                        this.clear()
                        listAdress.clear()
                        if (tencentLocation!!.address!= "")
                            listAdress.add(tencentLocation!!.address)
                        else
                            tencentLocation!!.run {
                                Log.d("adress is null","address")
                                listAdress.add((province+city+district+ town + village + street + streetNo).split("Unknown").first())
                            }

                        tencentLocation!!.poiList.forEach {
                            listAdress.add(it.address)
                            it.address.logIt("address")
                        }
                        for(i in 0 until listAdress.size) {
                            add(Menu.NONE,Menu.FIRST + i,i,listAdress[i])
                        }
                        Log.d("size",tencentLocation!!.poiList.size.toString())
                    }else{
                        Toast.makeText(context!!,"无法获取地理位置",Toast.LENGTH_SHORT).show()
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
                Log.d("is show","show")
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
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
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
        Log.d("hhh","hhhhh")
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
                    PHOTORESULT -> {
                        imageView_setup.setImageBitmap(context!!.getBitmapFromUri(this))
                        context!!.getBitmapFromUri(this).toBase64()?.run {
                            imageString = this
                            Log.d("bitMap", this)
                        }
                        imageView_setup.scaleType = ImageView.ScaleType.CENTER_INSIDE
                        textView_add_photo_setup.visibility = View.GONE
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)

    }
}

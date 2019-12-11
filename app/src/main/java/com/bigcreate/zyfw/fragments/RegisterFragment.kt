package com.bigcreate.zyfw.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.bigcreate.library.fromJson
import com.bigcreate.library.toast
import com.bigcreate.library.valueOrNotNull
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.base.RemoteService
import com.bigcreate.zyfw.base.appCompactActivity
import com.bigcreate.zyfw.callback.enqueue
import com.bigcreate.zyfw.models.LoginModel
import com.bigcreate.zyfw.models.RegisterRequest
import com.bigcreate.zyfw.mvp.user.RegisterImpl
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.android.synthetic.main.layout_org_select.*
import kotlinx.android.synthetic.main.layout_org_select.view.*
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the [SignUpFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class SignUpFragment : Fragment(), RegisterImpl.View {
    private var isForgetPass = false
    private var param1: String? = null
    private var param2: String? = null
    private var isSendCode = false
    private val presenter = RegisterImpl(this)
    private lateinit var provinceView:View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        isForgetPass = activity?.intent?.getBooleanExtra("isResetPassword", false).valueOrNotNull

        appCompactActivity?.setSupportActionBar(toolbarRegister)
        appCompactActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        provinceView = layoutInflater.inflate(R.layout.layout_org_select,null,false)
        provinceView.dropdownOrgProvince.setOnItemClickListener { parent, view, position, id ->
            val text = (view as TextView).text
            Log.e("select Province",text.toString())
            RemoteService.getOrgCity(text.toString()).enqueue {
                response {
                    body()?.apply {
                        provinceView.dropdownOrgCity.text.clear()
                        provinceView.dropdownOrgCity.setAdapter(ArrayAdapter(provinceView.context,R.layout.dropdown_menu_popup_item, get("cities").toString().fromJson<List<String>>()))
                    }
                }
            }
        }
        provinceView.dropdownOrgCity.setOnItemClickListener { parent, view, position, id ->
            val text = (view as TextView).text
            Log.e("select City",text.toString())
            RemoteService.getOrg(text.toString()).enqueue {
                response {
                    body()?.apply {
                        provinceView.dropdownOrg.text.clear()
                        provinceView.dropdownOrg.setAdapter(ArrayAdapter(provinceView.context,R.layout.dropdown_menu_popup_item, get("schools").toString().fromJson<List<String>>()))
                    }
                }
            }
        }
        val dialog = MaterialAlertDialogBuilder(view!!.context)
                .setTitle("选择机构")
                .setView(provinceView)
                .setPositiveButton("确定") { dialog, which ->
                    dropdownSchoolRegister.text.clear()
                    dropdownSchoolRegister.text.append(provinceView.dropdownOrg.text.toString())
                }
                .setNegativeButton("取消") { dialog, which ->
                    dialog.dismiss()
                }.create()

        if (isForgetPass) {
            appCompactActivity?.supportActionBar?.title = "忘记密码"
            layoutPassRegister.hint = "新密码"
            buttonSubmitRegister.text = "立即更改"
            layoutIdentifyRegister.isVisible = false
            layoutRealNameRegister.isVisible = false
            layoutSchoolNameRegister.isVisible = false
        }else {
//            dropdownSchoolRegister.setAdapter(ArrayAdapter(dropdownSchoolRegister.context,R.layout.dropdown_menu_popup_item, arrayOf(
//                    "桂林电子科技大学"
//            )))
            dropdownSchoolRegister.setOnClickListener {
                lifecycleScope.launch(Dispatchers.Main) {
                    provinceView.dropdownOrgProvince.text.clear()
                    RemoteService.getOrgProvince().enqueue {
                        response {
                            body()?.apply {
                                provinceView.dropdownOrgProvince.setAdapter(ArrayAdapter(provinceView.context,R.layout.dropdown_menu_popup_item, get("provinces").toString().fromJson<List<String>>()))
                            }
                        }
                    }
                }
                dialog.show()
            }
            layoutSchoolNameRegister.setEndIconOnClickListener {
                lifecycleScope.launch(Dispatchers.Main) {
                    provinceView.dropdownOrgProvince.text.clear()
                    RemoteService.getOrgProvince().enqueue {
                        response {
                            body()?.apply {
                                provinceView.dropdownOrgProvince.setAdapter(ArrayAdapter(provinceView.context,R.layout.dropdown_menu_popup_item, get("provinces").toString().fromJson<List<String>>()))
                            }
                        }
                    }
                }
                dialog.show()
            }
        }
        toolbarRegister.setNavigationOnClickListener {
            activity?.finish()
        }
        buttonSubmitRegister.setOnClickListener {
            attemptLogin()
        }
        inputValidCodeRegister.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })
        toolbarRegister.requestApplyInsets()
        buttonSendValidCodeRegister.setOnClickListener {
            if (inputPhoneRegister.text.toString().length != 11) {
                inputPhoneRegister.error = "请输入手机号"
                inputPhoneRegister.requestFocus()
            } else {
                isSendCode = true
                presenter.doSendValidCode(inputPhoneRegister.text.toString())
            }
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }


    private fun attemptLogin() {

        // Reset errors.
        inputPhoneRegister.error = null
        inputPassRegister.error = null
        buttonSubmitRegister.isEnabled = false
        // Store values at the time of the login attempt.
        val emailStr = inputPhoneRegister.text.toString()
        val passwordStr = inputPassRegister.text.toString()

        var cancel = false
        var focusView: View? = null

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(passwordStr) && !isPasswordValid(passwordStr)) {
            inputPhoneRegister.error = getString(R.string.error_invalid_password)
            focusView = inputPassRegister
            cancel = true
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(emailStr)) {
            inputPhoneRegister.error = getString(R.string.error_field_required)
            focusView = inputPhoneRegister
            cancel = true
        } else if (!isPhoneValid(emailStr)) {
            inputPhoneRegister.error = getString(R.string.error_invalid_email)
            focusView = inputPhoneRegister
            cancel = true
        } else if( inputIdentifyRegister.text.toString().isNullOrEmpty() && !isForgetPass) {
            inputIdentifyRegister.error = getString(R.string.error_invalid_email)
            focusView = inputIdentifyRegister
            cancel = true
        } else if (inputRealNameRegister.text.toString().isNullOrEmpty() && !isForgetPass) {
            inputRealNameRegister.error = getString(R.string.error_invalid_email)
            focusView = inputRealNameRegister
            cancel = true
        } else if (inputValidCodeRegister.text.toString().isNullOrEmpty() && !isForgetPass) {
            inputValidCodeRegister.error = getString(R.string.error_invalid_email)
            focusView = inputValidCodeRegister
            cancel = true
        } else if (dropdownSchoolRegister.text.toString().isNullOrEmpty() && !isForgetPass) {
            dropdownSchoolRegister.error = getString(R.string.error_invalid_email)
            focusView = dropdownSchoolRegister
            cancel = true
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView?.requestFocus()
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true)
            if (isForgetPass)
                presenter.doResetPassword(RegisterRequest(emailStr, passwordStr, "","", inputValidCodeRegister.text.toString(),""))
            else
                presenter.doRegister(RegisterRequest(emailStr, passwordStr, inputIdentifyRegister.text.toString(),inputRealNameRegister.text.toString(), inputValidCodeRegister.text.toString(),dropdownSchoolRegister.text.toString()))
        }
        buttonSubmitRegister.isEnabled = true
    }

    private fun isPhoneValid(email: String): Boolean {
        return email.length == 11
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 4
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private fun showProgress(show: Boolean) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

        scrollRegister?.visibility = if (show) View.GONE else View.VISIBLE
        scrollRegister.animate()
                .setDuration(shortAnimTime)
                .alpha((if (show) 0 else 1).toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        scrollRegister?.visibility = if (show) View.GONE else View.VISIBLE
                    }
                })

        progressRegister?.visibility = if (show) View.VISIBLE else View.GONE
        progressRegister.animate()
                .setDuration(shortAnimTime)
                .alpha((if (show) 1 else 0).toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        progressRegister?.visibility = if (show) View.VISIBLE else View.GONE
                    }
                })
    }

    override fun onRegisterFailed(response: JsonObject) {
        if (isForgetPass)
            context?.toast("重置密码失败")
        else
            context?.toast("注册失败")
    }

    override fun onRegisterSuccess(loginModel: LoginModel) {
        Attributes.loginUserInfo = loginModel
        if (isForgetPass) {
            context?.toast("重置成功,请重新登录")
            activity?.finish()
        }
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.run {
            replace(R.id.containerRegister, SetupInfoFragment("setupInfo",dropdownSchoolRegister.text.toString()))
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            addToBackStack(null)
            commit()
        }
    }

    override fun onValidCodeFail(response: JsonObject) {
        context?.toast("验证码发送失败")
    }

    override fun getViewContext(): Context {
        return context!!
    }


    override fun onValidCodeSend() {
        buttonSendValidCodeRegister.isEnabled = false
        context?.toast("验证码已发送到您的手机，请注意查收")
        GlobalScope.launch {
            for (i in 60 downTo 1) {
                withContext(Dispatchers.Main) {
                    buttonSendValidCodeRegister?.text = getString(R.string.reSendTimeVar, i)
                }
                delay(1000)
            }
            withContext(Dispatchers.Main) {
                buttonSendValidCodeRegister?.text = "重新发送"
                buttonSendValidCodeRegister?.isEnabled = true
            }
        }
    }

    override fun onRequesting() {
        if (!isSendCode)
            showProgress(true)
    }

    override fun onRequestFinished() {
        if (!isSendCode)
            showProgress(false)
        isSendCode = false
    }

    override fun onNetworkFailed() {
        context?.toast("网络似乎出现了点问题")
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
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
         * @return A new instance of fragment SignUpFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                SignUpFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}

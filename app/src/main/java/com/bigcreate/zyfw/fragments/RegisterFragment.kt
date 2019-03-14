package com.bigcreate.zyfw.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.bigcreate.library.toast
import com.bigcreate.library.valueOrNotNull
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.base.appCompactActivity
import com.bigcreate.zyfw.models.LoginModel
import com.bigcreate.zyfw.models.RegisterRequest
import com.bigcreate.zyfw.mvp.user.RegisterImpl
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.coroutines.*

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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        isForgetPass = activity?.intent?.getBooleanExtra("isResetPassword",false).valueOrNotNull

        appCompactActivity?.setSupportActionBar(toolbarRegister)
        appCompactActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (isForgetPass) {
            appCompactActivity?.supportActionBar?.title = "忘记密码"
            inputPassRegister.hint = "新密码"
            buttonSubmitRegister.text = "立即更改"
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
                presenter.doResetPassword(RegisterRequest(emailStr, passwordStr, inputValidCodeRegister.text.toString()))
            else
                presenter.doRegister(RegisterRequest(emailStr, passwordStr, inputValidCodeRegister.text.toString()))
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
            replace(R.id.containerRegister, SetupInfoFragment())
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
                    buttonSendValidCodeRegister.text = getString(R.string.reSendTimeVar,i)
                }
                delay(1000)
            }
            withContext(Dispatchers.Main) {
                buttonSendValidCodeRegister.text = "重新发送"
                buttonSendValidCodeRegister.isEnabled = true
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

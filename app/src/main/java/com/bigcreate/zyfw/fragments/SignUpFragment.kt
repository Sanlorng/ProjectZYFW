package com.bigcreate.zyfw.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.net.Uri
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
import com.bigcreate.library.transucentSystemUI
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.base.appCompactActivity
import com.bigcreate.zyfw.models.LoginModel
import com.bigcreate.zyfw.models.RegisterRequest
import com.bigcreate.zyfw.mvp.user.RegisterContract
import com.bigcreate.zyfw.mvp.user.RegisterImpl
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_sign_up.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SignUpFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [SignUpFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class SignUpFragment : Fragment(), RegisterContract.NetworkView {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
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
        appCompactActivity?.setSupportActionBar(toolbar_sign_up)
        appCompactActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_sign_up.setNavigationOnClickListener {
            activity?.finish()
        }
        phone_sign_up_button.setOnClickListener {
            attemptLogin()
        }
        password_sign_up.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })
        buttonSendValidCodeSignUp.setOnClickListener {
            if (phone_sign_up.text.toString().length != 11) {
                phone_sign_up.error = "请输入手机号"
                phone_sign_up.requestFocus()
            } else
                presenter.doSendValidCode(phone_sign_up.text.toString())
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }


    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    //    @SuppressLint("StaticFieldLeak")
//    inner class UserLoginTask internal constructor(private val mEmail: String, private val mPassword: String) : AsyncTask<Void, Void, Boolean>() {
//
//        override fun doInBackground(vararg params: Void): Boolean? {
//            // TODO: attempt authentication against a network service.
//
//            return try {
//                tryLoginTask()
//                activity?.myApplication?.loginToken != null
//            } catch (e: Throwable) {
//                false
//            }
//
//        }
//
//        override fun onPostExecute(success: Boolean?) {
//            mAuthTask = null
//            showProgress(false)
//
//            if (success!!) {
//                activity?.myApplication?.run {
//                    loginUser = User(phone_sign_up.text.toString(), password_sign_up.text.toString(),loginToken!!,"12")
//                    loginUser?.run {
//                        Log.d("name",name)
//                        Log.d("pass",password)
//                    }
//
//                }
//                val transaction = activity?.supportFragmentManager?.beginTransaction()
//                transaction?.run {
//                    replace(R.id.container_sign_up,SetupInfoFragment())
//                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//                    addToBackStack(null)
//                    commit()
//                }
//            } else {
//                password_sign_up.error = getString(R.string.error_incorrect_password)
//                password_sign_up.requestFocus()
//            }
//        }
//
//        override fun onCancelled() {
//            mAuthTask = null
//            showProgress(false)
//        }
//
//        private fun tryLoginTask() {
//            var temp: String? = null
//            activity?.myApplication?.run {
//                //TODO
////                val registerRequire = RegisterRequire(ipAddress, phone_sign_up.text.toString(), password_sign_up.text.toString())
////                val data = WebKit.gson.toJson(registerRequire)
//                val data = ""
//                Log.d("lisss",data)
//                mResponseString = WebKit.okClient.postRequest(WebInterface.REGISTER_URL, WebInterface.TYPE_JSON, data)?.string()
//                Log.d("responseString",mResponseString)
//                loginToken = WebKit.gson.fromJson(mResponseString,RegisterResponse::class.java).token
//
//            }
//
//        }
//    }
    private fun attemptLogin() {
//        if (mAuthTask != null) {
//            return
//        }

        // Reset errors.
        phone_sign_up.error = null
        password_sign_up.error = null
        phone_sign_up_button.isEnabled = false
        // Store values at the time of the login attempt.
        val emailStr = phone_sign_up.text.toString()
        val passwordStr = password_sign_up.text.toString()

        var cancel = false
        var focusView: View? = null

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(passwordStr) && !isPasswordValid(passwordStr)) {
            phone_sign_up.error = getString(R.string.error_invalid_password)
            focusView = password_sign_up
            cancel = true
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(emailStr)) {
            phone_sign_up.error = getString(R.string.error_field_required)
            focusView = phone_sign_up
            cancel = true
        } else if (!isPhoneValid(emailStr)) {
            phone_sign_up.error = getString(R.string.error_invalid_email)
            focusView = phone_sign_up
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
//            mAuthTask = UserLoginTask(emailStr, passwordStr)
//            mAuthTask!!.execute(null as Void?)
            presenter.doRegister(RegisterRequest(emailStr, passwordStr, textValidCodeRegister.text.toString()))
        }
        phone_sign_up_button.isEnabled = true
    }

    private fun isPhoneValid(email: String): Boolean {
        //TODO: Replace this with your own logic
        return email.length == 11
    }

    private fun isPasswordValid(password: String): Boolean {
        //TODO: Replace this with your own logic
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

        sign_up_form?.visibility = if (show) View.GONE else View.VISIBLE
        sign_up_form.animate()
                .setDuration(shortAnimTime)
                .alpha((if (show) 0 else 1).toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        sign_up_form?.visibility = if (show) View.GONE else View.VISIBLE
                    }
                })

        sign_up_progress?.visibility = if (show) View.VISIBLE else View.GONE
        sign_up_progress.animate()
                .setDuration(shortAnimTime)
                .alpha((if (show) 1 else 0).toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        sign_up_progress?.visibility = if (show) View.VISIBLE else View.GONE
                    }
                })
    }

    override fun onRegisterFaild(response: JsonObject) {
        context?.toast("注册失败")
    }

    override fun onRegisterSuccess(loginModel: LoginModel) {
        Attributes.loginUserInfo = loginModel
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.run {
            replace(R.id.container_sign_up, SetupInfoFragment())
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
        buttonSendValidCodeSignUp.isEnabled = false
        context?.toast("验证码已发送到您的手机，请注意查收")
        GlobalScope.launch {
            for (i in 60 downTo 1) {
                launch(Dispatchers.Main) {
                    buttonSendValidCodeSignUp.text = "重新发送(${i}s)"
                }
                delay(1000)
            }
            launch(Dispatchers.Main) {
                buttonSendValidCodeSignUp.text = "重新发送"
                buttonSendValidCodeSignUp.isEnabled = true
            }
        }
    }

    override fun onRequesting() {
        showProgress(true)
    }

    override fun onRequestFinished() {

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
    interface OnFragmentInteractionListener {
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SignUpFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                SignUpFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

    override fun onResume() {
        activity?.window?.run {
            transucentSystemUI(true)
        }
        super.onResume()
    }
}

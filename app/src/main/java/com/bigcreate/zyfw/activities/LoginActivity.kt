package com.bigcreate.zyfw.activities

import android.Manifest.permission.READ_CONTACTS
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bigcreate.library.startActivity
import com.bigcreate.library.toast
import com.bigcreate.library.translucentSystemUI
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.base.*
import com.bigcreate.zyfw.models.LoginModel
import com.bigcreate.zyfw.models.LoginRequest
import com.bigcreate.zyfw.mvp.user.LoginImpl
import com.bigcreate.zyfw.viewmodel.LoginStatus
import com.bigcreate.zyfw.viewmodel.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_login.*

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity(), LoginImpl.View {
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private val loginPresenter = LoginImpl(this)
    private lateinit var loginViewModel: LoginViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setSupportActionBar(toolbarLogin)
        loginViewModel = ViewModelHelper.getAppViewModelProvider(application as MyApplication)[LoginViewModel::class.java]
        if (loginViewModel.userInfo.value != null) {
            finish()
        }
        loginViewModel.loginStatus.observe(this, Observer {
            when(it) {
                LoginStatus.STATUS_LOGIN -> {
                    showProgress(true)
                }
                LoginStatus.SUCCESS -> {
                    showProgress(false)
                    finish()
                }
                else -> {

                }
            }
        })
        window.translucentSystemUI(true)
        toolbarLogin.setNavigationOnClickListener {
            finish()
        }
        supportActionBar?.title = getString(R.string.action_sign_in)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // Set up the login form.
        populateAutoComplete()
        inputPassLogin.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })

        buttonActionLogin.setOnClickListener {
            attemptLogin()
        }
        textStartSignUpLogin.setOnClickListener {
            startActivity<RegisterActivity>()
        }
        textStartResetLogin.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java).apply {
                putExtra("isResetPassword", true)
            })
        }
    }

    override fun onResume() {
//        if (Attributes.loginUserInfo != null)
//            finish()
        super.onResume()
    }

    private fun populateAutoComplete() {
        if (!mayRequestContacts()) {
            return
        }
    }

    private fun mayRequestContacts(): Boolean {
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(inputPhoneLogin, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok
                    ) { requestPermissions(arrayOf(READ_CONTACTS), REQUEST_READ_CONTACTS) }
        } else {
            requestPermissions(arrayOf(READ_CONTACTS), REQUEST_READ_CONTACTS)
        }
        return false
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete()
            }
        }
    }

    override fun onLoginFailed(response: JsonObject) {
        toast("登录失败")
        toast("s")
    }

    override fun onLoginSuccess(loginInfo: LoginModel) {
        Attributes.loginUserInfo = loginInfo
        setResult(ResultCode.OK)
        finish()
    }

    override fun getViewContext(): Context {
        return this
    }

    override fun onNetworkFailed() {
        toast("网络连接失败")
    }

    override fun onRequesting() {
        showProgress(true)
    }

    override fun onRequestFinished() {
        showProgress(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        loginPresenter.detachView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RequestCode.REGISTER -> if (resultCode == ResultCode.OK) {
                setResult(ResultCode.OK);finish()
            }
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private fun attemptLogin() {
        buttonActionLogin.isEnabled = false
        // Reset errors.
        inputPhoneLogin.error = null
        inputPassLogin.error = null

        // Store values at the time of the login attempt.
        val emailStr = inputPhoneLogin.text.toString()
        val passwordStr = inputPassLogin.text.toString()

        var cancel = false
        var focusView: View? = null

        if (!TextUtils.isEmpty(passwordStr) && !isPasswordValid(passwordStr)) {
            inputPassLogin.error = getString(R.string.error_invalid_password)
            focusView = inputPassLogin
            cancel = true
        }

        if (TextUtils.isEmpty(emailStr)) {
            inputPhoneLogin.error = getString(R.string.error_field_required)
            focusView = inputPhoneLogin
            cancel = true
        } else if (!isPhoneValid(emailStr)) {
            inputPhoneLogin.error = getString(R.string.error_invalid_email)
            focusView = inputPhoneLogin
            cancel = true
        }

        if (cancel) {
            focusView?.requestFocus()
        } else {
            showProgress(true)
            loginPresenter.doRequest(LoginRequest(emailStr,passwordStr))
            //loginViewModel.tryLogin(emailStr, passwordStr)
        }
        buttonActionLogin.isEnabled = true
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

        formLogin.visibility = if (show) View.GONE else View.VISIBLE
        formLogin.animate()
                .setDuration(shortAnimTime)
                .alpha((if (show) 0 else 1).toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        formLogin.visibility = if (show) View.GONE else View.VISIBLE
                    }
                })

        progressLogin.visibility = if (show) View.VISIBLE else View.GONE
        progressLogin.animate()
                .setDuration(shortAnimTime)
                .alpha((if (show) 1 else 0).toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        progressLogin.visibility = if (show) View.VISIBLE else View.GONE
                    }
                })
    }

    companion object {

        /**
         * Id to identity READ_CONTACTS permission request.
         */
        private const val REQUEST_READ_CONTACTS = 0

    }

}

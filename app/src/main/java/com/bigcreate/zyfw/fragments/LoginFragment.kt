package com.bigcreate.zyfw.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bigcreate.zyfw.base.MyApplication
import com.bigcreate.zyfw.base.ViewModelHelper
import com.bigcreate.zyfw.viewmodel.LoginViewModel

abstract class LoginFragment: Fragment() {
    private lateinit var loginViewModel: LoginViewModel
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        loginViewModel = ViewModelHelper.getAppViewModelProvider(activity!!.application as MyApplication)[LoginViewModel::class.java]
        super.onActivityCreated(savedInstanceState)
    }
    fun getLoginViewModel():LoginViewModel {
        return loginViewModel
    }
}
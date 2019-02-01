package com.bigcreate.zyfw.mvp.base

import kotlinx.coroutines.Job

interface BasePresenter {
    fun detachView()
    fun cancelJob()
}
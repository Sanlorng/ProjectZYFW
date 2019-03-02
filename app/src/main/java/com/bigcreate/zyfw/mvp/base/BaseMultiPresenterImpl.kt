package com.bigcreate.zyfw.mvp.base

import kotlinx.coroutines.Job


abstract class BaseMultiPresenterImpl<V : BaseView>(var mView: V?) : BasePresenter {
    private val jobs = ArrayList<Job?>()
    override fun cancelJob() {
        jobs.forEach {
            it?.cancel()
        }
        jobs.clear()
    }

    override fun detachView() {
        cancelJob()
        mView = null
    }

    fun addJob(job: Job?) {
        jobs.add(job)
    }

}
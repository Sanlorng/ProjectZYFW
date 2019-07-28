package com.bigcreate.zyfw.mvp.base

class PresenterImpl<R, D, V : BaseView>(view: V) : BasePresenterImpl<R, D, V>(view) {
    private var requestSuccessBlock: (D?.() -> Unit)? = null
    private var dataBlock: (R.() -> D?)? = null
    fun data(block: (R.() -> D?)) {
        dataBlock = block
    }

    fun success(block: (D?.() -> Unit)) {
        requestSuccessBlock = block
    }

    override fun afterRequestSuccess(data: D?) {
        requestSuccessBlock?.invoke(data)
    }

    override fun backgroundRequest(request: R): D? {
        return dataBlock?.invoke(request)
    }
}

fun <R, D, V : BaseView> presenterImpl(view: V, block: BasePresenterImpl<R, D, V>.() -> Unit): BasePresenterImpl<R, D, V> {
    val impl = PresenterImpl<R, D, V>(view)
    block(impl)
    return impl
}
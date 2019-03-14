package com.bigcreate.zyfw.fragments

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.bigcreate.library.toast
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.callback.CommentCallBack
import com.bigcreate.zyfw.callback.FillTextCallBack
import com.bigcreate.zyfw.models.CreateCommentRequest
import com.bigcreate.zyfw.mvp.project.CreateCommentImpl
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_comment_dialog.*

class CommentDialogFragment : DialogFragment(), View.OnClickListener, TextWatcher, CreateCommentImpl.View {
    var fillTextCallBack: FillTextCallBack? = null
    var commentCallBack: CommentCallBack? = null
    private var createCommentImpl = CreateCommentImpl(this)
    private lateinit var editText: EditText
    lateinit var button: ImageView
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(context!!, R.style.bottomDialog)
        dialog.run {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.fragment_comment_dialog)
            setCanceledOnTouchOutside(true)
            window?.run {
                attributes.gravity = Gravity.BOTTOM
                attributes.width = WindowManager.LayoutParams.MATCH_PARENT
            }
            sendCommentDialog.setOnClickListener(this@CommentDialogFragment)
            editText = inputCommentDialog
            button = sendCommentDialog
            inputCommentDialog.addTextChangedListener(this@CommentDialogFragment)
            window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
        return dialog
    }

    override fun onHiddenChanged(hidden: Boolean) {
        if (hidden.not()) {
            editText.text.clear()
            editText.text.append(fillTextCallBack?.getTextContent())
        }
        super.onHiddenChanged(hidden)
    }

    override fun onResume() {
        editText.text.clear()
        editText.text.append(fillTextCallBack?.getTextContent())
        if (editText.text.toString().isEmpty()) {
            button.isEnabled = false
            button.setColorFilter(ContextCompat.getColor(context!!, R.color.color737373))
        } else {
            button.isEnabled = true
            button.setColorFilter(ContextCompat.getColor(context!!, R.color.colorAccent))
        }
        editText.requestFocus()
        super.onResume()
    }

    override fun onDismiss(dialog: DialogInterface) {
        fillTextCallBack?.setTextContent(editText.text.toString())
        super.onDismiss(dialog)
    }

    override fun onClick(v: View?) {
        if (v != null) when (v.id) {
            R.id.sendCommentDialog -> {
                val projectId = fillTextCallBack!!.getProjectId()
                Attributes.loginUserInfo?.run {
                    createCommentImpl.doRequest(CreateCommentRequest(
                            comment = editText.text.toString(),
                            projectId = projectId,
                            token = token,
                            username = username
                    ))
                }
            }
        }
    }

    override fun onCreateCommentFailed(jsonObject: JsonObject) {
        context?.toast("评论失败，错误代码：${jsonObject.get("code").asString}")
    }

    override fun onCreateCommentSuccess(jsonObject: JsonObject) {
        editText.text.clear()
        context?.toast("评论成功")
        dismiss()
        commentCallBack?.commentSuccess()
    }

    override fun getViewContext(): Context {
        return context!!
    }

    override fun onNetworkFailed() {
        context?.toast("网络出了点差错")
    }

    override fun onRequesting() {

    }

    override fun onRequestFinished() {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    override fun afterTextChanged(s: Editable?) {
        if (s == null || s.isEmpty()) {
            button.isEnabled = false
            button.setColorFilter(ContextCompat.getColor(context!!, R.color.color737373))
        } else {
            button.isEnabled = true
            button.setColorFilter(ContextCompat.getColor(context!!, R.color.colorAccent))
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }
}
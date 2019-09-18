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
import com.bigcreate.zyfw.models.CreateCommentRequest
import com.bigcreate.zyfw.mvp.project.CreateCommentImpl
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_comment_dialog.*

class CommentDialogFragment : DialogFragment(), View.OnClickListener, TextWatcher{
    //var fillTextCallBack: FillTextCallBack? = null
    var commentCallBack: CommentCallback? = null
//    private var createCommentImpl = CreateCommentImpl(this)
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
            editText.text.append(commentCallBack?.getCommentContent())
        }
        super.onHiddenChanged(hidden)
    }

    override fun onResume() {
        editText.text.clear()
        editText.text.append(commentCallBack?.getCommentContent()?:"")
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

    override fun onDestroy() {
        super.onDestroy()
        //commentCallBack = null
    }
    override fun onDismiss(dialog: DialogInterface) {
        commentCallBack?.setCommentContent(editText.text.toString())
        super.onDismiss(dialog)
    }
    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.sendCommentDialog -> commentCallBack?.onCommentDone(editText.text.toString())
        }

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    override fun afterTextChanged(s: Editable?) {
        if (s.isNullOrEmpty()) {
            button.isEnabled = false
            button.setColorFilter(ContextCompat.getColor(context!!, R.color.color737373))
        } else {
            button.isEnabled = true
            button.setColorFilter(ContextCompat.getColor(context!!, R.color.colorAccent))
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }
    interface CommentCallback {
        fun getCommentContent():CharSequence
        fun setCommentContent(content: String)
        fun onCommentDone(content: String)
    }
}
package com.anguyen.mymap.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.anguyen.mymap.R
import com.anguyen.mymap.commons.initProgress
import com.anguyen.mymap.commons.onClick
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.logout_revoke_dialog.*


class RevokeDialogFragment(
    private val revokeDialogListener: RevokeDialogListener,
    private val progressDialog: KProgressHUD
) : DialogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.logout_revoke_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDialog.dismiss()

        txt_check.onClick {
            txt_check.isChecked = !txt_check.isChecked

            if(txt_check.isChecked){
                txt_detail.text = getText(R.string.revoke_option1)
                txt_check.checkMarkDrawable = activity?.getDrawable(R.drawable.ic_checked)
            }else{
                txt_detail.text = getText(R.string.revoke_option2)
                txt_check.checkMarkDrawable = activity?.getDrawable(R.drawable.ic_unchecked)
            }

        }

        btn_logout_confirm.onClick {
            progressDialog.show()
            revokeDialogListener.onRevokeConfirmedListener(txt_check.isChecked)
        }

    }

    interface RevokeDialogListener{
        fun onRevokeConfirmedListener(isChecked: Boolean)
    }

}

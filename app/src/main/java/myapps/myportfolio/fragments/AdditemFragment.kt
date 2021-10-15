package myapps.myportfolio.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import myapps.myportfolio.R
import myapps.myportfolio.data.Share
import java.lang.RuntimeException

class AdditemFragment : DialogFragment() {
    private lateinit var assetHandler: AssetHandler
    private lateinit var etName: EditText
    private lateinit var etNumber: EditText
    private lateinit var etValue: EditText

    interface AssetHandler{
        fun shareCreated(share: Share)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AssetHandler)
            assetHandler = context
        else
            throw RuntimeException("Wrong call")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("New Asset")

        initDialogContent(builder)

        builder.setPositiveButton("Add Asset") { _, _ ->
            // keep it empty
        }
        return builder.create()
    }

    private fun initDialogContent(builder: AlertDialog.Builder) {
        val rootView = requireActivity().layoutInflater.inflate(R.layout.fragment_additem, null)
        etName = rootView.findViewById(R.id.etAssetName) as EditText
        etNumber = rootView.findViewById(R.id.etAssetNumber) as EditText
        etValue = rootView.findViewById(R.id.etAssetValue) as EditText
        builder.setView(rootView)
    }

    override fun onResume() {
        super.onResume()

        val dialog = dialog as AlertDialog
        val positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE)

        positiveButton.setOnClickListener {
            if (etName.text.isNotEmpty() &&
                    etNumber.text.isNotEmpty() &&
                    etValue.text.isNotEmpty()){
                assetHandler.shareCreated(
                    Share(
                        etName.text.toString(),
                        etNumber.text.toString().toDouble(),
                        etValue.text.toString().toDouble()
                    )
                )
                dialog.dismiss()
            }
            else {
                if (etName.text.isEmpty())
                    etName.error = "This field cannot be empty"
                if (etNumber.text.isEmpty())
                    etNumber.error = "This field cannot be empty"
                if (etValue.text.isEmpty())
                    etValue.error = "This field cannot be empty"
            }
        }
    }
}
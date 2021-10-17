package myapps.myportfolio.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.*
import androidx.fragment.app.DialogFragment
import myapps.myportfolio.R
import myapps.myportfolio.data.Share
import java.lang.RuntimeException

class AdditemFragment(val assets: MutableList<String>) : DialogFragment() {
    private lateinit var assetHandler: AssetHandler
    private lateinit var etName: AutoCompleteTextView
    private lateinit var etNumber: EditText
    private lateinit var etPrice: EditText
    private lateinit var tgbtnBuySell: ToggleButton

    interface AssetHandler{
        fun shareBought(share: Share)
        fun shareSold(share: Share)
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
        builder.setTitle("New Stock")

        initDialogContent(builder)

        builder.setPositiveButton("Add Stock") { _, _ -> }
        return builder.create()
    }

    private fun initDialogContent(builder: AlertDialog.Builder) {
        val rootView = requireActivity().layoutInflater.inflate(R.layout.fragment_additem, null)
        etName = rootView.findViewById(R.id.etAssetName) as AutoCompleteTextView
        etNumber = rootView.findViewById(R.id.etAssetNumber) as EditText
        etPrice = rootView.findViewById(R.id.etAssetPrice) as EditText
        tgbtnBuySell = rootView.findViewById(R.id.tgbtnBuySell) as ToggleButton

        etName.threshold = 1
        etName.setAdapter(ArrayAdapter(requireContext(), android.R.layout.select_dialog_item, assets))

        builder.setView(rootView)
    }

    override fun onResume() {
        super.onResume()

        val dialog = dialog as AlertDialog
        val positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE)

        positiveButton.setOnClickListener {
            if (etName.text.isNotEmpty() && etNumber.text.isNotEmpty() && etPrice.text.isNotEmpty()){
                if (etName.text.toString() in assets) {
                    val share = Share(
                        null,
                        etName.text.toString(),
                        etNumber.text.toString().toDouble(),
                        etPrice.text.toString().toDouble(),
                        etPrice.text.toString().toDouble() * etNumber.text.toString()
                            .toDouble()
                    )
                    if (!tgbtnBuySell.isChecked)
                        assetHandler.shareBought(share)
                    else
                        assetHandler.shareSold(share)
                    dialog.dismiss()
                }
                else
                    etName.error = "Value must be from list below"
            }
            else {
                if (etName.text.isEmpty())
                    etName.error = "This field cannot be empty"
                if (etNumber.text.isEmpty())
                    etNumber.error = "This field cannot be empty"
                if (etPrice.text.isEmpty())
                    etPrice.error = "This field cannot be empty"
            }
        }
    }
}
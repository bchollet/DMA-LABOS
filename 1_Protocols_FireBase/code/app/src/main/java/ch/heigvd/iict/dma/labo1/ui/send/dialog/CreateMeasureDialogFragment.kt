package ch.heigvd.iict.dma.labo1.ui.send.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import ch.heigvd.iict.dma.labo1.R
import ch.heigvd.iict.dma.labo1.models.Measure
import ch.heigvd.iict.dma.labo1.ui.send.SendViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputLayout

class CreateMeasureDialogFragment : DialogFragment() {

    private val sendViewModel : SendViewModel by activityViewModels()
    private lateinit var dialogView : View

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        // inflate dialog view from XML
        dialogView = layoutInflater.inflate(R.layout.dialog_create_measure, null)

        // create and display dialog
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(R.string.dialog_create_measure_title)
            .setView(dialogView)
            .setPositiveButton(R.string.dialog_create_measure_btn_create) { _, _ ->

                val value = getValue()
                val type = getCheckedType()

                val parsedValue = try { value.toDouble() }
                                  catch(_ : java.lang.Exception) { null }

                if(type != null && parsedValue != null) {
                    sendViewModel.addMeasure(Measure(type = type, value = parsedValue))
                }
            }
            .setNegativeButton(R.string.dialog_create_measure_btn_cancel) { _, _ ->
                // nothing to do, the dialog close itself
            }
            .create()

        // manage state restoration
        savedInstanceState?.let {state ->
            state.getString(valueKey)?.let {value ->
                dialogView.findViewById<TextInputLayout>(R.id.dialog_create_measure_value).editText?.setText(value)
            }
            state.getString(typeKey)?.let { typeString ->
                Measure.Type.valueOf(typeString).let { type ->
                    when(type) {
                        Measure.Type.TEMPERATURE -> dialogView.findViewById<Chip>(R.id.dialog_create_measure_type_temperature).isChecked = true
                        Measure.Type.HUMIDITY -> dialogView.findViewById<Chip>(R.id.dialog_create_measure_type_humidity).isChecked = true
                        Measure.Type.PRECIPITATION -> dialogView.findViewById<Chip>(R.id.dialog_create_measure_type_precipitation).isChecked = true
                        Measure.Type.PRESSURE -> dialogView.findViewById<Chip>(R.id.dialog_create_measure_type_pressure).isChecked = true
                    }
                }
            }
        }

        return dialog
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(valueKey, getValue())
        outState.putString(typeKey, getCheckedType()?.name)
    }

    private fun getValue() : String {
        return dialogView.findViewById<TextInputLayout>(R.id.dialog_create_measure_value).editText?.text.toString()
    }
    private fun getCheckedType() : Measure.Type? {
        val typeChecked = dialogView.findViewById<ChipGroup>(R.id.dialog_create_measure_type).checkedChipId
        return when(typeChecked) {
            R.id.dialog_create_measure_type_temperature -> Measure.Type.TEMPERATURE
            R.id.dialog_create_measure_type_humidity -> Measure.Type.HUMIDITY
            R.id.dialog_create_measure_type_precipitation -> Measure.Type.PRECIPITATION
            R.id.dialog_create_measure_type_pressure -> Measure.Type.PRESSURE
            else -> null
        }
    }

    companion object {
        fun getInstance() : CreateMeasureDialogFragment = CreateMeasureDialogFragment()

        private const val typeKey = "TYPE"
        private const val valueKey = "VALUE"
    }

}
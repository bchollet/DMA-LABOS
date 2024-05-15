package ch.heigvd.iict.dma.labo4.ui

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import ch.heigvd.iict.dma.labo4.R
import ch.heigvd.iict.dma.labo4.databinding.FragmentConnectedBinding
import ch.heigvd.iict.dma.labo4.viewmodels.BleViewModel
import java.text.SimpleDateFormat

class BleConnectedFragment : Fragment(), MenuProvider {

    private val bleViewModel : BleViewModel by activityViewModels()

    private var _binding : FragmentConnectedBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentConnectedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Buttons click counts
        bleViewModel.buttonClick.observe(viewLifecycleOwner) {
            binding.clicksValue.text = "$it"
        }

        // reading the temperature
        bleViewModel.temperature.observe(viewLifecycleOwner) {
            binding.tempValue.text = "$it"
        }

        // asking for temperature
        binding.readTempBtn.setOnClickListener {
            bleViewModel.readTemperature()
        }

        // time writing
        binding.updateTimeBtn.setOnClickListener {
            // send the current time
            bleViewModel.setTime()
        }

        // time reading
        bleViewModel.currentTime.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.currentTimeValue.text = SimpleDateFormat.getTimeInstance().format(it.time)
            }
        }

        // sending a number
        binding.sendButtonClick.setOnClickListener {
            if (binding.numberToSend.text.isEmpty()) {
                return@setOnClickListener
            }

            if (bleViewModel.sendValue(Integer.valueOf(binding.numberToSend.text.toString()))) {
                Toast.makeText(context, "Nombre correctement envoyé !", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Erreur lors de l'envoi :(", Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as AppCompatActivity).addMenuProvider(this)
    }

    override fun onPause() {
        super.onPause()
        (requireActivity() as AppCompatActivity).removeMenuProvider(this)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.connected_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when(menuItem.itemId) {
            R.id.menu_ble_connected_disconnect -> {
                bleViewModel.disconnect()
                true
            }
            else -> false
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = BleConnectedFragment()
    }

}
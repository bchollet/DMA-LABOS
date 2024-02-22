package ch.heigvd.iict.dma.labo1.ui.send

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ch.heigvd.iict.dma.labo1.R
import ch.heigvd.iict.dma.labo1.databinding.FragmentSendBinding
import ch.heigvd.iict.dma.labo1.models.*
import ch.heigvd.iict.dma.labo1.ui.send.dialog.CreateMeasureDialogFragment
import com.google.android.material.chip.ChipGroup

class SendFragment : Fragment(), MenuProvider {

    private val sendViewModel : SendViewModel by activityViewModels()

    private var _binding: FragmentSendBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private val measuresAdapter = MeasuresAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSendBinding.inflate(inflater, container, false).apply {
            fragmentSendList.adapter = measuresAdapter
            fragmentSendList.layoutManager = LinearLayoutManager(requireContext())
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sendViewModel.measures.observe(viewLifecycleOwner) { newMeasures ->
            measuresAdapter.measures = newMeasures
        }
        binding.fragmentSendBtnClear.setOnClickListener {
            sendViewModel.clearAllMeasures()
        }
        binding.fragmentSendBtnCreate.setOnClickListener {
            CreateMeasureDialogFragment.getInstance().show(parentFragmentManager, "create_dialog")
        }
        binding.fragmentSendBtnCreate.setOnLongClickListener {
            val measuresToAdd = Measure.getRandomMeasures(10)
            sendViewModel.addMeasures(measuresToAdd)
            true
        }
        binding.fragmentSendBtnSend.setOnClickListener {
            sendViewModel.sendMeasureToServer()
        }
        binding.fragmentSendSerialisation.setOnCheckedStateChangeListener(object : ChipGroup.OnCheckedStateChangeListener {
            override fun onCheckedChanged(group: ChipGroup, checkedIds: MutableList<Int>) {
                if(checkedIds.size != 1) {
                    Log.e("SendFragment", "Not exactly one serialisation method selected")
                }
                else {
                    when(checkedIds[0]) {
                        R.id.fragment_send_serialisation_json -> sendViewModel.changeSerialisation(Serialisation.JSON)
                        R.id.fragment_send_serialisation_xml -> sendViewModel.changeSerialisation(Serialisation.XML)
                        R.id.fragment_send_serialisation_protobuf -> sendViewModel.changeSerialisation(Serialisation.PROTOBUF)
                    }
                }
            }

        })
        sendViewModel.serialisation.observe(viewLifecycleOwner) { serialisation ->
            when(serialisation) {
                Serialisation.JSON -> binding.fragmentSendSerialisationJson.isChecked = true
                Serialisation.XML -> binding.fragmentSendSerialisationXml.isChecked = true
                Serialisation.PROTOBUF -> binding.fragmentSendSerialisationProtobuf.isChecked = true
            }
        }
        sendViewModel.requestDuration.observe(viewLifecycleOwner) { elapsedTimeMs ->
            if(elapsedTimeMs > 0) {
                Toast.makeText(requireContext(), "Duration : $elapsedTimeMs", Toast.LENGTH_SHORT).show()
                sendViewModel.resetRequestDuration()
            }
        }
    }

    private var menu: Menu? = null

    override fun onResume() {
        super.onResume()
        (requireActivity() as AppCompatActivity).addMenuProvider(this)
    }

    override fun onPause() {
        super.onPause()
        (requireActivity() as AppCompatActivity).removeMenuProvider(this)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.fragment_send_menu, menu)
        this.menu = menu

        // manage menu changes
        sendViewModel.encryption.observe(this) { encryption ->
            val menuEntries = menu.findItem(R.id.action_encryption)
            val menuEntry = when(encryption) {
                Encryption.DISABLED -> menu.findItem(R.id.action_encryption_disabled)
                Encryption.SSL -> menu.findItem(R.id.action_encryption_ssl)
            }
            menuEntries.subMenu?.children?.let {
                for(choice in it.iterator()) {
                    choice.isChecked = false
                }
            }
            menuEntry.isChecked = true
        }
        sendViewModel.compression.observe(this) { compression ->
            val menuEntries = menu.findItem(R.id.action_compression)
            val menuEntry = when(compression) {
                Compression.DISABLED -> menu.findItem(R.id.action_compression_disabled)
                Compression.DEFLATE -> menu.findItem(R.id.action_compression_deflate)
            }
            menuEntries.subMenu?.children?.let {
                for(choice in it.iterator()) {
                    choice.isChecked = false
                }
            }
            menuEntry.isChecked = true
        }
        sendViewModel.networkType.observe(this) { network ->
            val menuEntries = menu.findItem(R.id.action_network)
            val menuEntry = when(network) {
                NetworkType.RANDOM -> menu.findItem(R.id.action_network_random)
                NetworkType.CSD -> menu.findItem(R.id.action_network_csd)
                NetworkType.GPRS -> menu.findItem(R.id.action_network_gprs)
                NetworkType.EDGE -> menu.findItem(R.id.action_network_edge)
                NetworkType.UMTS -> menu.findItem(R.id.action_network_umts)
                NetworkType.HSPA -> menu.findItem(R.id.action_network_hspa)
                NetworkType.LTE -> menu.findItem(R.id.action_network_lte)
                NetworkType.NR5G -> menu.findItem(R.id.action_network_nr5g)
            }
            menuEntries.subMenu?.children?.let {
                for(choice in it.iterator()) {
                    choice.isChecked = false
                }
            }
            menuEntry.isChecked = true
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when(menuItem.itemId) {
            R.id.action_encryption_disabled -> sendViewModel.changeEncryption(Encryption.DISABLED)
            R.id.action_encryption_ssl -> sendViewModel.changeEncryption(Encryption.SSL)
            R.id.action_compression_disabled -> sendViewModel.changeCompression(Compression.DISABLED)
            R.id.action_compression_deflate -> sendViewModel.changeCompression(Compression.DEFLATE)
            R.id.action_network_random -> sendViewModel.changeNetworkType(NetworkType.RANDOM)
            R.id.action_network_csd -> sendViewModel.changeNetworkType(NetworkType.CSD)
            R.id.action_network_gprs -> sendViewModel.changeNetworkType(NetworkType.GPRS)
            R.id.action_network_edge -> sendViewModel.changeNetworkType(NetworkType.EDGE)
            R.id.action_network_umts -> sendViewModel.changeNetworkType(NetworkType.UMTS)
            R.id.action_network_hspa -> sendViewModel.changeNetworkType(NetworkType.HSPA)
            R.id.action_network_lte -> sendViewModel.changeNetworkType(NetworkType.LTE)
            R.id.action_network_nr5g -> sendViewModel.changeNetworkType(NetworkType.NR5G)
            else -> return false
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
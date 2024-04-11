package ch.heigvd.iict.dma.wifirtt.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import ch.heigvd.iict.dma.wifirtt.WifiRttViewModel
import ch.heigvd.iict.dma.wifirtt.databinding.FragmentMapBinding

class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private val wifiRttViewModel : WifiRttViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // init map
        wifiRttViewModel.mapConfig.observe(viewLifecycleOwner) {map ->
            binding.map.setImageResource(map.imageRes)
            binding.mapOverlay.setMapDimension(map.mapHeightMm, map.mapWidthMm)
            binding.mapOverlay.setAccessPoints(map.accessPointKnownLocations)
        }

        // we observe position
        wifiRttViewModel.estimatedPosition.observe(viewLifecycleOwner) { position ->
            if(position == null) return@observe
            binding.mapOverlay.estimatedPosition = position
        }

        wifiRttViewModel.estimatedDistances.observe(viewLifecycleOwner) { distances ->
            binding.mapOverlay.estimatedDistances = distances
        }

        // debug mode
        wifiRttViewModel.debug.observe(viewLifecycleOwner) { debug ->
            binding.mapDebug.isChecked = debug
            binding.mapOverlay.debug = debug
        }

        binding.mapDebug.setOnCheckedChangeListener{ _: CompoundButton, b: Boolean ->
            wifiRttViewModel.debugMode(b)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
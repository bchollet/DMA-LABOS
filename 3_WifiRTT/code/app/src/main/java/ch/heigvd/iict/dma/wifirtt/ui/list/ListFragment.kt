package ch.heigvd.iict.dma.wifirtt.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ch.heigvd.iict.dma.wifirtt.WifiRttViewModel
import ch.heigvd.iict.dma.wifirtt.databinding.FragmentListBinding

class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private val wifiRttViewModel : WifiRttViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = RangedAccessPointsAdapter()
        binding.list.adapter = adapter
        binding.list.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        wifiRttViewModel.rangedAccessPoints.observe(viewLifecycleOwner) {
            adapter.rangedAccessPoints = it
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
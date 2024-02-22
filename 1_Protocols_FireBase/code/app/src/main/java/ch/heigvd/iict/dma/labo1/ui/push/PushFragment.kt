package ch.heigvd.iict.dma.labo1.ui.push

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import ch.heigvd.iict.dma.labo1.Labo1Application
import ch.heigvd.iict.dma.labo1.R
import ch.heigvd.iict.dma.labo1.databinding.FragmentPushBinding

class PushFragment : Fragment() {

    private var _binding: FragmentPushBinding? = null

    private val pushViewModel : PushViewModel by activityViewModels {
        PushViewModelFactory((requireActivity().application as Labo1Application).messagesDao)
    }

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentPushBinding.inflate(inflater, container, false)
        val root: View = binding.root

        pushViewModel.lastMessage.observe(viewLifecycleOwner) { message ->
            if(message == null)
                binding.textNotifications.text = getString(R.string.fragment_push_no_message)
            else {
                Log.d("PushFragment", "$message")
                binding.textNotifications.text = message.message
            }
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
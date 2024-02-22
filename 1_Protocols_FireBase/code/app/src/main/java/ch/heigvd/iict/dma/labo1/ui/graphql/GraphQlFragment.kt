package ch.heigvd.iict.dma.labo1.ui.graphql

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ch.heigvd.iict.dma.labo1.databinding.FragmentGraphqlBinding
import ch.heigvd.iict.dma.labo1.ui.graphql.adapters.AuthorAdapter
import ch.heigvd.iict.dma.labo1.ui.graphql.adapters.BookAdapter

class GraphQlFragment : Fragment() {

    private val graphQlViewModel : GraphQlViewModel by activityViewModels()

    private var _binding: FragmentGraphqlBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentGraphqlBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //set up
        val authorAdapter = AuthorAdapter(requireContext())
        binding.fragmentGraphqlSelectauthor.adapter = authorAdapter
        binding.fragmentGraphqlSelectauthor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(position >= 0)
                    graphQlViewModel.loadBooksFromAuthor(authorAdapter.getItem(position))
            }
        }

        val bookAdapter = BookAdapter()
        binding.fragmentGraphqlListbooks.adapter = bookAdapter
        binding.fragmentGraphqlListbooks.layoutManager = LinearLayoutManager(requireContext())

        graphQlViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            if(isLoading) {
                binding.fragmentGraphqlProgress.visibility = View.VISIBLE
                binding.fragmentGraphqlListbooks.visibility = View.GONE
            } else {
                binding.fragmentGraphqlProgress.visibility = View.GONE
                binding.fragmentGraphqlListbooks.visibility = View.VISIBLE
            }
        }

        graphQlViewModel.authors.observe(viewLifecycleOwner) { authors ->
            authorAdapter.authors = authors
        }

        graphQlViewModel.books.observe(viewLifecycleOwner) { books ->
            bookAdapter.books = books
        }

        graphQlViewModel.requestDuration.observe(viewLifecycleOwner) { elapsedTimeMs ->
            if(elapsedTimeMs > 0) {
                Toast.makeText(requireContext(), "Duration : $elapsedTimeMs", Toast.LENGTH_SHORT).show()
                graphQlViewModel.resetRequestDuration()
            }
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
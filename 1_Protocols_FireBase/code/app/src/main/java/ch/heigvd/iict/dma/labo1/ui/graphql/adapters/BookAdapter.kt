package ch.heigvd.iict.dma.labo1.ui.graphql.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ch.heigvd.iict.dma.labo1.models.Book

class BookAdapter : RecyclerView.Adapter<BookAdapter.ViewHolder>() {

    var books : List<Book> = emptyList()
        @SuppressLint("NotifyDataSetChanged") //we change all books at once, no need to optimize
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_2, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = books.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(books[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val text1 = view.findViewById<TextView>(android.R.id.text1)
        private val text2 = view.findViewById<TextView>(android.R.id.text2)

        fun bind(book : Book) {
            text1.text = book.title
            val authors = book.authors.map { it.name }.joinToString()
            text2.text = "${book.publicationDate} - $authors"
        }
    }

}
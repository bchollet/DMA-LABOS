package ch.heigvd.iict.dma.labo1.ui.graphql.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import ch.heigvd.iict.dma.labo1.models.Author

class AuthorAdapter(private val context : Context) : BaseAdapter() {
    var authors : List<Author> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getCount() = authors.size

    override fun getItem(position: Int) = authors[position]

    override fun getItemId(position: Int) = authors[position].id.toLong()

    override fun hasStableIds() = true

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        if(view == null) {
            view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false)
        }

        val item = getItem(position)
        view!!.findViewById<TextView>(android.R.id.text1).text = item.name

        return view
    }
}

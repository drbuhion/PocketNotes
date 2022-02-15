package ph.edu.dlsu.mobdeve.s12.buhion.minor.pocketnotes

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ph.edu.dlsu.mobdeve.s12.buhion.minor.pocketnotes.databinding.ItemNoteBinding


class NotesAdapter(private var notesList : ArrayList<Note>, val listener: MyOnClickListener): RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val itemBinding = ItemNoteBinding
            .inflate(LayoutInflater.from(parent.context),parent,false)
        return NoteViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {

        val current_item = notesList[position]

        holder.title.text = current_item.title
        holder.text.text = current_item.text

    }

    override fun getItemCount(): Int {
        return notesList.size
    }

    inner class NoteViewHolder(private val itemBinding: ItemNoteBinding)
        :RecyclerView.ViewHolder(itemBinding.root) {

        val title: TextView = itemBinding.rvTvTitle
        val text: TextView = itemBinding.rvTvText

        init {

            itemBinding.root.setOnClickListener{
                val position = bindingAdapterPosition
                listener.onClick(position)
            }
        }
    }

    interface MyOnClickListener{
        fun onClick(position: Int)
    }
}
package id.ac.unhas.todoapp.Adapter

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.work.WorkManager
import id.ac.unhas.todoapp.Data.Word
import id.ac.unhas.todoapp.Data.WordViewModel
import id.ac.unhas.todoapp.R
import com.github.abdularis.civ.AvatarImageView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.random.Random

class WordListAdapter internal constructor(
    context: Context?, v: FloatingActionButton
) : RecyclerView.Adapter<WordListAdapter.WordViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var words = ArrayList<Word>() // Cached copy of words
    private lateinit var ctx: Context
    private lateinit var view: View
    private val parentView: FloatingActionButton = v
    private lateinit var wordViewModel: WordViewModel

    inner class WordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val wordItemView: TextView = itemView.findViewById(R.id.task)
        val timeItemView: TextView = itemView.findViewById(R.id.time)
        val avImageView: AvatarImageView = itemView.findViewById(R.id.TxtImg)
        val relcard: RelativeLayout = itemView.findViewById(R.id.relcard)
        val completionToggle: CheckBox = itemView.findViewById(R.id.completionToggle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val itemView = inflater.inflate(R.layout.row_layout, parent, false)
        return WordViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val current = words[position]
        holder.wordItemView.text = current.word
        if(current.isComplete){
            holder.wordItemView.setTextColor(ctx.resources.getColor(R.color.colorAccent))
            holder.wordItemView.paintFlags =
                holder.wordItemView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
        else{
            holder.wordItemView.setTextColor(ctx.resources.getColor(R.color.textColor))
            holder.wordItemView.paintFlags = holder.wordItemView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
        holder.timeItemView.text = current.time
        holder.avImageView.setText(current.word.toCharArray()[0] + "")
        holder.avImageView.avatarBackgroundColor = Color.parseColor(current.color)
        holder.completionToggle.isChecked = current.isComplete
        holder.completionToggle.setOnCheckedChangeListener { _, isChecked ->
            toggleCompletion(current.id, isChecked)
        }
        holder.relcard.setOnClickListener {
            val mDialogView =
                LayoutInflater.from(ctx).inflate(R.layout.task_description_dialog, null)
            mDialogView.findViewById<TextView>(R.id.name).text = current.word
            if (current.description.isNotEmpty()) {
                mDialogView.findViewById<TextView>(R.id.description).text = current.description
            } else {
                mDialogView.findViewById<TextView>(R.id.description).visibility = View.GONE
            }
            mDialogView.findViewById<TextView>(R.id.time).text = current.time
            mDialogView.findViewById<TextView>(R.id.status).text =
                if (current.isComplete) ctx.getString(R.string.completed) else ctx.getString(R.string.not_completed)
            mDialogView.findViewById<TextView>(R.id.status).setTextColor(
                ContextCompat.getColor(
                    ctx,
                    if (current.isComplete) R.color.colorAccent else R.color.textColor
                )
            )
            val mBuilder = AlertDialog.Builder(ctx).setView(mDialogView)
            val mAlertDialog = mBuilder.show()
            mDialogView.findViewById<Button>(R.id.close).setOnClickListener {
                mAlertDialog.dismiss()
            }
        }
    }

    internal fun setWords(
        words: List<Word>,
        ctx: Context?,
        wordViewModel: WordViewModel,
        view: View
    ) {
        this.words = words as ArrayList<Word>
        this.ctx = ctx!!
        this.wordViewModel = wordViewModel
        this.view = view
        notifyDataSetChanged()
    }

    override fun getItemCount() = words.size

    fun getList() = words
    private fun toggleCompletion(id: Int, mark: Boolean) {
        wordViewModel.markAsComplete(id, mark)
    }

    fun removeitem(position: Int) {
        wordViewModel.delete(words[position])
        WorkManager.getInstance().cancelAllWorkByTag(words[position].tag)
        notifyItemRemoved(position)
    }

    fun restoreItem(word: Word, position: Int) {
        words.add(position, word)
        notifyItemChanged(position)
        wordViewModel.insert(word)
    }
}
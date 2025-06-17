import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tp3_0377892.SujetResultat.SujetResultat
import com.example.tp3_0377892.SujetVote.SujetVote
import com.example.tp3_0377892.databinding.MonItemBinding
import org.depinfo.sujetbd.Sujet

class MonAdapter : ListAdapter<Sujet, MonAdapter.MonItemViewHolder>(MonItemDiffCallback) {

    inner class MonItemViewHolder(private val binding: MonItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Sujet) {
            binding.tvElement.text = item.contenu

            binding.tvElement.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, SujetVote::class.java).apply {
                    putExtra("EXTRA_TEXT", item.contenu)
                    putExtra("EXTRA_SUJET_ID", item.id)
                }
                context.startActivity(intent)
            }

            binding.iconButton.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, SujetResultat::class.java).apply {
                    putExtra("EXTRA_TEXT", item.contenu)
                    putExtra("EXTRA_SUJET_ID", item.id)
                }
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonItemViewHolder {
        val binding: MonItemBinding = MonItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MonItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MonItemViewHolder, position: Int) {
        val item: Sujet = getItem(position)
        holder.bind(item)
    }
}

object MonItemDiffCallback : DiffUtil.ItemCallback<Sujet>() {
    override fun areItemsTheSame(oldItem: Sujet, newItem: Sujet): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Sujet, newItem: Sujet): Boolean {
        return oldItem == newItem
    }
}

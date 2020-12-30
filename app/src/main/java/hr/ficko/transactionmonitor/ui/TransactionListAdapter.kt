package hr.ficko.transactionmonitor.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hr.ficko.transactionmonitor.databinding.ListItemBinding
import hr.ficko.transactionmonitor.models.Transaction

class TransactionListAdapter(var dataset: List<Transaction> = listOf()) :
    RecyclerView.Adapter<TransactionListAdapter.ViewHolder>() {

    class ViewHolder(itemBinding: ListItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        var description: TextView = itemBinding.tvDescriptionValue
        var date: TextView = itemBinding.tvTransactionDate
        var amount: TextView = itemBinding.tvTransactionAmount
        var type: TextView = itemBinding.tvTransactionType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding =
            ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = dataset[position]

        holder.apply {
            description.text = transaction.description
            date.text = transaction.date
            amount.text = transaction.amount
            type.text = transaction.type
        }
    }

    override fun getItemCount(): Int = dataset.size
}
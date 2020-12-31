package hr.ficko.transactionmonitor.ui

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class SpinnerAdapter(
    context: Context,
    id: Int,
    list: List<String>
) : ArrayAdapter<String>(context, id, list) {

    // Set the color of the first item (hint) to gray
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent) as TextView
        if (position == 0) {
            view.setTextColor(Color.GRAY)
        } else {
            view.setTextColor(Color.BLACK)
        }
        return view
    }

    // Disable the first item (hint)
    override fun isEnabled(position: Int): Boolean {
        return position != 0
    }
}
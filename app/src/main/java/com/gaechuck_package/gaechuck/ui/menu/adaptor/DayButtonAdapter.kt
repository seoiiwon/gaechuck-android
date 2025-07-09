import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.gaechuck_package.gaechuck.R

class DayButtonAdapter(
    private val context: Context,
    private val days: List<String>,
    private var selectedDay: String,
    private val onDaySelected: (String) -> Unit
) : RecyclerView.Adapter<DayButtonAdapter.DayViewHolder>() {

    inner class DayViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dayButton: Button = view.findViewById(R.id.dayButton)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_day_button, parent, false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val day = days[position]
        holder.dayButton.text = day

        holder.dayButton.isSelected = (day == selectedDay)

        holder.dayButton.setOnClickListener {
            val previousSelected = selectedDay
            selectedDay = day
            notifyDataSetChanged()  // 상태 변경을 위해 갱신
            onDaySelected(day)
        }
    }

    override fun getItemCount(): Int = days.size

}
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.gaechuck.R

class DayButtonAdapter(
    private val context: Context,
    private val days: List<String>,
    private var selectedDay: String,
    private val onDaySelected: (String) -> Unit
) : RecyclerView.Adapter<DayButtonAdapter.DayViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val button = LayoutInflater.from(context).inflate(R.layout.item_day_button, parent, false) as Button
        return DayViewHolder(button)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val day = days[position]
        holder.button.text = day

        // 선택된 날짜 버튼 스타일 변경
        holder.button.setBackgroundColor(
            if (day == selectedDay) ContextCompat.getColor(context, R.color.gnu_blue)
            else ContextCompat.getColor(context, R.color.default_tab_color)
        )

        holder.button.setOnClickListener {
            selectedDay = day
            onDaySelected(day)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int = days.size

    class DayViewHolder(val button: Button) : RecyclerView.ViewHolder(button)
}
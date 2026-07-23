package com.gaechuck_package.gaechuck.ui.menu.adaptor

import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.gaechuck_package.gaechuck.R

data class MenuSection(
    val title: String,
    val time: String,
    val menuText: String,
    var isExpanded: Boolean = true
)

class CafeteriaSectionAdapter : RecyclerView.Adapter<CafeteriaSectionAdapter.SectionViewHolder>() {

    private val sections = mutableListOf<MenuSection>()

    inner class SectionViewHolder(val root: LinearLayout, view: View) : RecyclerView.ViewHolder(view) {
        val header: LinearLayout = view.findViewById(R.id.sectionHeader)
        val title: TextView = view.findViewById(R.id.sectionTitle)
        val chevron: ImageView = view.findViewById(R.id.chevronIcon)
        val content: LinearLayout = view.findViewById(R.id.sectionContent)
        val time: TextView = view.findViewById(R.id.sectionTime)
        val menu: TextView = view.findViewById(R.id.sectionMenu)

        fun bind(section: MenuSection) {
            title.text = section.title

            if (section.time.isNotBlank()) {
                time.text = section.time
                time.visibility = View.VISIBLE
            } else {
                time.visibility = View.GONE
            }

            menu.text = section.menuText

            // 빈 섹션(정보 없음)은 헤더만 표시
            if (section.menuText.isBlank()) {
                header.isClickable = false
                chevron.visibility = View.GONE
                content.visibility = View.GONE
                return
            }

            header.isClickable = true
            chevron.visibility = View.VISIBLE
            applyExpandedState(section.isExpanded, animate = false)

            header.setOnClickListener {
                section.isExpanded = !section.isExpanded
                applyExpandedState(section.isExpanded, animate = true)
            }
        }

        private fun applyExpandedState(expanded: Boolean, animate: Boolean) {
            if (animate) {
                val transition = AutoTransition().apply { duration = 200 }
                TransitionManager.beginDelayedTransition(root, transition)
            }
            content.visibility = if (expanded) View.VISIBLE else View.GONE
            chevron.setImageResource(if (expanded) R.drawable.ic_chevron_up else R.drawable.ic_chevron_down)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cafeteria_section, parent, false)
        return SectionViewHolder(view as LinearLayout, view)
    }

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) =
        holder.bind(sections[position])

    override fun getItemCount(): Int = sections.size

    fun submitSections(newSections: List<MenuSection>) {
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize() = sections.size
            override fun getNewListSize() = newSections.size
            override fun areItemsTheSame(old: Int, new: Int) =
                sections[old].title == newSections[new].title
            override fun areContentsTheSame(old: Int, new: Int) =
                sections[old].menuText == newSections[new].menuText &&
                sections[old].time == newSections[new].time
        })
        sections.clear()
        sections.addAll(newSections)
        diff.dispatchUpdatesTo(this)
    }
}

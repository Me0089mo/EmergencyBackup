package com.example.emergencybackupv10.fragments

import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.emergencybackupv10.R
import kotlinx.android.synthetic.main.item_folder_viewer.view.*
import java.util.*

class BackupSettingsAdapter(
    private var values : MutableList<Pair<Int, Pair<String, String>>>
) : RecyclerView.Adapter<BackupSettingsAdapter.ViewHolder>(),
    ItemTouchHelperInterface{

    var touchHelper : ItemTouchHelper? = null

    fun setTouchHelper(touchHelper: ItemTouchHelper?) = touchHelper.also { this.touchHelper = it }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BackupSettingsAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_folder_viewer,
                parent,
                false
        ))
    }

    override fun onBindViewHolder(holder: BackupSettingsAdapter.ViewHolder, position: Int) {
        val item = values[position]
        holder.contentView?.text = item.second.first
    }

    override fun getItemCount(): Int = values.size

    fun getItems(): List<Pair<Int, Pair<String, String>>>{
        return values
    }

    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView),
        View.OnTouchListener,
        GestureDetector.OnGestureListener {

        var gestureDetector : GestureDetector? = null
        var contentView: TextView? = null

        init{
            gestureDetector = GestureDetector(itemView.context, this)
            contentView = itemView.content_items
        }

        override fun toString(): String {
            return super.toString() + " '" + contentView?.text + "'"
        }

        override fun onDown(e: MotionEvent?): Boolean {
            TODO("Not yet implemented")
        }

        override fun onShowPress(e: MotionEvent?) {
            TODO("Not yet implemented")
        }

        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            TODO("Not yet implemented")
        }

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent?,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            TODO("Not yet implemented")
        }

        override fun onLongPress(e: MotionEvent?) {
            touchHelper?.startDrag(this)
        }

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent?,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            TODO("Not yet implemented")
        }

        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            gestureDetector?.onTouchEvent(event)
            return true
        }
    }

    override fun onItemMove(from: Int, to: Int) {
        val aux = values[from]
        values[from] =  Pair(values[to].first,values[from].second)
        values[to] = Pair(aux.first, values[to].second)
        Collections.swap(values, from, to)
        notifyItemMoved(from, to)
    }

    override fun onItemSwipe(position: Int) {
        for (i in position+1..values.size-1){
            values[i] = Pair(values[i].first-1, values[i].second)
        }
        values.removeAt(position)
        notifyItemRemoved(position)
    }
}
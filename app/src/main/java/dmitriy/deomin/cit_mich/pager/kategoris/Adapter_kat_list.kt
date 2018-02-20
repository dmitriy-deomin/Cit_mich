package dmitriy.deomin.cit_mich.pager.kategoris


import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import dmitriy.deomin.cit_mich.R
import org.jetbrains.anko.sdk25.coroutines.onClick


class Adapter_kat_list(private var items: ArrayList<Map<String, String>>): RecyclerView.Adapter<Adapter_kat_list.ViewHolder>() {


    class ViewHolder(row: View) : RecyclerView.ViewHolder(row) {
        var txtName: Button = row.findViewById<Button>(R.id.but_item_kategor)
        val context: Context = row.context
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(R.layout.item_kategoriy, parent, false)
        return ViewHolder(itemView)
    }


    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        //ставим название категории
        holder.txtName.text = items[position]["title"].toString()

        holder.txtName.onClick {
            //играем анимацию
            val anim = AnimationUtils.loadAnimation(holder.context, R.anim.alfa)
            holder.txtName.startAnimation(anim)

            
        }








    }
}
package dmitriy.deomin.cit_mich.pager.tovar_nedeli

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.makeramen.roundedimageview.RoundedTransformationBuilder
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import dmitriy.deomin.cit_mich.R

class Adapter_tovar_nedeli(private var items: ArrayList<Map<String, String>>): RecyclerView.Adapter<Adapter_tovar_nedeli.ViewHolder>() {


    //для обводки картинок рамкой
    var transformation: Transformation? = null

    class ViewHolder(row: View) : RecyclerView.ViewHolder(row) {
        var ava: ImageView = row.findViewById<ImageView>(R.id.imageView_ava)
        var name:TextView = row.findViewById<TextView>(R.id.textView_name)
        var mani:TextView = row.findViewById<TextView>(R.id.textView_mani)
        var bonus:TextView = row.findViewById<TextView>(R.id.textView_bonus)
        var nalichie:TextView = row.findViewById<TextView>(R.id.textView_nalichie)
        var context:Context = row.context

    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(R.layout.item_tovar, parent, false)

        transformation = RoundedTransformationBuilder()
                .borderColor(Color.BLACK)
                .borderWidthDp(1F)
                .cornerRadiusDp(10F)
                .oval(false)
                .build()

        return ViewHolder(itemView)
    }


    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        //ставим название товара и прочие
        holder.name.text = items[position]["name"].toString()
        holder.mani.text = items[position]["cena"].toString()
        holder.bonus.text = items[position]["bonus"].toString()
        holder.nalichie.text = items[position]["nalichie"].toString()

        Picasso.with(holder.context).load(items[position]["picture"].toString()).fit().transform(transformation).into(holder.ava)

    }
}
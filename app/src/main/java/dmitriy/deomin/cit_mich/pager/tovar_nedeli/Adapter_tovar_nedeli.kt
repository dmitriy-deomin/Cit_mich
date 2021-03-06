package dmitriy.deomin.cit_mich.pager.tovar_nedeli

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import com.makeramen.roundedimageview.RoundedTransformationBuilder
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import dmitriy.deomin.cit_mich.Main
import dmitriy.deomin.cit_mich.Podrobno_o_tovare
import dmitriy.deomin.cit_mich.R
import org.jetbrains.anko.sdk25.coroutines.onClick

class Adapter_tovar_nedeli(private var items: ArrayList<Map<String, String>>): RecyclerView.Adapter<Adapter_tovar_nedeli.ViewHolder>() {


    //для обводки картинок рамкой
    lateinit var transformation: Transformation

    class ViewHolder(row: View) : RecyclerView.ViewHolder(row) {
        var ava: ImageView = row.findViewById<ImageView>(R.id.imageView_ava)
        var name:TextView = row.findViewById<TextView>(R.id.textView_name)
        var mani:TextView = row.findViewById<TextView>(R.id.textView_mani)
        var bonus:TextView = row.findViewById<TextView>(R.id.textView_bonus)
        var nalichie:TextView = row.findViewById<TextView>(R.id.textView_nalichie)
        var context:Context = row.context

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_tovar, parent, false)

        transformation = RoundedTransformationBuilder()
                .borderColor(Color.BLACK)
                .borderWidthDp(0.5F)
                .cornerRadiusDp(5F)
                .oval(false)
                .build()

        return ViewHolder(itemView)
    }


    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        //устанавливаем шрифт
        //--------------------------------------------------
        holder.name.typeface = Main.face
        holder.mani.typeface = Main.face
        holder.bonus.typeface = Main.face
        holder.nalichie.typeface = Main.face
        //--------------------------------------------------------


        //ставим название товара и прочие
        holder.name.text = items[position]["name"].toString()
        holder.mani.text = items[position]["cena"].toString()
        holder.bonus.text = items[position]["bonus"].toString()
        holder.nalichie.text = items[position]["nalichie"].toString()

        Picasso.get().load(items[position]["picture"].toString()).transform(transformation).into(holder.ava)

        holder.ava.onClick {
            //играем анимацию
            val anim = AnimationUtils.loadAnimation(holder.context, R.anim.alfa)
            holder.ava.startAnimation(anim)

            //запускаем активити которая подгрузит остальные данные
            val intent = Intent(holder.context, Podrobno_o_tovare::class.java)
            intent.putExtra("podrobno_url",items[position]["podrobno"].toString() )
            holder.context.startActivity(intent)
        }



    }
}
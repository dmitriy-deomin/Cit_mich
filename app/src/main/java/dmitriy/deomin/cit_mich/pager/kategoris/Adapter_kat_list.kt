package dmitriy.deomin.cit_mich.pager.kategoris


import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.LinearLayout
import dmitriy.deomin.cit_mich.List_tovarov
import dmitriy.deomin.cit_mich.Podrobno_o_tovare
import dmitriy.deomin.cit_mich.R
import org.jetbrains.anko.sdk25.coroutines.onClick


class Adapter_kat_list(private var items: ArrayList<Map<String, String>>): RecyclerView.Adapter<Adapter_kat_list.ViewHolder>() {


    class ViewHolder(row: View) : RecyclerView.ViewHolder(row) {
        val txtName: Button = row.findViewById<Button>(R.id.but_item_kategor)
        val open_kat: Button = row.findViewById<Button>(R.id.but_open_kategorii_list)
        val conteiner: LinearLayout = row.findViewById<LinearLayout>(R.id.list_kategori_open_loaut)
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

            //при клике на категорию будем открывать все товары списком
            val intent = Intent(holder.context, List_tovarov::class.java)
            intent.putExtra("url_list_polniy",items[position]["title_url"].toString())
            holder.context.startActivity(intent)

        }

        holder.open_kat.onClick {
            //играем анимацию
            val anim = AnimationUtils.loadAnimation(holder.context, R.anim.alfa)
            holder.open_kat.startAnimation(anim)
            //меняем надпись на кноке(свернуть\развернуть)
            if(holder.open_kat.text=="+")
            {
                holder.open_kat.text="-"
                holder.conteiner.visibility = View.VISIBLE
            }
            else
            {
                holder.open_kat.text="+"
                holder.conteiner.visibility = View.GONE
            }
        }


    }
    fun open_kategoriu(kat:String){

    }
}
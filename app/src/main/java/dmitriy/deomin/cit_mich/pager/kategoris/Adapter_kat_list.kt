package dmitriy.deomin.cit_mich.pager.kategoris


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import com.github.aakira.expandablelayout.ExpandableRelativeLayout
import dmitriy.deomin.cit_mich.List_tovarov
import dmitriy.deomin.cit_mich.Main
import dmitriy.deomin.cit_mich.R
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements




class Adapter_kat_list(private var items: ArrayList<Map<String, String>>): RecyclerView.Adapter<Adapter_kat_list.ViewHolder>() {

    class ViewHolder(row: View) : RecyclerView.ViewHolder(row) {
        val txtName: Button = row.findViewById<Button>(R.id.but_item_kategor)
        val open_kat: Button = row.findViewById<Button>(R.id.but_open_kategorii_list)
        val podlist_kategorii: ExpandableRelativeLayout = row.findViewById<ExpandableRelativeLayout>(R.id.expandableLayout_podlist_kategorii)
        val podlist:RecyclerView = row.findViewById<RecyclerView>(R.id.podlist_kategorii)
        val progres:ProgressBar = row.findViewById<ProgressBar>(R.id.progres_load_podkategorii)
        val context: Context = row.context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_kategoriy, parent, false)
        return ViewHolder(itemView)
    }


    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        //устанавливаем шрифт
        //----------------------------------------------
        holder.txtName.typeface = Main.face
        holder.open_kat.typeface = Main.face
        //---------------------------------------------

        //ставим название категории
        holder.txtName.text = items[position]["title"].toString()

        //убираем подлисты
        holder.podlist_kategorii.collapse()


        holder.txtName.onClick {
            //играем анимацию
            val anim = AnimationUtils.loadAnimation(holder.context, R.anim.alfa)
            holder.txtName.startAnimation(anim)

            //при клике на категорию будем открывать все товары списком
            val intent = Intent(holder.context, List_tovarov::class.java)
            intent.putExtra("url_list_polniy",items[position]["title_url"].toString())
            intent.putExtra("name_kat",items[position]["title"].toString())
            holder.context.startActivity(intent)

        }

        holder.open_kat.onClick {
            //играем анимацию
            val anim = AnimationUtils.loadAnimation(holder.context, R.anim.alfa)
            holder.open_kat.startAnimation(anim)

            //меняем надпись на кноке(свернуть\развернуть)
            if(holder.open_kat.text=="+") {
                holder.open_kat.text="-"
                holder.podlist_kategorii.toggle()

                //регистрируем слушителя сигналов и будем слушать сигналы
                //***************************************************************************
                //фильтр для нашего сигнала из сервиса
                val intentFilter = IntentFilter()
                intentFilter.addAction("signal_load_podlist")
                //приёмник  сигналов
                val broadcastReceiver:BroadcastReceiver = object : BroadcastReceiver() {
                    override fun onReceive(context: Context, intent: Intent) {
                        Log.e("TTTT","чето пришло, значение="+intent.getBooleanExtra("visible",false).toString())

                        //выключаем или запускаем анимацию и работает со списком
                        visible_progres(
                                holder.context,
                                intent.getBooleanExtra("visible",false),
                                holder.podlist,
                                holder.progres,
                                items[position]["title"].toString(),
                                holder.podlist_kategorii
                        )
                    }
                }
                //регистрируем приёмник
                holder.context.registerReceiver(broadcastReceiver, intentFilter)
                //************************************************************************************

                //будем выполнять запрос и получать подкатегории,сохраним их, и пошлём сигнал чтобы все грузилось в список
                load_podlist(items[position]["title_url"].toString(),items[position]["title"].toString(),holder.context)
            }
            else {
                holder.open_kat.text="+"
                holder.podlist_kategorii.collapse()
            }

        }



    }


    fun visible_progres(context:Context,v:Boolean=false,list:RecyclerView,progres:ProgressBar,homekat:String,loaut:ExpandableRelativeLayout){
        if(v){
            list.visibility = View.GONE
            progres.visibility = View.VISIBLE
        }else{
            progres.visibility = View.GONE
            list.visibility = View.VISIBLE
            load_listview(context,list,homekat)
            //костылёк чтобы лояут разворачивался
            loaut.layoutParams.height=(list.adapter.itemCount+1)*100
        }
    }

    fun load_listview(context:Context,list:RecyclerView,homekat: String){
         val adapter = Adapter_kat_list(generateData(homekat))
         val layoutManager = LinearLayoutManager(context)
         list.setHasFixedSize(true)
         list.layoutManager = layoutManager
         list.itemAnimator = DefaultItemAnimator()
         list.adapter = adapter
    }

    private fun generateData(homekat: String=""): ArrayList<Map<String, String>> {

        //временые переменые для хранения
        val title:ArrayList<String> = Main.read_arraylist("podlist_title" + homekat)
        val title_url:ArrayList<String> = Main.read_arraylist("podlist_url" +homekat)


        val result = ArrayList<Map<String,String>>()
        var stroka:Map<String,String>

        for (i in 0 until title.size) {
            Log.e("TTTT","цикл пашет"+title[i]+" "+title_url[i]+"\n")
            stroka = HashMap<String,String>(title.size)
            stroka.put("title", title[i])
            stroka.put("title_url", title_url[i])
            result.add(stroka)
        }

        return result
    }


    //name_home_kat - название категории с которой будем грузить список подкатегорий
    //url - url адрес этой кктегориии
    //context - эта дич нужна чтобы посылать синалы о начеле и конце загрузки и обработки данных
    fun load_podlist(url:String,name_home_kat:String,context:Context) {
        val title: ArrayList<String> = Main.read_arraylist("podlist_title" + name_home_kat)
        //если есть сохранёные данные загрузим их
        if (title.size > 1) {
            //пошлём сигнал для скрытия прогрессбара
            //********************************************************
            context.applicationContext?.sendBroadcast(Intent("signal_load_podlist").putExtra("visible", false))
            //********************************************************
        }else{
            async(UI) {
                //выполняем в новом потоке хрень
                bg {
                    //пошлём сигнал для показа прогрессбара что чтото происходит
                    //********************************************************
                    context.applicationContext.sendBroadcast(Intent("signal_load_podlist").putExtra("visible", true))
                    //********************************************************

                    Log.e("TTTT","грузим документ ="+url)
                    //грузим страницу с подменю
                    val doc: Document? = Jsoup.connect(url).get()

                    Log.e("TTTT","документ загружен="+doc.toString())

                    //блок со всем списком категорий
                    val element: Element = doc?.selectFirst(".bx_catalog_text")!!


                    //список товаров списком
                    val elements: Elements = element.select(".bx_catalog_text_title")

                    //временые переменые для хранения
                    val titles: ArrayList<String> = ArrayList()
                    val title_url: ArrayList<String> = ArrayList()


                    //заполняем в цикле их
                    for (i in elements.indices) {
                        titles.add(i, elements[i]?.selectFirst("a")?.text() ?: "-")
                        title_url.add(i, "http://www.cit-tmb.ru" + elements[i]?.selectFirst("a")
                                ?.attr("href"))
                    }
                    //и сохраняем в память все
                    Main.save_arraylist("podlist_title" + name_home_kat, titles)
                    Main.save_arraylist("podlist_url" + name_home_kat, title_url)

                    //пошлём сигнал для скрытия прогрессбара
                    //********************************************************
                    context.applicationContext?.sendBroadcast(Intent("signal_load_podlist").putExtra("visible", false))
                    //********************************************************
                }
            }
        }
    }




}
package dmitriy.deomin.cit_mich.pager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.app.Fragment
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.ProgressBar
import android.widget.TextView
import dmitriy.deomin.cit_mich.Main
import dmitriy.deomin.cit_mich.R
import dmitriy.deomin.cit_mich.pager.tovar_nedeli.Adapter_tovar_nedeli
import kotlinx.android.synthetic.main.tovar_nedeli.*
import kotlinx.android.synthetic.main.tovar_nedeli.view.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.support.v4.onRefresh
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.util.*

class Tovar_nedeli : Fragment() {

    lateinit var list_tovar:RecyclerView
    lateinit var progres:ProgressBar
    lateinit var broadcastReceiver:BroadcastReceiver
    lateinit var text_update_date:TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val result = inflater.inflate(R.layout.tovar_nedeli, container, false)


        list_tovar = result.list_tovar_nedeli
        progres = result.progress_load_tovar_nedeli
        text_update_date = result.data_update_data_nedeli


        //и будем слушать сигналы
        //***************************************************************************
        //фильтр для нашего сигнала из сервиса
        val intentFilter = IntentFilter()
        intentFilter.addAction("signal_dla_progressa")

        //приёмник  сигналов
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
               visible_progres(intent.getBooleanExtra("visible",false))
            }
        }
        //регистрируем приёмник
        context!!.registerReceiver(broadcastReceiver, intentFilter)
        //************************************************************************************

        result.conteiner_swipe.onRefresh {
            result.conteiner_swipe.isRefreshing = false
            //удаляем сохранялки
            Main.save_arraylist("bonus_nedeli", ArrayList())
            Main.save_arraylist("title_nedeli", ArrayList())
            Main.save_arraylist("cena_nedeli", ArrayList())
            Main.save_arraylist("nalichie_nedeli", ArrayList())
            Main.save_arraylist("podrobno_nedeli", ArrayList())
            Main.save_arraylist("picture_nedeli", ArrayList())
            start()
        }


        //загрузка, сохранение и отображение данных
        start()

        return result
    }

    fun start(){
        val title:ArrayList<String> = Main.read_arraylist("title_nedeli")
        //если есть сохранёные данные загрузим их
        if(title.size>1){
            visible_progres(false)
            //покажем дату обновления данных
            if(Main.save_read("tovar_nedeli")==Main.return_data()){
                //если свежак скроем
                text_update_date.visibility=View.GONE
            }else{
                text_update_date.visibility=View.VISIBLE
                text_update_date.text = "Данные от "+Main.save_read("tovar_nedeli")
            }

        }else {
            async(UI) {
                //выполняем в новом потоке хрень
                bg {
                    //пошлём сигнал для показа прогрессбара
                    //********************************************************
                    getContext()!!.applicationContext.sendBroadcast(Intent("signal_dla_progressa").putExtra("visible", true))
                    //********************************************************

                    val doc: Document? = Jsoup.connect("http://www.cit-tmb.ru/catalog/tovar-nedeli/").get()

                    //блок со всем списком товаровов недели
                    val element: Element = doc!!.select(".bx_item_list_slide").first()

                    //список товаров списком
                    val elements: Elements = element.select(".bx_catalog_item_container")

                    //временые переменые для хранения
                    val bonus: ArrayList<String> = ArrayList()
                    val titles: ArrayList<String> = ArrayList()
                    val cena: ArrayList<String> = ArrayList()
                    val nalichie: ArrayList<String> = ArrayList()
                    val podrobno: ArrayList<String> = ArrayList()
                    val picture: ArrayList<String> = ArrayList()

                    //заполняем в цикле их
                    for (i in elements.indices) {
                        bonus.add(i, elements[i]?.selectFirst(".bonus-section-list")?.text()?:"-")
                        titles.add(i, elements[i]?.selectFirst(".bx_catalog_item_title")?.text()?:"-")
                        cena.add(i, elements[i]?.selectFirst(".bx_catalog_item_price")?.text()?:"-")
                        nalichie.add(i, elements[i]?.selectFirst(".quantity_block")?.text()?:"-")
                        podrobno.add(i, "http://www.cit-tmb.ru" + elements[i]?.selectFirst("a")?.attr("href"))
                        picture.add(i, "http://www.cit-tmb.ru" + elements[i]?.selectFirst("a")
                                ?.attr("style")
                                ?.substring(22)
                                ?.replace(")", ""))
                    }
                    //и сохраняем в память все
                    Main.save_arraylist("bonus_nedeli", bonus)
                    Main.save_arraylist("title_nedeli", titles)
                    Main.save_arraylist("cena_nedeli", cena)
                    Main.save_arraylist("nalichie_nedeli", nalichie)
                    Main.save_arraylist("podrobno_nedeli", podrobno)
                    Main.save_arraylist("picture_nedeli", picture)

                    Main.save_value("tovar_nedeli",Main.return_data())


                    //пошлём сигнал для скрытия прогрессбара
                    //********************************************************
                    getContext()!!.applicationContext.sendBroadcast(Intent("signal_dla_progressa")
                            .putExtra("visible", false))
                    //********************************************************
                }
            }
        }
    }

    fun visible_progres(v:Boolean=false){
        if(v){
            progres.visibility = View.VISIBLE
            list_tovar.visibility = View.GONE
        }else{
            progres.visibility = View.GONE
            list_tovar.visibility = View.VISIBLE
            //если свежак скроем
            text_update_date.visibility=View.GONE
            load_listview(this.context!!)
        }
    }

    fun load_listview(context:Context){
        val adapter = Adapter_tovar_nedeli(generateData())
        val layoutManager = LinearLayoutManager(context)
        list_tovar.layoutManager = layoutManager
        list_tovar.itemAnimator = DefaultItemAnimator()
        list_tovar.adapter = adapter
    }

    private fun generateData(): ArrayList<Map<String, String>> {

        //временые переменые для хранения
        val bonus:ArrayList<String> = Main.read_arraylist("bonus_nedeli")
        val title:ArrayList<String> = Main.read_arraylist("title_nedeli")
        val cena:ArrayList<String> = Main.read_arraylist("cena_nedeli")
        val nalichie:ArrayList<String> = Main.read_arraylist("nalichie_nedeli")
        val podrobno:ArrayList<String> = Main.read_arraylist("podrobno_nedeli")
        val picture:ArrayList<String> = Main.read_arraylist("picture_nedeli")


        val result = ArrayList<Map<String,String>>()
        var stroka:Map<String,String>

        for (i in 0 until title.size) {
            stroka = HashMap<String,String>(title.size)
            stroka.put("name", title[i])
            stroka.put("bonus", bonus[i])
            stroka.put("cena", cena[i])
            stroka.put("nalichie", nalichie[i])
            stroka.put("podrobno", podrobno[i])
            stroka.put("picture", picture[i])
            result.add(stroka)
        }

        return result
    }

}
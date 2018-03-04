package dmitriy.deomin.cit_mich.pager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.app.Fragment
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.Adapter
import android.widget.ProgressBar
import dmitriy.deomin.cit_mich.MainActivity
import dmitriy.deomin.cit_mich.R
import dmitriy.deomin.cit_mich.pager.kategoris.Adapter_kat_list
import dmitriy.deomin.cit_mich.pager.tovar_nedeli.Adapter_tovar_nedeli
import kotlinx.android.synthetic.main.kategoris.view.*
import kotlinx.android.synthetic.main.tovar_nedeli.view.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.support.v4.onRefresh
import org.jetbrains.anko.support.v4.toast
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements


class Kategoris : Fragment() {

//    override fun onPause() {
//       context.unregisterReceiver(broadcastReceiver)
//        super.onPause()
//    }

    lateinit var list_tovar:RecyclerView
    lateinit var progres: ProgressBar
    lateinit var broadcastReceiver:BroadcastReceiver

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val v: View = inflater.inflate(R.layout.kategoris, null)

        list_tovar = v.recirl_list
        progres = v.progressBar

        //и будем слушать сигналы
        //***************************************************************************
        //фильтр для нашего сигнала из сервиса
        val intentFilter = IntentFilter()
        intentFilter.addAction("signal_dla_progressa_kat")

        //приёмник  сигналов
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                visible_progres(intent.getBooleanExtra("visible",false))
            }
        }
        //регистрируем приёмник
        context!!.registerReceiver(broadcastReceiver, intentFilter)
        //************************************************************************************

        v.conteiner_swipe_kategorii.onRefresh {
            v.conteiner_swipe_kategorii.isRefreshing = false
            //удаляем сохранялки
            MainActivity.save_arraylist("title_list_kateg", ArrayList())
            MainActivity.save_arraylist("title_url_list_kateg", ArrayList())
            start()
        }


        //загрузка, сохранение и отображение данных
        start()

        return v
    }



    fun start(){
        val title:ArrayList<String> = MainActivity.read_arraylist("title_list_kateg")
        //если есть сохранёные данные загрузим их
        if(title.size>1){
            visible_progres(false)
        }else {
            async(UI) {
                //выполняем в новом потоке хрень
                bg {
                    //пошлём сигнал для показа прогрессбара
                    //********************************************************
                    getContext().applicationContext.sendBroadcast(Intent("signal_dla_progressa_kat").putExtra("visible", true))
                    //********************************************************

                    val doc: Document? = Jsoup.connect("http://www.cit-tmb.ru/catalog/").get()


                    //блок со всем списком товаровов
                    val element: Element = doc?.select(".bx_catalog_text")!!.first()


                    //список товаров списком
                    val elements: Elements = element.select(".bx_catalog_text_title")

                    //временые переменые для хранения
                   val title: ArrayList<String> = ArrayList()
                   val title_url: ArrayList<String> = ArrayList()


                    //заполняем в цикле их
                    for (i in elements.indices) {
                        title.add(i,elements.get(i).select("a").first().text())
                        title_url.add(i, "http://www.cit-tmb.ru" + elements.get(i).select("a")
                                .first()
                                .attr("href"))
                    }
                    //и сохраняем в память все
                    MainActivity.save_arraylist("title_list_kateg", title)
                    MainActivity.save_arraylist("title_url_list_kateg", title_url)

                    //пошлём сигнал для скрытия прогрессбара
                    //********************************************************
                    getContext().applicationContext.sendBroadcast(Intent("signal_dla_progressa_kat")
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
            load_listview(context)
        }
    }

    fun load_listview(context:Context){
        val adapter = Adapter_kat_list(generateData())
        val layoutManager = LinearLayoutManager(context)
        list_tovar.layoutManager = layoutManager
        list_tovar.itemAnimator = DefaultItemAnimator()
        list_tovar.adapter = adapter
    }

    private fun generateData(): ArrayList<Map<String, String>> {

        //временые переменые для хранения
        val title:ArrayList<String> = MainActivity.read_arraylist("title_list_kateg")
        val title_url:ArrayList<String> = MainActivity.read_arraylist("title_url_list_kateg")


        val result = ArrayList<Map<String,String>>()
        var stroka:Map<String,String>

        for (i in 0 until title.size) {
            stroka = HashMap<String,String>(title.size)
            stroka.put("title", title[i])
            stroka.put("title_url", title_url[i])
            result.add(stroka)
        }

        return result
    }

}
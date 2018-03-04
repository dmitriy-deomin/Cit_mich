package dmitriy.deomin.cit_mich

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import kotlinx.android.synthetic.main.list_tovarov.*
import kotlinx.android.synthetic.main.podrobno_o_tovare.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements


//универсальный класс для всех типов товаров с подгрузкой страниц, и сменой вида списка
//в память не сохраняется(кроме картинок вроде)
class List_tovarov : Activity() {

    override fun onPause() {
        unregisterReceiver(broadcastReceiver)
        super.onPause()
    }

    lateinit var broadcastReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_tovarov)
        //во весь экран
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        //получаем ссылку на список
        val url_list:String = intent.extras.getString("url_list_polniy")


        //и будем слушать сигналы
        //***************************************************************************
        //фильтр для нашего сигнала из сервиса
        val intentFilter = IntentFilter()
        intentFilter.addAction("signal_dla_list_tovarov_spisok")

        //приёмник  сигналов
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                run_animacia(intent.getBooleanExtra("visible",false))
            }
        }
        //регистрируем приёмник
        this.registerReceiver(broadcastReceiver, intentFilter)
        //************************************************************************************



        async(UI) {
            //выполняем в новом потоке
            bg {
                //пошлём сигнал для показа прогрессбара
                //********************************************************
                applicationContext.sendBroadcast(Intent("signal_dla_list_tovarov_spisok").putExtra("visible", true))
                //********************************************************
                //выполняем запрос и получаем полный список товаров категории
                val doc: Document? = Jsoup.connect(url_list+"?SHOWALL_1=1").get()
                //блок со всем списком товаровов
                val element: Element? = doc?.select(".bx_catalog_list_home")?.first()!!
                //список товаров списком
                val elements: Elements = element?.select(".bx_catalog_item_container")!!
                Log.e("jnjnjn",elements.size.toString()+"        "+elements.toString())





                //пошлём сигнал для скрытия прогрессбара
                //********************************************************
                applicationContext.sendBroadcast(Intent("signal_dla_list_tovarov_spisok").putExtra("visible", false))
                //********************************************************
            }
        }




    }

    fun run_animacia(run:Boolean){
        if(run){
            list_tovary_spisok.visibility = View.GONE
            progress_load_tovari_spisok.visibility = View.VISIBLE
        }else{
            list_tovary_spisok.visibility = View.VISIBLE
            progress_load_tovari_spisok.visibility = View.GONE
        }
    }
}

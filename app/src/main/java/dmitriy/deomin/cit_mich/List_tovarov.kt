package dmitriy.deomin.cit_mich

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
import dmitriy.deomin.cit_mich.pager.tovar_nedeli.Adapter_tovar_nedeli
import kotlinx.android.synthetic.main.list_tovarov.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.support.v4.onRefresh
import org.jetbrains.anko.toast
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import android.os.AsyncTask.execute
import android.util.Log
import org.jsoup.helper.HttpConnection




//универсальный класс для всех типов товаров с подгрузкой страниц, и сменой вида списка
//в память не сохраняется(кроме картинок вроде)
class List_tovarov : Activity() {

//    override fun onPause() {
//        unregisterReceiver(broadcastReceiver)
//        super.onPause()
//    }


    lateinit var list_tovar_spisok: RecyclerView
    lateinit var broadcastReceiver: BroadcastReceiver
    lateinit var progres_list: ProgressBar

    //будет хранится переваемый в параметрах интента адрес, и также будет использоваться как ключ для сохранения данных
    lateinit var url_list: String
    //название категории
    lateinit var name_kat:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_tovarov)
        //во весь экран
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        //получаем ссылку на список
        url_list = intent.extras.getString("url_list_polniy")

        //получаем название категории
        name_kat = intent.extras.getString("name_kat")

        //получаем наш листвью
        list_tovar_spisok = list_tovary_spisok

        //прогрессбар
        progres_list = progress_load_tovari_spisok


        //и будем слушать сигналы
        //***************************************************************************
        //фильтр для нашего сигнала из сервиса
        val intentFilter = IntentFilter()
        intentFilter.addAction("signal_dla_list_tovarov_spisok")

        //приёмник  сигналов
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                run_animacia(intent.getBooleanExtra("visible", false))
            }
        }
        //регистрируем приёмник
        this.registerReceiver(broadcastReceiver, intentFilter)
        //************************************************************************************


        //обновить данные
        conteiner_swipe_list_tovarov.onRefresh {

            conteiner_swipe_list_tovarov.isRefreshing=false
            //удаляем сохранялки
            MainActivity.save_arraylist("bonus"+url_list, ArrayList())
            MainActivity.save_arraylist("title"+url_list, ArrayList())
            MainActivity.save_arraylist("cena"+url_list, ArrayList())
            MainActivity.save_arraylist("nalichie"+url_list, ArrayList())
            MainActivity.save_arraylist("podrobno"+url_list, ArrayList())
            MainActivity.save_arraylist("picture"+url_list, ArrayList())

            start()
        }

        //включаем анимацию
        run_animacia(true)

        //загрузка, сохранение и отображение данных
        start()
    }

    fun start() {
        val title: ArrayList<String> = MainActivity.read_arraylist("title"+url_list)
        //если есть сохранёные данные загрузим их(загрузка сразу после выключчения анимации)
        if (title.size > 1) {
            run_animacia(false)
            //скажем что грузим сохранялку
            toast("сохранёные данные")
        } else {
            async(UI) {
                //выполняем в новом потоке
                bg {
                    //пошлём сигнал для показа прогрессбара
                    //********************************************************
                    applicationContext.sendBroadcast(Intent("signal_dla_list_tovarov_spisok").putExtra("visible", true))
                    //********************************************************

                    //выполняем запрос и получаем полный список товаров категории
                    val doc: Document? = Jsoup.connect(url_list + "?SHOWALL_1=1").get()

                    //Напишем что идёт загрузка данных
                    button_title_list.text = "Загруженно"

                    //блок со всем списком товаровов
                    val element: Element? = doc?.select(".bx_catalog_list_home")?.first()!!

                    //Напишем что идёт загрузка данных
                    button_title_list.text = "Составленно"

                    //список товаров списком
                    val elements: Elements = element?.select(".bx_catalog_item_container")!!

                    //Напишем что идёт загрузка данных
                    button_title_list.text = "Обработка элементов 0/"+elements.size.toString()

                    //временые переменые для хранения
                    val bonus: ArrayList<String> = ArrayList()
                    val title: ArrayList<String> = ArrayList()
                    val cena: ArrayList<String> = ArrayList()
                    val nalichie: ArrayList<String> = ArrayList()
                    val podrobno: ArrayList<String> = ArrayList()
                    val picture: ArrayList<String> = ArrayList()

                    //заполняем в цикле их
                    for (i in elements.indices) {
                            bonus.add(i, elements[i]?.selectFirst(".bonus-section-list")?.text() ?: "-")
                            title.add(i, elements[i]?.selectFirst(".bx_catalog_item_title")?.text() ?: "-")
                            cena.add(i, elements[i]?.selectFirst(".bx_catalog_item_price")?.text() ?: "-")
                            nalichie.add(i, elements[i]?.selectFirst(".quantity_block")?.text() ?: "-")
                            podrobno.add(i, "http://www.cit-tmb.ru" + elements[i]?.selectFirst("a")
                                    ?.attr("href"))
                            picture.add(i, "http://www.cit-tmb.ru" + elements[i]?.selectFirst("a")
                                    ?.attr("style")
                                    ?.substring(23)
                                    ?.replace("')", ""))

                            //Напишем что идёт обработка
                            button_title_list.text = "Обработка элементов $i/" + elements.size.toString()
                    }

                    //Напишем что идёт загрузка данных
                    button_title_list.text = "Сохранение"


                    //и сохраняем в память все
                    MainActivity.save_arraylist("bonus" + url_list, bonus)
                    MainActivity.save_arraylist("title" + url_list, title)
                    MainActivity.save_arraylist("cena" + url_list, cena)
                    MainActivity.save_arraylist("nalichie" + url_list, nalichie)
                    MainActivity.save_arraylist("podrobno" + url_list, podrobno)
                    MainActivity.save_arraylist("picture" + url_list, picture)


                    //Напишем что идёт загрузка данных
                    button_title_list.text = "Готово"

                    //пошлём сигнал для скрытия прогрессбара
                    //********************************************************
                    applicationContext.sendBroadcast(Intent("signal_dla_list_tovarov_spisok").putExtra("visible", false))
                    //********************************************************
                }
            }
        }
    }

    fun run_animacia(run: Boolean) {
        if (run) {
            //Напишем что идёт загрузка данных
            button_title_list.text = "выполняется загрузка...."
            list_tovar_spisok.visibility = View.GONE
            progres_list.visibility = View.VISIBLE
        } else {
            list_tovar_spisok.visibility = View.VISIBLE
            progres_list.visibility = View.GONE
            load_listview(applicationContext)
        }
    }

    fun load_listview(context: Context) {
        val adapter = Adapter_tovar_nedeli(generateData())
        val layoutManager = LinearLayoutManager(context)
        list_tovar_spisok.layoutManager = layoutManager
        list_tovar_spisok.itemAnimator = DefaultItemAnimator()
        list_tovar_spisok.adapter = adapter
    }

    fun generateData(): ArrayList<Map<String, String>> {

        //временые переменые для хранения
        val bonus: ArrayList<String> = MainActivity.read_arraylist("bonus" + url_list)
        val title: ArrayList<String> = MainActivity.read_arraylist("title" + url_list)
        val cena: ArrayList<String> = MainActivity.read_arraylist("cena" + url_list)
        val nalichie: ArrayList<String> = MainActivity.read_arraylist("nalichie" + url_list)
        val podrobno: ArrayList<String> = MainActivity.read_arraylist("podrobno" + url_list)
        val picture: ArrayList<String> = MainActivity.read_arraylist("picture" + url_list)


        //поставим в титл название категории и количество товаров
        button_title_list.text = name_kat+", товаров:"+title.size.toString()


        val result = ArrayList<Map<String, String>>()
        var stroka: Map<String, String>

        for (i in 0 until title.size) {
            stroka = HashMap<String, String>(title.size)
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

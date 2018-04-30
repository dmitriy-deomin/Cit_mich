package dmitriy.deomin.cit_mich

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import dmitriy.deomin.cit_mich.pager.tovar_nedeli.Adapter_tovar_nedeli
import kotlinx.android.synthetic.main.list_tovarov.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.onRefresh
import org.jetbrains.anko.toast
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.util.*


//универсальный класс для всех типов товаров с подгрузкой страниц, и сменой вида списка
class List_tovarov : Activity() {

//    override fun onPause() {
//        unregisterReceiver(broadcastReceiver)
//        super.onPause()
//    }

    lateinit var list_tovar_spisok: RecyclerView
    lateinit var broadcastReceiver: BroadcastReceiver

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
        list_tovar_spisok = this.list_tovary_spisok

        //установим шрифт
        //---------------------------
        title_categoriy.typeface = Main.face
        //-------------------------------

        //установим название
        title_categoriy.text= name_kat

        //будем слушать нажатия на заголовок
        title_categoriy.onClick {
            //играем анимацию
            val anim = AnimationUtils.loadAnimation(this@List_tovarov, R.anim.alfa)
            title_categoriy.startAnimation(anim)
        }


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
            Main.save_arraylist("bonus"+url_list, ArrayList())
            Main.save_arraylist("title"+url_list, ArrayList())
            Main.save_arraylist("cena"+url_list, ArrayList())
            Main.save_arraylist("nalichie"+url_list, ArrayList())
            Main.save_arraylist("podrobno"+url_list, ArrayList())
            Main.save_arraylist("picture"+url_list, ArrayList())
            start()
        }

        //загрузка, сохранение и отображение данных
        start()
    }

    fun start() {
        //загрузим из памяти
        val title: ArrayList<String> = Main.read_arraylist("title"+url_list)
        //если есть сохранёные данные загрузим их(загрузка сразу после выключчения анимации)
        if (title.size > 1) {
            run_animacia(false)
            //покажем дату обновления данных
            if(Main.save_read(name_kat)==Main.return_data()){
                //если свежак скроем
                data_update_data.visibility=View.GONE
            }else{
                data_update_data.visibility=View.VISIBLE
                data_update_data.text = "Данные от "+Main.save_read(name_kat)
            }

        }
        else {
            //если интернет есть будем грузить
            if(isOnline()) {
                async(UI) {
                    //выполняем в новом потоке
                    bg {
                        //пошлём сигнал для показа прогрессбара
                        //********************************************************
                        applicationContext.sendBroadcast(Intent("signal_dla_list_tovarov_spisok").putExtra("visible", true))
                        //********************************************************

                        //Напишем что идёт загрузка данных
                        title_categoriy.text = "выполняется загрузка...."

                        //выполним запрос и получим
                        var doc: Document? = null
                        try {
                            //SHOWALL_1=1 -  грузит весь список,удобно но работает очень долго
                            //PAGEN_1=1  - постранично быстрей работает
                            //doc = Jsoup.connect(url_list + "?SHOWALL_1=1").userAgent("Mozilla").get()
                            doc = Jsoup.connect(url_list + "?SHOWALL_1=1").userAgent(Main.USERAGENT).get()
                        }catch (e:Exception){
                            //Напишем ошибку
                            title_categoriy.text = "Ошибочка:"+e.message.toString()+"\n попробуйте еще раз"
                            //пошлём сигнал для скрытия прогрессбара
                            //********************************************************
                            applicationContext.sendBroadcast(Intent("signal_dla_list_tovarov_spisok").putExtra("visible", false))
                            //********************************************************
                        }


                        title_categoriy.text = "Загруженно"

                        //получим блок со всем списком товаровов
                        var element: Element? = null
                        try {
                            element = doc?.selectFirst(".bx_catalog_list_home")
                        }catch (e:Exception){
                            //Напишем ошибку
                            title_categoriy.text = e.message.toString()
                        }


                        title_categoriy.text =  "Распарсенно"

                        //список товаров списком
                        val elements: Elements = element?.select(".bx_catalog_item_container")!!

                        title_categoriy.text =  "Список составлен"


                        //Напишем что идёт загрузка данных
                        title_categoriy.text =  "Обработка элементов 0/" + elements.size.toString()

                        //временые переменые для хранения
                        val bonus: ArrayList<String> = ArrayList()
                        val titles: ArrayList<String> = ArrayList()
                        val cena: ArrayList<String> = ArrayList()
                        val nalichie: ArrayList<String> = ArrayList()
                        val podrobno: ArrayList<String> = ArrayList()
                        val picture: ArrayList<String> = ArrayList()

                        //заполняем в цикле их
                        for (i in elements.indices) {
                            bonus.add(i, elements[i]?.selectFirst(".bonus-section-list")?.text()
                                    ?: "-")
                            titles.add(i, elements[i]?.selectFirst(".bx_catalog_item_title")?.text()
                                    ?: "-")
                            cena.add(i, elements[i]?.selectFirst(".bx_catalog_item_price")?.text()
                                    ?: "-")
                            nalichie.add(i, elements[i]?.selectFirst(".quantity_block")?.text()
                                    ?: "-")
                            podrobno.add(i, "http://www.cit-tmb.ru" + elements[i]?.selectFirst("a")
                                    ?.attr("href"))
                            picture.add(i, "http://www.cit-tmb.ru" + elements[i]?.selectFirst("a")
                                    ?.attr("style")
                                    ?.substring(23)
                                    ?.replace("')", ""))

                            //Напишем что идёт обработка
                            title_categoriy.text =  "Обработка элементов $i/" + elements.size.toString()
                        }

                        //Напишем что идёт загрузка данных
                        title_categoriy.text =  "Сохранение"


                        //и сохраняем в память все
                        Main.save_arraylist("bonus" + url_list, bonus)
                        Main.save_arraylist("title" + url_list, titles)
                        Main.save_arraylist("cena" + url_list, cena)
                        Main.save_arraylist("nalichie" + url_list, nalichie)
                        Main.save_arraylist("podrobno" + url_list, podrobno)
                        Main.save_arraylist("picture" + url_list, picture)

                        //и сохраним дату , чтобы знать когда обновляли
                        Main.save_value(name_kat,Main.return_data())


                        //Напишем что идёт загрузка данных
                        title_categoriy.text =  "Готово"

                        //пошлём сигнал для скрытия прогрессбара
                        //********************************************************
                        applicationContext.sendBroadcast(Intent("signal_dla_list_tovarov_spisok").putExtra("visible", false))
                        //********************************************************
                    }
                }
            }else{
                //если интернета нету
                title_categoriy.text = "Нет доступа к интернету"
                conteiner_swipe_list_tovarov.visibility = View.GONE
            }
        }
    }

    //показывает и скрывает нужные виджеты
    fun run_animacia(run: Boolean) {
        if (run) {
            //скрываем список
            conteiner_swipe_list_tovarov.visibility = View.GONE
            //показываем текстовое поле прогресса
            progress.visibility = View.VISIBLE
        } else {
            //скроем текстовый прогрес
            progress.visibility = View.GONE
            //если свежак скроем
            data_update_data.visibility=View.GONE
            //показываем список
            conteiner_swipe_list_tovarov.visibility = View.VISIBLE
            //загружаем в него данные
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
        val bonus: ArrayList<String> = Main.read_arraylist("bonus" + url_list)
        val title: ArrayList<String> = Main.read_arraylist("title" + url_list)
        val cena: ArrayList<String> = Main.read_arraylist("cena" + url_list)
        val nalichie: ArrayList<String> = Main.read_arraylist("nalichie" + url_list)
        val podrobno: ArrayList<String> = Main.read_arraylist("podrobno" + url_list)
        val picture: ArrayList<String> = Main.read_arraylist("picture" + url_list)


        //поставим в титл название категории и количество товаров
        title_categoriy.text = name_kat+", товаров:"+title.size.toString()


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
    fun isOnline(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val netInfo = cm!!.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }



}

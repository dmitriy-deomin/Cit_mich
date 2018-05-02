package dmitriy.deomin.cit_mich

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import com.makeramen.roundedimageview.RoundedTransformationBuilder
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import kotlinx.android.synthetic.main.podrobno_o_tovare.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.ctx
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

class Podrobno_o_tovare : Activity() {

    override fun onPause() {
        unregisterReceiver(broadcastReceiver)
        super.onPause()
    }

    //для обводки картинок рамкой
    lateinit var transformation: Transformation
    lateinit var broadcastReceiver:BroadcastReceiver
    var url_pik:ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.podrobno_o_tovare)
        //во весь экран
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)


        //установим шрифт
        //-----------------------------------
        name_tovara_podrobno.typeface = Main.face
        textView_artikul_tovara_podrobno.typeface = Main.face
        textView_cena_tovara_podrobno.typeface = Main.face
        textView_bonus_tovara_podrobno.typeface = Main.face
        textView_nalichie_tovara_podrobno.typeface = Main.face
        textView_opisanie_tovara_podrobno.typeface = Main.face
        //------------------------------------------------------

        //для обводки картинок рамкой с скруглением
        transformation = RoundedTransformationBuilder()
                .borderColor(Color.BLACK)
                .borderWidthDp(0.5F)
                .cornerRadiusDp(5F)
                .oval(false)
                .build()

        //получаем ссылку на товар
        val podrobno_url:String = getIntent().getExtras().getString("podrobno_url")


        //показываем анимацию и скрываем все остальное
        scroll_kontent_podrobno.visibility = View.GONE
        progressBar_podrobno_load_data.visibility = View.VISIBLE


        //и будем слушать сигналы
        //***************************************************************************
        //фильтр для нашего сигнала из сервиса
        val intentFilter = IntentFilter()
        intentFilter.addAction("signal_dla_pokaza_podrobo")

        //приёмник  сигналов
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                //скрываем анимацию и показываем загруженную инфу
                scroll_kontent_podrobno.visibility = View.VISIBLE
                progressBar_podrobno_load_data.visibility = View.GONE

                //в зависимости от количества картинок будем показывать маленькие картинки
                load_visible_imag(url_pik.size)
            }
        }
        //регистрируем приёмник
         this.registerReceiver(broadcastReceiver, intentFilter)
        //************************************************************************************



        //при клике на маленьке картинки будем грузить их картинку в большую
        imageView_ava1.onClick {
            //играем анимацию
            val anim = AnimationUtils.loadAnimation(ctx, R.anim.alfa)
            imageView_ava1.startAnimation(anim)
            //рисуем картинку
            Picasso.get().load(url_pik[0]).transform(transformation).into(imageView_ava_tovara_podrobno)
        }
        imageView_ava2.onClick {
            //играем анимацию
            val anim = AnimationUtils.loadAnimation(ctx, R.anim.alfa)
            imageView_ava2.startAnimation(anim)
            //рисуем картинку
            Picasso.get().load(url_pik[1]).transform(transformation).into(imageView_ava_tovara_podrobno)
        }
        imageView_ava3.onClick {
            //играем анимацию
            val anim = AnimationUtils.loadAnimation(ctx, R.anim.alfa)
            imageView_ava3.startAnimation(anim)
            //рисуем картинку
            Picasso.get().load(url_pik[2]).transform(transformation).into(imageView_ava_tovara_podrobno)
        }
        imageView_ava4.onClick {
            //играем анимацию
            val anim = AnimationUtils.loadAnimation(ctx, R.anim.alfa)
            imageView_ava4.startAnimation(anim)
            //рисуем картинку
            Picasso.get().load(url_pik[3]).transform(transformation).into(imageView_ava_tovara_podrobno)
        }
        imageView_ava5.onClick {
            //играем анимацию
            val anim = AnimationUtils.loadAnimation(ctx, R.anim.alfa)
            imageView_ava5.startAnimation(anim)
            //рисуем картинку
            Picasso.get().load(url_pik[4]).transform(transformation).into(imageView_ava_tovara_podrobno)
        }



        //запустим загрузку данных
                async(UI) {
                    //выполняем в новом потоке
                    bg {
                        //Напишем что идёт загрузка данных
                        name_tovara_podrobno.text = "Выполняется загрузка...."
                        var doc: Document? = null
                        try {
                            doc = Jsoup.connect(podrobno_url).userAgent(Main.USERAGENT).get()
                        }catch (e:Exception){
                            //Напишем ошибку
                            name_tovara_podrobno.text = "Ошибочка:"+e.message.toString()+"\n попробуйте еще раз"

                            //пошлём сигнал для скрытия прогрессбара
                            //********************************************************
                            applicationContext.sendBroadcast(Intent("signal_dla_pokaza_podrobo"))
                            //********************************************************
                        }

                        name_tovara_podrobno.text ="Парсим название..."
                        val title: String? = doc?.selectFirst(".bx_item_title")?.text()?:"-"

                        name_tovara_podrobno.text ="Парсим артикул..."
                        val artikul: String? = doc?.selectFirst(".articul")?.text()?:"-"

                        name_tovara_podrobno.text ="Парсим цену..."
                        val cena: String? = doc?.selectFirst(".item_current_price")?.text()?:"-"

                        name_tovara_podrobno.text ="Парсим бонусы..."
                        val bonus: String? = doc?.selectFirst(".bonus-section")?.text()?:"-"

                        name_tovara_podrobno.text ="Парсим наличие товара..."
                        var nalichie: String? = doc?.selectFirst(".item_buttons")?.text()?:"-"
                        if(nalichie?.length!! >2){
                            if(!nalichie.contains("Нет в наличии На заказ")){
                                nalichie = nalichie.replace("В корзину ","")
                                nalichie = nalichie.replace("Купить в кредит","")
                            }
                        }

                        name_tovara_podrobno.text ="Парсим полное описание..."
                        val opisanie: String? = doc?.selectFirst(".bx_item_description")?.toString()
                                ?.replace("br","\n")
                                ?.replace("&nbsp;","")
                                ?.substringBefore("</p>")
                                ?.substringAfter("hidden;\">")?:"-"


                        name_tovara_podrobno.text ="Состовляем список картинок..."
                        val pik_list:Elements = doc?.selectFirst(".bx_slide")?.select("li")!!


                        name_tovara_podrobno.text ="Парсим список картинок..."
                        var kesh:String
                        for (i in pik_list.indices) {
                            kesh = pik_list[i]?.selectFirst(".cnt_item")?.attr("style")!!
                            url_pik.add(i,"http://www.cit-tmb.ru"+kesh.substring(22,kesh.length-3))
                        }

                        //пошлём сигнал для скрытия прогрессбара и показа инфы
                        //********************************************************
                        applicationContext.sendBroadcast(Intent("signal_dla_pokaza_podrobo"))
                        //********************************************************

                        //загружаем получиную инфу в виджеты
                        name_tovara_podrobno.text = title
                        textView_artikul_tovara_podrobno.text = artikul
                        textView_cena_tovara_podrobno.text = "Цена: "+cena
                        textView_bonus_tovara_podrobno.text = bonus
                        textView_nalichie_tovara_podrobno.text = nalichie
                        textView_opisanie_tovara_podrobno.text = "Полное описание: \n"+opisanie
                    }
                }
    }

    fun load_visible_imag(img_count:Int){
        when {
            (img_count == 1) -> {
                //картинка зальётся только в большую
                imageView_ava1.visibility = View.GONE
                imageView_ava2.visibility = View.GONE
                imageView_ava3.visibility = View.GONE
                imageView_ava4.visibility = View.GONE
                imageView_ava5.visibility =View.GONE
                //рисуем картинку
                Picasso.get().load(url_pik[0]).transform(transformation).into(imageView_ava_tovara_podrobno)
            }
            (img_count == 2) -> {
                imageView_ava1.visibility =View.VISIBLE
                imageView_ava2.visibility =View.VISIBLE
                imageView_ava3.visibility =View.GONE
                imageView_ava4.visibility =View.GONE
                imageView_ava5.visibility =View.GONE
                Picasso.get().load(url_pik[0]).transform(transformation).into(imageView_ava_tovara_podrobno)
                Picasso.get().load(url_pik[0]).transform(transformation).into(imageView_ava1)
                Picasso.get().load(url_pik[1]).transform(transformation).into(imageView_ava2)
            }
            (img_count == 3) -> {
                imageView_ava1.visibility =View.VISIBLE
                imageView_ava2.visibility =View.VISIBLE
                imageView_ava3.visibility =View.VISIBLE
                imageView_ava4.visibility =View.GONE
                imageView_ava5.visibility =View.GONE
                Picasso.get().load(url_pik[0]).transform(transformation).into(imageView_ava_tovara_podrobno)
                Picasso.get().load(url_pik[0]).transform(transformation).into(imageView_ava1)
                Picasso.get().load(url_pik[1]).transform(transformation).into(imageView_ava2)
                Picasso.get().load(url_pik[2]).transform(transformation).into(imageView_ava3)
            }
            (img_count == 4)->{
                imageView_ava1.visibility =View.VISIBLE
                imageView_ava2.visibility =View.VISIBLE
                imageView_ava3.visibility =View.VISIBLE
                imageView_ava4.visibility =View.VISIBLE
                imageView_ava5.visibility =View.GONE
                Picasso.get().load(url_pik[0]).transform(transformation).into(imageView_ava_tovara_podrobno)
                Picasso.get().load(url_pik[0]).transform(transformation).into(imageView_ava1)
                Picasso.get().load(url_pik[1]).transform(transformation).into(imageView_ava2)
                Picasso.get().load(url_pik[2]).transform(transformation).into(imageView_ava3)
                Picasso.get().load(url_pik[3]).transform(transformation).into(imageView_ava4)
            }
            (img_count > 4 )->{
                imageView_ava1.visibility =View.VISIBLE
                imageView_ava2.visibility =View.VISIBLE
                imageView_ava3.visibility =View.VISIBLE
                imageView_ava4.visibility =View.VISIBLE
                imageView_ava5.visibility =View.VISIBLE
                Picasso.get().load(url_pik[0]).transform(transformation).into(imageView_ava_tovara_podrobno)
                Picasso.get().load(url_pik[0]).transform(transformation).into(imageView_ava1)
                Picasso.get().load(url_pik[1]).transform(transformation).into(imageView_ava2)
                Picasso.get().load(url_pik[2]).transform(transformation).into(imageView_ava3)
                Picasso.get().load(url_pik[3]).transform(transformation).into(imageView_ava4)
                Picasso.get().load(url_pik[4]).transform(transformation).into(imageView_ava5)
            }
        }
    }

}

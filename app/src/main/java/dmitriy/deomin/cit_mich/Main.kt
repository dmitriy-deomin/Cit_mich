package dmitriy.deomin.cit_mich

import android.app.Activity
import android.content.Context
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.*
import android.content.SharedPreferences
import android.text.Spannable
import android.graphics.Typeface
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.text.format.DateFormat
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import dmitriy.deomin.cit_mich.pager.adapter
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.onPageChangeListener
import org.json.JSONArray
import org.json.JSONException
import java.util.*


class Main : FragmentActivity() {

    //lateinit  -   это если нельзя сразу определиь

    lateinit var context: Context
    //для логотипа массив картиник
    val images = arrayOf(R.drawable.title1,R.drawable.title2,R.drawable.title3,R.drawable.title4)

    companion object {
        //шрифт
        var face: Typeface? = null
        //для текста
        var text: Spannable? = null


        val USERAGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 YaBrowser/18.2.1.174 Yowser/2.5 Safari/537.36"


        var COLOR_FON: Int = 0
        var COLOR_ITEM: Int = 0
        var COLOR_TEXT: Int = 0

        //сохранялка
        //----------------------------------------------------------------
        var settings: SharedPreferences? = null // сохранялка

        fun save_value(Key: String, Value: String) { //сохранение строки
            val editor = settings!!.edit()
            editor.putString(Key, Value)
            editor.apply()
        }

        fun save_read(key_save: String): String {  // чтение настройки
            return if (settings!!.contains(key_save)) {
                settings!!.getString(key_save, "")
            } else ""
        }

        fun save_value_int(Key: String, Value: Int) { //сохранение строки
            val editor = settings!!.edit()
            editor.putInt(Key, Value)
            editor.apply()
        }

        fun save_read_int(key_save: String): Int {  // чтение настройки
            return if (settings!!.contains(key_save)) {
                settings!!.getInt(key_save, 0)
            } else 0
        }


        //*************************************************************************************************
        //ArrayList
        fun save_arraylist(key: String, values: ArrayList<String>) {
            val editor = settings!!.edit()
            val a = JSONArray()
            for (i in 0 until values.size) {
                a.put(values[i])
            }
            if (!values.isEmpty()) {
                editor.putString(key, a.toString())
            } else {
                editor.putString(key, null)
            }
            editor.commit()
        }
        fun read_arraylist(key: String): ArrayList<String> {
            val json = settings!!.getString(key, null)
            val urls = ArrayList<String>()
            if (json != null) {
                try {
                    val a = JSONArray(json)
                    for (i in 0 until a.length()) {
                        val url = a.optString(i)
                        urls.add(url)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
            return urls
        }
        //*************************************************************************************************

        fun return_data():String{
            val d = DateFormat.format("dd", Date()) as String //yyyy-MM-dd kk:mm:ss   день
            val m = Main.return_mesac(DateFormat.format("M", Date()) as String)
            return d+ return_mesac(m)
        }


        fun return_mesac(nomer_mecaca: String): String {
            when (nomer_mecaca) {
                "1" -> return " Января"
                "2" -> return " Февраля"
                "3" -> return " Марта"
                "4" -> return " Апреля"
                "5" -> return " Мая"
                "6" -> return " Июня"
                "7" -> return " Июля"
                "8" -> return " Августа"
                "9" -> return " Сентября"
                "10" -> return " Октября"
                "11" -> return " Ноября"
                "12" -> return " Декабря"
            }
            return nomer_mecaca
        }
      }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //во весь экран
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        context = this

        settings = getSharedPreferences("mysettings", FragmentActivity.MODE_PRIVATE)
        face = Typeface.createFromAsset(assets, if (save_read("fonts") == "") "fonts/Tweed.ttf" else save_read("fonts"))


        //ставим цвет фона
        if (save_read_int("color_fon") == 0) {
            COLOR_FON = ContextCompat.getColor(this,R.color.colorFonDefoult)
        } else {
            COLOR_FON = save_read_int("color_fon")
        }
        //ставим цвет постов
        if (save_read_int("color_post1") == 0) {
            COLOR_ITEM = ContextCompat.getColor(this,R.color.colorFon_tovaraDefoult)
        } else {
            COLOR_ITEM = save_read_int("color_post1")
        }
        //ставим цвеи текста
        if (save_read_int("color_text") == 0) {
            COLOR_TEXT = ContextCompat.getColor(this,R.color.colorText_tovarDefoult)
        } else {
            COLOR_TEXT = save_read_int("color_text")
        }

        //устанавлваем шрифр
        //-------------------------------------------
        but_news.typeface= face
        but_tovar_dna.typeface= face
        but_kagoriy.typeface= face
        but_info.typeface= face
        //---------------------------------------------

        val adapter = adapter(supportFragmentManager)
        val viewpager = findViewById<View>(R.id.pager) as ViewPager

        viewpager.adapter = adapter
        viewpager.offscreenPageLimit = 4

        //для логотипа гемор чтоб рисовалась картинка , её выравнивание и положение
        imageSwitcher.setFactory {
            val myView = ImageView(applicationContext)
            myView.scaleType = ImageView.ScaleType.FIT_XY
            myView.layoutParams =FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
            myView
        }

        // так устанавливается нужное лого
        imageSwitcher.setImageResource(images[1])
        //откроевкладку товар дня
        viewpager.currentItem = 1
        //выделим жирным кноки текст
        but_tovar_dna.typeface = Typeface.DEFAULT_BOLD

        //При скроле
        viewpager.onPageChangeListener {
            onPageSelected {
                fon_butn(viewpager.currentItem)
                imageSwitcher.setImageResource(images[viewpager.currentItem])
            }
        }

        //Слушаем кнопки
        but_news.onClick {  viewpager.currentItem = 0 }
        but_tovar_dna.onClick {  viewpager.currentItem = 1 }
        but_kagoriy.onClick {  viewpager.currentItem = 2 }
        but_info.onClick {  viewpager.currentItem = 3 }

        //При клике на лого покажем меню программы
        imageSwitcher.onClick {
            val anim = AnimationUtils.loadAnimation(this@Main, R.anim.alfa)
            imageSwitcher.startAnimation(anim)
            startActivity<Menu>()
        }

    }

    private fun fon_butn(number:Int){
        when(number){
            0->{
                but_news.typeface = Typeface.DEFAULT_BOLD
                but_tovar_dna.typeface = face
                but_kagoriy.typeface = face
                but_info.typeface = face
            }
            1->{
                but_tovar_dna.typeface = Typeface.DEFAULT_BOLD
                but_news.typeface = face
                but_kagoriy.typeface = face
                but_info.typeface = face
            }
            2->{
                but_kagoriy.typeface = Typeface.DEFAULT_BOLD
                but_news.typeface = face
                but_tovar_dna.typeface = face
                but_info.typeface = face
            }
            3->{
                but_info.typeface = Typeface.DEFAULT_BOLD
                but_kagoriy.typeface = face
                but_news.typeface = face
                but_tovar_dna.typeface = face
            }
        }

    }
}

package dmitriy.deomin.cit_mich

import android.content.Context
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.*
import android.content.SharedPreferences
import android.text.Spannable
import android.graphics.Typeface
import android.support.v4.app.FragmentActivity
import android.support.v4.view.ViewPager
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import dmitriy.deomin.cit_mich.pager.adapter
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.onPageChangeListener
import org.json.JSONArray
import org.json.JSONException


class MainActivity : FragmentActivity() {

    //lateinit  -   это если нельзя сразу определиь

    lateinit var context: Context
    //для логотипа массив картиник
    val images = arrayOf(R.drawable.title1,R.drawable.title2,R.drawable.title3,R.drawable.title4)

    companion object {
        //шрифт
        var face: Typeface? = null
        //для текста
        var text: Spannable? = null

        //сохранялка
        //----------------------------------------------------------------
        var settings: SharedPreferences? = null // сохранялка
        //чтение настроек
        fun read_str(key:String):String{ if(ne_pusto(key)){return settings!!.getString(key,"")}else{return ""} }
        fun ne_pusto(key:String):Boolean{return settings!!.contains(key)}
        //запись настроек
        fun save_str(key:String,value:String){ settings!!.edit().putString(key,value).apply() }
        //----------------------------------------------------------------

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

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //во весь экран
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        context = this

        settings = getSharedPreferences("mysettings", FragmentActivity.MODE_PRIVATE)
        face = Typeface.createFromAsset(assets, if (read_str("fonts") == "") "fonts/Tweed.ttf" else read_str("fonts"))

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
            val anim = AnimationUtils.loadAnimation(this@MainActivity, R.anim.alfa)
            imageSwitcher.startAnimation(anim)
            startActivity<Menu>()
        }

    }


    private fun fon_butn(number:Int){
        when(number){
            0->{
                but_news.typeface = Typeface.DEFAULT_BOLD
                but_tovar_dna.typeface = Typeface.DEFAULT
                but_kagoriy.typeface = Typeface.DEFAULT
                but_info.typeface = Typeface.DEFAULT
            }
            1->{
                but_tovar_dna.typeface = Typeface.DEFAULT_BOLD
                but_news.typeface = Typeface.DEFAULT
                but_kagoriy.typeface = Typeface.DEFAULT
                but_info.typeface = Typeface.DEFAULT
            }
            2->{
                but_kagoriy.typeface = Typeface.DEFAULT_BOLD
                but_news.typeface = Typeface.DEFAULT
                but_tovar_dna.typeface = Typeface.DEFAULT
                but_info.typeface = Typeface.DEFAULT
            }
            3->{
                but_info.typeface = Typeface.DEFAULT_BOLD
                but_kagoriy.typeface = Typeface.DEFAULT
                but_news.typeface = Typeface.DEFAULT
                but_tovar_dna.typeface = Typeface.DEFAULT
            }
        }

    }
}

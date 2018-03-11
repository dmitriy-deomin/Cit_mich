package dmitriy.deomin.cit_mich

import android.app.Activity
import android.os.Bundle
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.menu.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity

class Menu : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu)

        //устанавлваем шрифр
        //-------------------------------------------
        textView_tittle.typeface =  Main.face
        name_i_version.typeface = Main.face
        used_library.typeface = Main.face
        textView_librariry.typeface = Main.face
        textView_ifo.typeface =  Main.face
        name_i_version.typeface = Main.face
        button_edit_fonts.typeface = Main.face
        //---------------------------------------------

        name_i_version.text = "версия:" + getString(R.string.versionName)
        button_edit_fonts.onClick {
            val anim = AnimationUtils.loadAnimation(applicationContext, R.anim.alfa)
            button_edit_fonts.startAnimation(anim)
            startActivity<Fonts_vibor>()
        }
    }


}
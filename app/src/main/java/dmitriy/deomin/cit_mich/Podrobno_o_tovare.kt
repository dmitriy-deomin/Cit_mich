package dmitriy.deomin.cit_mich

import android.app.Activity
import android.os.Bundle
import android.view.WindowManager

class Podrobno_o_tovare : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.podrobno_o_tovare)
        //во весь экран
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)


    }
}

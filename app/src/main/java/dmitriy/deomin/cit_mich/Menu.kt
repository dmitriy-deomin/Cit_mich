package dmitriy.deomin.cit_mich

import android.app.Activity
import android.os.Bundle
import kotlinx.android.synthetic.main.menu.*

class Menu : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu)
        name_i_version.text = getString(R.string.app_name) + " ver:" + getString(R.string.versionName)
    }


}
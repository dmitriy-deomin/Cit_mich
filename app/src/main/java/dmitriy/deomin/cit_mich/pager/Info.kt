package dmitriy.deomin.cit_mich.pager

import android.support.v4.app.Fragment
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import dmitriy.deomin.cit_mich.R
import kotlinx.android.synthetic.main.info.view.*
import org.jetbrains.anko.sdk25.coroutines.onClick


class Info : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val result = inflater.inflate(R.layout.info, container, false)

        //развернёт список с контактами розничного отдела
        result.expandableLayoutroznichniy_otdel.collapse()
        result.roznichniy_otdel.onClick { result.expandableLayoutroznichniy_otdel.toggle() }
        //развернёт список с контактами корпоративный отдел
        result.expandableLayoutkorporotivniy_otdel.collapse()
        result.korporotivniy_otdel.onClick { result.expandableLayoutkorporotivniy_otdel.toggle() }
        //развернёт список с контактами ИНТЕРНЕТ-МАГАЗИН
        result.expandableLayoutinternet_magazin.collapse()
        result.internet_magazin.onClick { result.expandableLayoutinternet_magazin.toggle() }
        //развернёт список с контактами ТЕНДЕРНЫЙ ОТДЕЛ
        result.expandableLayouttenderniy_otdel.collapse()
        result.tenderniy_otdel.onClick { result.expandableLayouttenderniy_otdel.toggle() }
        //развернёт список с контактами ОТДЕЛ ЗАКУПОК
        result.expandableLayoutotdel_zakupok.collapse()
        result.otdel_zakupok.onClick { result.expandableLayoutotdel_zakupok.toggle() }
        //развернёт список с контактами ОТДЕЛ МАРКЕТИНГА
        result.expandableLayoutotdel_marketinga.collapse()
        result.otdel_marketinga.onClick { result.expandableLayoutotdel_marketinga.toggle() }
        //развернёт список с контактами Сервисный центр
        result.expandableLayoutotdel_marketinga.collapse()
        result.servis_centr.onClick { result.expandableLayoutservis_centr.toggle() }
        //развернёт список с контактами Отдел автоматизации (1С)
        result.expandableLayoutotdel_avtomatizacii.collapse()
        result.otdel_avtomatizacii.onClick { result.expandableLayoutotdel_avtomatizacii.toggle() }

        return result
    }

}
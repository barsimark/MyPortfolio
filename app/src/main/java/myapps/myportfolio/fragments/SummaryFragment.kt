package myapps.myportfolio.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import myapps.myportfolio.databinding.FragmentSummaryBinding
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import myapps.myportfolio.data.DataManager

class SummaryFragment : Fragment() {
    private lateinit var binding: FragmentSummaryBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var sumCurrent = 0.0
        var sumOriginal = 0.0

        val entries = mutableListOf<PieEntry>()
        for (element in DataManager.shares){
            sumCurrent += element.number * element.price
            sumOriginal += element.buyValue
            entries.add(PieEntry((element.number * element.price).toFloat(), element.name))
        }
        val dataset = PieDataSet(entries, " ")
        dataset.colors = ColorTemplate.MATERIAL_COLORS.toList()
        val data = PieData(dataset)
        binding.chartSummary.data = data
        binding.chartSummary.setTouchEnabled(false)
        binding.tvTotalNetWorth.text =
            String.format("%.2f (%.2f%%)", sumCurrent, ((sumCurrent / sumOriginal) - 1.0) * 100.0)

        binding.chartSummary.invalidate()
    }
}
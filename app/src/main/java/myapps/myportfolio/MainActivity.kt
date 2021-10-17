package myapps.myportfolio

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.opencsv.CSVReader
import kotlinx.android.synthetic.main.activity_main.*
import myapps.myportfolio.adapter.AssetsRecyclerAdapter
import myapps.myportfolio.adapter.SummaryPagerAdapter
import myapps.myportfolio.data.DataManager
import myapps.myportfolio.data.Share
import myapps.myportfolio.database.MyDatabase
import myapps.myportfolio.databinding.ActivityMainBinding
import myapps.myportfolio.fragments.AdditemFragment
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.InputStreamReader

class MainActivity : AppCompatActivity(),
    AdditemFragment.AssetHandler, AssetsRecyclerAdapter.AssetDeleter {
    private lateinit var binding: ActivityMainBinding
    var assetsStrings = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.tabLayout.visibility = View.GONE

        loadDataFromDatabase()
        loadAssetsList()

        binding.tabLayout.setupWithViewPager(binding.vpSummary)
        binding.vpSummary.adapter = SummaryPagerAdapter(supportFragmentManager)
        binding.fabAddAsset.setOnClickListener {
            AdditemFragment(assetsStrings).show(supportFragmentManager, "ADD_TAG")
        }
    }

    private fun loadAssetsList(){
        Thread {
            val inputStreamReader = InputStreamReader(assets.open("USE_20211014.csv"))
            val bufferedReader = BufferedReader(inputStreamReader)
            bufferedReader.readLine()

            var str = bufferedReader.readLine()

            while (str != null) {
                assetsStrings.add(str.split(",").toTypedArray()[0])
                str = bufferedReader.readLine()
            }

            runOnUiThread {
                binding.tabLayout.visibility = View.VISIBLE
            }
        }.start()
    }

    private fun loadDataFromDatabase(){
        Thread{
            val items = MyDatabase.getInstance(this).assetDao().getAllShares()
            DataManager.shares = items.toMutableList()
            runOnUiThread {
                (binding.vpSummary.adapter as SummaryPagerAdapter).notifyDataSetChanged()
            }
        }.start()
    }

    override fun shareCreated(share: Share) {
        Thread{
            val id = MyDatabase.getInstance(this).assetDao().insertShare(share)
            share.uid = id
            DataManager.shares.add(share)
            runOnUiThread {
                (binding.vpSummary.adapter as SummaryPagerAdapter).notifyDataSetChanged()
            }
        }.start()
    }

    override fun shareDeleted(share: Share) {
        Thread {
            MyDatabase.getInstance(this).assetDao().deleteShare(share)
            DataManager.shares.remove(share)
            runOnUiThread {
                (binding.vpSummary.adapter as SummaryPagerAdapter).notifyDataSetChanged()
            }
        }.start()
    }
}

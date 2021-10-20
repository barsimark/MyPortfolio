package myapps.myportfolio

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.opencsv.CSVReader
import kotlinx.android.synthetic.main.activity_main.*
import myapps.myportfolio.adapter.AssetsRecyclerAdapter
import myapps.myportfolio.adapter.SummaryPagerAdapter
import myapps.myportfolio.data.DataManager
import myapps.myportfolio.data.Share
import myapps.myportfolio.database.MyDatabase
import myapps.myportfolio.databinding.ActivityMainBinding
import myapps.myportfolio.fragments.AdditemFragment
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.*
import java.net.URL
import java.util.jar.Manifest

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
            //AdditemFragment(assetsStrings).show(supportFragmentManager, "ADD_TAG")
            getStockInfo()
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

    private fun getStockInfo(){
        Thread {
            val mediaType = "text/plain".toMediaTypeOrNull()
            val body = RequestBody.create(
                mediaType,
                "\"SELECT * FROM stocks WHERE symbol in ('FB', 'AMZN', 'AAPL', 'NFLX', 'GOOG') ORDER BY price_change_percent_1m DESC\""
            )
            val request = Request.Builder()
                .url("https://hotstoks-sql-finance.p.rapidapi.com/query")
                .post(body)
                .addHeader("content-type", "text/plain")
                .addHeader("x-rapidapi-host", "hotstoks-sql-finance.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "99dd94090emsha0710a563e8e79fp1194bcjsn29b8bb215d6e")
                .build()
            val client = OkHttpClient()
            val response = client.newCall(request).execute()
            runOnUiThread {
                Toast.makeText(this, response.body?.string(), Toast.LENGTH_LONG).show()
            }
        }.start()
    }

    override fun shareBought(share: Share) {
        Thread{
            val res = DataManager.addShare(share)
            if (res == null) {
                val id = MyDatabase.getInstance(this).assetDao().insertShare(share)
                share.uid = id
            }
            else {
                MyDatabase.getInstance(this).assetDao().updateShare(res)
            }
            runOnUiThread {
                (binding.vpSummary.adapter as SummaryPagerAdapter).notifyDataSetChanged()
            }
        }.start()
    }

    override fun shareSold(share: Share) {
        Thread{
            val res = DataManager.sellShare(share)
            if (res != null) {
                if (res.number > 0)
                    MyDatabase.getInstance(this).assetDao().updateShare(res)
                else
                    shareDeleted(res)
            }
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

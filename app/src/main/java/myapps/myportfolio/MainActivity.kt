package myapps.myportfolio

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.work.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import myapps.myportfolio.adapter.AssetsRecyclerAdapter
import myapps.myportfolio.adapter.SummaryPagerAdapter
import myapps.myportfolio.data.DataManager
import myapps.myportfolio.data.Share
import myapps.myportfolio.data.MyDatabase
import myapps.myportfolio.databinding.ActivityMainBinding
import myapps.myportfolio.fragments.AdditemFragment
import myapps.myportfolio.network.WebSQLBuilder
import myapps.myportfolio.network.WebShareMinimal
import myapps.myportfolio.worker.PriceUpdateWorker
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.*
import java.util.concurrent.TimeUnit

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
        loadAvailableStocks()

        binding.tabLayout.setupWithViewPager(binding.vpSummary)
        binding.vpSummary.adapter = SummaryPagerAdapter(supportFragmentManager)
        binding.fabAddAsset.setOnClickListener {
            AdditemFragment(assetsStrings).show(supportFragmentManager, "ADD_TAG")
        }
        binding.swipeLayout.setOnRefreshListener {
            getStockPrice()
        }
        WorkManager.getInstance(this).cancelAllWork()
    }

    override fun onStop(){
        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val periodicWorkRequest = PeriodicWorkRequestBuilder<PriceUpdateWorker>(1, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(this).enqueue(periodicWorkRequest)
        super.onStop()
    }

    private fun loadAvailableStocks(){
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

    private fun getStockPrice(){
        Thread {
            val mediaType = "text/plain".toMediaTypeOrNull()
            val body = RequestBody.create(
                mediaType,
                WebSQLBuilder().SQLNamePriceQuery()
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
            //val res = "{\"count\":1,\"results\":[{\"symbol\":\"AMD\",\"price\":\"40.11\"}]}"
            val res = response.body?.string()
            if (res != null) {
                val gson = Gson()
                val type = object : TypeToken<MutableList<WebShareMinimal>>() {}.type
                val jsonObject = JSONObject(res)
                val parsed: MutableList<WebShareMinimal> =
                    gson.fromJson(jsonObject.getJSONArray("results").toString(), type)
                updateSharePrices(parsed)
            }
        }.start()
    }

    fun updateSharePrices(prices: MutableList<WebShareMinimal>){
        Thread {
            for (webshare in prices) {
                val share = DataManager.updateSharePrice(webshare)
                share?.let {
                    MyDatabase.getInstance(this).assetDao().updateShare(it)
                }
            }
            runOnUiThread {
                (binding.vpSummary.adapter as SummaryPagerAdapter).notifyDataSetChanged()
                binding.swipeLayout.isRefreshing = false
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

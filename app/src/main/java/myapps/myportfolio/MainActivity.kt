package myapps.myportfolio

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import myapps.myportfolio.adapter.AssetsRecyclerAdapter
import myapps.myportfolio.adapter.SummaryPagerAdapter
import myapps.myportfolio.data.DataManager
import myapps.myportfolio.data.Share
import myapps.myportfolio.database.MyDatabase
import myapps.myportfolio.databinding.ActivityMainBinding
import myapps.myportfolio.fragments.AdditemFragment

class MainActivity : AppCompatActivity(),
    AdditemFragment.AssetHandler, AssetsRecyclerAdapter.AssetDeleter {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadDataFromDatabase()

        binding.tabLayout.setupWithViewPager(binding.vpSummary)
        binding.vpSummary.adapter = SummaryPagerAdapter(supportFragmentManager)
        binding.floatingActionButton.setOnClickListener {
            AdditemFragment().show(supportFragmentManager, "ADD_TAG")
        }
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
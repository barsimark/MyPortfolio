package myapps.myportfolio

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import myapps.myportfolio.adapter.AssetsRecyclerAdapter
import myapps.myportfolio.adapter.SummaryPagerAdapter
import myapps.myportfolio.data.DataManager
import myapps.myportfolio.data.Share
import myapps.myportfolio.databinding.ActivityMainBinding
import myapps.myportfolio.fragments.AdditemFragment

class MainActivity : AppCompatActivity(),
    AdditemFragment.AssetHandler, AssetsRecyclerAdapter.AssetDeleter {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tabLayout.setupWithViewPager(binding.vpSummary)
        binding.vpSummary.adapter = SummaryPagerAdapter(supportFragmentManager)
        binding.floatingActionButton.setOnClickListener {
            AdditemFragment().show(supportFragmentManager, "ADD_TAG")
        }
    }

    override fun shareCreated(share: Share) {
        DataManager.shares.add(share)
        (binding.vpSummary.adapter as SummaryPagerAdapter).notifyDataSetChanged()
    }

    override fun shareDeleted(share: Share) {
        DataManager.shares.remove(share)
        (binding.vpSummary.adapter as SummaryPagerAdapter).notifyDataSetChanged()
    }
}
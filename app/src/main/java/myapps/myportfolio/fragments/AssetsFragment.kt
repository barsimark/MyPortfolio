package myapps.myportfolio.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import myapps.myportfolio.R
import myapps.myportfolio.adapter.AssetsRecyclerAdapter
import myapps.myportfolio.data.Share
import myapps.myportfolio.databinding.FragmentAssetsBinding
import myapps.myportfolio.touch.AssetsTouchHelperAdapter
import myapps.myportfolio.touch.AssetsTouchHelperCallback

class AssetsFragment : Fragment() {
    private lateinit var binding: FragmentAssetsBinding
    private lateinit var adapter: AssetsRecyclerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAssetsBinding.inflate(inflater, container, false)
        adapter = AssetsRecyclerAdapter(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvAssets.adapter = adapter
        val callback = AssetsTouchHelperCallback(adapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(binding.rvAssets)

        adapter.refresh()
    }
}
package myapps.myportfolio.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import myapps.myportfolio.R
import myapps.myportfolio.adapter.AssetsRecyclerAdapter
import myapps.myportfolio.data.Share
import myapps.myportfolio.databinding.FragmentAssetsBinding

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
        adapter.refresh()
    }
}
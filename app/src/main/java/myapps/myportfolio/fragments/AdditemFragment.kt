package myapps.myportfolio.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import myapps.myportfolio.R
import myapps.myportfolio.adapter.AssetsRecyclerAdapter
import myapps.myportfolio.databinding.FragmentAdditemBinding
import myapps.myportfolio.databinding.FragmentAssetsBinding

class AdditemFragment : DialogFragment() {
    private lateinit var binding: FragmentAdditemBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAdditemBinding.inflate(inflater, container, false)
        return binding.root
    }
}
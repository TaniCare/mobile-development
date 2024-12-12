package com.dicoding.tanicare.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.tanicare.databinding.FragmentHistoryBinding
import com.dicoding.tanicare.viewmodel.ViewModelFactory

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HistoryViewModel by viewModels { ViewModelFactory(requireContext()) }
    private lateinit var adapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = HistoryAdapter()

        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistory.adapter = adapter

        binding.lottieLoading.visibility = View.VISIBLE

        viewModel.allHistory.observe(viewLifecycleOwner) { historyList ->
            binding.lottieLoading.visibility = View.GONE
            if (historyList.isNullOrEmpty()) {
                binding.tvEmptyMessage.visibility = View.VISIBLE
            } else {
                binding.tvEmptyMessage.visibility = View.GONE
            }
            adapter.submitList(historyList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

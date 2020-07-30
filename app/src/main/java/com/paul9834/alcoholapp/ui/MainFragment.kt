package com.paul9834.alcoholapp.ui

import android.opengl.Visibility
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.SlideDistanceProvider
import com.paul9834.alcoholapp.AppDataBase

import com.paul9834.alcoholapp.R
import com.paul9834.alcoholapp.data.model.DataSource
import com.paul9834.alcoholapp.data.model.Drink
import com.paul9834.alcoholapp.domain.RepoImpl
import com.paul9834.alcoholapp.ui.adapter.MainAdapter
import com.paul9834.alcoholapp.ui.viewmodel.MainViewModel
import com.paul9834.alcoholapp.ui.viewmodel.VMFactory
import com.paul9834.alcoholapp.vo.Resource
import kotlinx.android.synthetic.main.fragment_main.*


class MainFragment : Fragment(), MainAdapter.onTragoClickListener {


    private val viewModel by activityViewModels<MainViewModel> {
        VMFactory(RepoImpl(DataSource(AppDataBase.getDatabase(requireActivity().applicationContext))))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialFadeThrough()



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchView()
        setupObservers ()

        swiperefresh.isEnabled = false

        btn_ir_favoritos.setOnClickListener{
            findNavController().navigate(R.id.action_mainFragment_to_favoriteDrinkFragment)
        }


    }

    private fun setupObservers () {
        viewModel.fetchTragosList.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is Resource.Loading -> {
                    swiperefresh.isRefreshing = true
                }
                is Resource.Success -> {
                    rv_tragos.adapter = MainAdapter(requireContext(), result.data, this)

                    swiperefresh.isRefreshing = false
                }
                is Resource.Failure -> {
                    swiperefresh.isRefreshing = false
                    Toast.makeText(requireContext(), "Ocurrio un error en cargar los datos ${result.exception}", Toast.LENGTH_SHORT).show()
                }

            }
        })
    }

    private fun setupSearchView () {

        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.setTrago(query!!)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })

    }

    private fun setupRecyclerView () {

        rv_tragos.layoutManager = LinearLayoutManager(requireContext())
        rv_tragos.addItemDecoration(DividerItemDecoration(requireContext(),DividerItemDecoration.VERTICAL))

    }

    override fun onTragoClick(drink: Drink) {

        val bundle = Bundle()
        bundle.putParcelable("drink", drink)
       findNavController().navigate(R.id.detallesTragoFragment, bundle)


    }



}
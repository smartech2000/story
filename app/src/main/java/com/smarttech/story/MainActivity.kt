package com.smarttech.story

import android.os.Bundle
import android.view.Menu
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SearchView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.smarttech.story.ui.category.CategoryFragmentDirections
import com.smarttech.story.ui.story.StoryFragment

class MainActivity : AppCompatActivity() {
    lateinit var adapter: ArrayAdapter<*>
    lateinit var navController: NavController
    lateinit var navHostFragment: NavHostFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        //val navController = findNavController(R.id.nav_host_fragment)
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.category_fragment, R.id.navigation_bookself, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        adapter = ArrayAdapter<Any?>(
            this, android.R.layout.simple_list_item_1,
            resources.getStringArray(R.array.months_array)
        )
    }

    @Override
    override fun onSupportNavigateUp(): Boolean {
        return this.findNavController(R.id.nav_host_fragment)
            .navigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        val search = menu.findItem(R.id.appSearchBar)
        val searchView = search.actionView as SearchView
        searchView.queryHint = "Tìm theo tên hoặc tác giả"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val action = query?.let {
                    CategoryFragmentDirections
                        .actionCategoryFragmentToStoryFragment(-1, it)
                }
                var navHostFragmentCheck =
                    navHostFragment.getChildFragmentManager().getFragments().get(0);
                if (navHostFragmentCheck is StoryFragment == false) {
                    action?.let { navController.navigate(it) }
                } else {
                    val action = query?.let {
                        CategoryFragmentDirections
                            .actionCategoryFragmentToStoryFragment(-1, it)
                    }
                    val id = navController.currentDestination?.id
                    navController.popBackStack(id!!,true)
                    action?.let { navController.navigate(it) }
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }
    private fun refreshCurrentFragment(query:String){
    }
}
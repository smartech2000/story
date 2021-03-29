package com.smarttech.story

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SearchView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.smarttech.story.database.AppDatabase
import com.smarttech.story.model.local.History
import com.smarttech.story.ui.category.CategoryFragmentDirections
import com.smarttech.story.ui.story.StoryFragment
import com.smarttech.story.utils.InitData

class MainActivity : AppCompatActivity() {
    lateinit var adapter: ArrayAdapter<*>
    lateinit var navController: NavController
    lateinit var navHostFragment: NavHostFragment
    private lateinit var db: AppDatabase;
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
                R.id.category_fragment, R.id.navigation_bookself, R.id.navigation_search
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        adapter = ArrayAdapter<Any?>(
            this, android.R.layout.simple_list_item_1,
            resources.getStringArray(R.array.months_array)
        )

        //db = AppDatabase(this);
       // val storyDao = db.storyDao();
        //InitData().InitData(storyDao)
       // val historyLocals = storyDao.getAllHistoryLocal();

    }

    @Override
    override fun onSupportNavigateUp(): Boolean {
        return this.findNavController(R.id.nav_host_fragment)
            .navigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)

        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.appSearchBar -> {
                val searchView = item.actionView as SearchView
                searchView.setMaxWidth(Integer.MAX_VALUE)
                searchView.queryHint = "Tìm theo tên hoặc tác giả"
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        val bundle = bundleOf("categoryName" to query, "categoryId"  to -1)
                        navController.navigate(R.id.story_fragment, bundle)
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        adapter.filter.filter(newText)
                        return true
                    }
                })
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}
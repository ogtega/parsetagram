package de.tolunla.parsetagram.view

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import de.tolunla.parsetagram.R
import de.tolunla.parsetagram.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfig: AppBarConfiguration
    private lateinit var navGraph: NavGraph

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        navController = binding.fragmentContainerView.getFragment<NavHostFragment>().navController
        navGraph = navController.navInflater.inflate(R.navigation.nav_graph)

        // Used to know when we are at a "top level" destination
        appBarConfig = AppBarConfiguration.Builder(
            R.id.feed_dst,
        ).build()

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            this.onOptionsItemSelected(item)
        }

        setupNavigation()

        setContentView(binding.root)
    }

    private fun setupNavigation() {
        navController.graph = navGraph
        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navController, appBarConfig)
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (!appBarConfig.topLevelDestinations.contains(destination.id)) {
                binding.bottomNavigation.visibility = View.GONE
            } else {
                binding.bottomNavigation.visibility = View.VISIBLE
            }

            if (destination.id in listOf(R.id.login_dst, R.id.register_dst)) {
                binding.toolbar.visibility = View.GONE
            } else {
                binding.toolbar.visibility = View.VISIBLE
            }
        }
    }
}
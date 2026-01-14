package hr.algebra.nasa

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import hr.algebra.nasa.databinding.ActivityHostBinding

class HostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initHamburgerMenu()
        initNavigation()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.host_menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                toggleDrawer()
                return true
            }
            R.id.menuExit -> {
                exitApp()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun exitApp() {
        AlertDialog.Builder(this).apply {
            setTitle(R.string.exit)
            setMessage(getString(R.string.do_you_really_want_to_exit))
            setIcon(R.drawable.exit)
            setNegativeButton(getString(R.string.cancel), null)
            setPositiveButton("OK") { _, _ -> finish() }
            show()
        }
    }

    private fun toggleDrawer() {
        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawers()
        } else {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun initHamburgerMenu() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
    }

    private fun initNavigation() {
        val navController = findNavController(this, R.id.navController)
        NavigationUI.setupWithNavController(binding.navView, navController)
    }
}
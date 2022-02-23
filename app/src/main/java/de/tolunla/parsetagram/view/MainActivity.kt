package de.tolunla.parsetagram.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import de.tolunla.parsetagram.R
import de.tolunla.parsetagram.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setSupportActionBar(binding.toolbar)
        binding.root
    }
}
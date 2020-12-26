package hr.ficko.transactionmonitor.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import hr.ficko.transactionmonitor.R
import hr.ficko.transactionmonitor.databinding.ActivityMainBinding
import hr.ficko.transactionmonitor.viewModels.RepositoryViewModel

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<RepositoryViewModel>()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
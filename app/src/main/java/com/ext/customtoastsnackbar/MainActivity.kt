package com.ext.customtoastsnackbar

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ext.custom_toast_snackbar.CustomSnackbar
import com.ext.custom_toast_snackbar.CustomToast
import com.ext.customtoastsnackbar.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.snackbar1.setAutoShow(false)
        binding.snackbar2.setAutoShow(false)
        binding.snackbar3.setAutoShow(false)
        binding.toast1.setAutoShow(false)
        binding.toast2.setAutoShow(false)
        binding.toast3.setAutoShow(false)

        // Custom Toast
//            CustomToast(this)
//                .setText("Data synced successfully!")
//                .setIcon(ContextCompat.getDrawable(this, android.R.drawable.ic_menu_upload))
//                .setIconSize(40f)
//                .setToastBackgroundColor(Color.parseColor("#2196F3"))  // FIXED
//                .setTextColor(Color.BLACK)
//                .setCornerRadius(30f)
//                .setDuration(Toast.LENGTH_LONG)
//                .show()

        // Custom Snackbar
//            CustomSnackbar(this)  // ← Pass Context (Activity)
//                .setParentView(binding.root)  // ← Then set the parent view
//                .setText("Sync complete • 5 items")
//                .setIcon(ContextCompat.getDrawable(this, android.R.drawable.ic_menu_save))
//                .setIconSize(40f)
//                .setSnackbarBackgroundColor(Color.parseColor("#8BC34A"))
//                .setTextColor(Color.BLACK)
//                .setCornerRadius(32f)
//                .setCustomMargins(44, 0, 44, 140)
//                .setDuration(com.google.android.material.snackbar.Snackbar.LENGTH_LONG)
//                .setAutoShow(false)
//                .show()

    }
}
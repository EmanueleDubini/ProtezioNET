package it.insubria.protezionet.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import it.insubria.protezionet.admin.R

/**
 * Activity utilizzata per visualizzare lo splash screen prima di entrare nell'activity di login
 */
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler().postDelayed(
                {
                    // Terminati i 3 secondi di attesa verrà lanciata l'activity
                    // principale e terminata quella di launch
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                },
                3000 // La Launch Screen rimarrà visibile per 3 secondi
        )
    }
}
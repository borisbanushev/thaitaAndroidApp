package app.thaita

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import app.thaita.ui.navigation.ThaitaNavGraph
import app.thaita.ui.theme.ThaitaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ThaitaTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ThaitaNavGraph()
                }
            }
        }
    }
}

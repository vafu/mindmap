package v47.mindmap.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getKoin
import org.koin.androidx.compose.koinViewModel
import v47.mindmap.DefaultThoughtViewModel
import v47.mindmap.ThoughtViewModel
import v47.mindmap.android.ui.AddScreen
import v47.mindmap.android.ui.Nav
import v47.mindmap.android.ui.ThoughtScreen
import v47.mindmap.android.ui.theme.MyApplicationTheme
import v47.mindmap.common.Id
import v47.mindmap.connections.StaticConnectionsRepository
import v47.mindmap.thought.StaticThoughtRepository

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            MyApplicationTheme {
                NavHost(
                    navController = navController,
                    startDestination = Nav.MAIN
                ) {
                    composable(Nav.MAIN) {
                        ThoughtScreen(navController)
                    }
                    composable(Nav.ADD) {
                        AddScreen()
                    }
                }
            }
        }
    }
}

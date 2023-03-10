package v47.mindmap.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import v47.mindmap.android.ui.Navigation
import v47.mindmap.android.ui.Navigator
import v47.mindmap.android.ui.ThoughtPreviewScreen
import v47.mindmap.android.ui.theme.MyApplicationTheme

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            MyApplicationTheme {
                val navController = rememberNavController()
                val navigator = remember<Navigator> {
                    {
                        navController.navigate(it.tag)
                    }
                }
                NavHost(
                    navController = navController,
                    startDestination = Navigation.Main.tag
                ) {

                    composable(Navigation.Main.tag) {
                        ThoughtPreviewScreen()
                    }
                }
            }
        }
    }
}
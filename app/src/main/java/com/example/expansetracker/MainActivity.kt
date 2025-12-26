package com.example.expansetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import com.example.expansetracker.navigation.AppNavGraph
import com.example.expansetracker.ui.theme.ExpenseTrackerTheme
import androidx.fragment.app.FragmentActivity

//@AndroidEntryPoint
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            ExpenseTrackerTheme {
//                AppNavGraph()
//            }
//        }
//    }
//}

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ExpenseTrackerTheme {
                AppNavGraph()
            }
        }
    }
}

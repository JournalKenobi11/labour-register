package com.labourregister.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.labourregister.app.ui.navigation.AppBottomBar
import com.labourregister.app.ui.navigation.Screen
import com.labourregister.app.ui.screens.AddLabourScreen
import com.labourregister.app.ui.screens.LabourRegisterScreen
import com.labourregister.app.ui.screens.ShopInquiryScreen
import com.labourregister.app.ui.theme.LabourRegisterTheme
import com.labourregister.app.viewmodel.LabourViewModel
import com.labourregister.app.viewmodel.LabourViewModelFactory
import com.labourregister.app.viewmodel.ShopInquiryViewModel
import com.labourregister.app.viewmodel.ShopInquiryViewModelFactory

class MainActivity : ComponentActivity() {

    private val labourViewModel: LabourViewModel by viewModels {
        val app = application as LabourApplication
        LabourViewModelFactory(app.repository)
    }

    private val inquiryViewModel: ShopInquiryViewModel by viewModels {
        val app = application as LabourApplication
        ShopInquiryViewModelFactory(app.shopInquiryRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LabourRegisterTheme {
                MainAppContent(
                    labourViewModel = labourViewModel,
                    inquiryViewModel = inquiryViewModel
                )
            }
        }
    }
}

@Composable
fun MainAppContent(
    labourViewModel: LabourViewModel,
    inquiryViewModel: ShopInquiryViewModel
) {
    var currentScreen by remember { mutableStateOf(Screen.ADD_LABOUR) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            AppBottomBar(
                currentScreen = currentScreen,
                onNavigate = { screen ->
                    currentScreen = screen
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                Screen.ADD_LABOUR -> {
                    AddLabourScreen(viewModel = labourViewModel)
                }
                Screen.LABOUR_REGISTER -> {
                    LabourRegisterScreen(viewModel = labourViewModel)
                }
                Screen.INQUIRIES -> {
                    ShopInquiryScreen(viewModel = inquiryViewModel)
                }
            }
        }
    }
}

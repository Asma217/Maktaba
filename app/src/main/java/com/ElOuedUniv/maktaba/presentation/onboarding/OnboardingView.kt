package com.ElOuedUniv.maktaba.presentation.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ElOuedUniv.maktaba.data.repository.OnboardingRepository
import com.ElOuedUniv.maktaba.presentation.navigation.Screen
import kotlinx.coroutines.launch

@Composable
fun OnboardingView(
    navController: NavController,
    onboardingRepository: OnboardingRepository,
    onNavigateToLibrary: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to Maktaba", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Your personal digital library.")
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = {
            coroutineScope.launch {
                onboardingRepository.setOnboardingCompleted()
                navController.navigate(Screen.BookList.route) {
                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                }
            }
        }) {
            Text("Get Started")
        }
    }
}

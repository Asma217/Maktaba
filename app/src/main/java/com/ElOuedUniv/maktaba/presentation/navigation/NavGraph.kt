package com.ElOuedUniv.maktaba.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ElOuedUniv.maktaba.data.repository.OnboardingRepository
import com.ElOuedUniv.maktaba.presentation.book.BookListView
import com.ElOuedUniv.maktaba.presentation.book.add.AddBookView
import com.ElOuedUniv.maktaba.presentation.book.detail.BookDetailView
import com.ElOuedUniv.maktaba.presentation.category.CategoryListView
import com.ElOuedUniv.maktaba.presentation.onboarding.OnboardingView

@Composable
fun NavGraph(
    onboardingRepository: OnboardingRepository,
    navController: NavHostController = rememberNavController()
) {
    val hasOnboarded by onboardingRepository.hasCompletedOnboarding
        .collectAsState(initial = false)

    NavHost(
        navController = navController,
        startDestination = if (hasOnboarded) Screen.BookList.route else Screen.Onboarding.route
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingView(
                navController = navController,
                onboardingRepository = onboardingRepository,
                onNavigateToLibrary = {
                    navController.navigate(Screen.BookList.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.BookList.route) {
            BookListView(
                onCategoriesClick = {
                    navController.navigate(Screen.CategoryList.route)
                },
                onAddBookClick = {
                    navController.navigate(Screen.AddBook.route)
                },
                onBookClick = { isbn ->
                    navController.navigate(Screen.BookDetail.createRoute(isbn))
                }
            )
        }

        composable(Screen.AddBook.route) {
            AddBookView(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.CategoryList.route) {
            CategoryListView(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.BookDetail.route,
            arguments = listOf(navArgument("isbn") { type = NavType.StringType })
        ) {
            BookDetailView(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

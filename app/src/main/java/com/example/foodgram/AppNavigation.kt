package com.example.foodgram

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.foodgram.ui.theme.main.MainScreen
import com.example.foodgram.ui.theme.main.MainViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.navArgument

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel: MainViewModel = viewModel()
    var title by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("") }
    var cookingTime by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var ingredients by rememberSaveable { mutableStateOf(listOf("")) }
    var instructions by rememberSaveable { mutableStateOf(listOf("")) }
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentRoute == Screen.Feed.route,
                    onClick = {
                        navController.navigate(Screen.Feed.route) {
                            popUpTo(Screen.Feed.route) {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    },
                    icon = {
                        Icon(Icons.Default.Home, contentDescription = "Feed")
                    },
                    label = { Text("Feed") }
                )

                NavigationBarItem(
                    selected = currentRoute == Screen.AddRecipe.route,
                    onClick = {
                        navController.navigate(Screen.AddRecipe.route) {
                            launchSingleTop = true
                        }
                    },
                    icon = {
                        Icon(Icons.Default.Add, contentDescription = "Add recipe")
                    },
                    label = { Text("Add") }
                )

                NavigationBarItem(
                    selected = currentRoute == Screen.Profile.route,
                    onClick = {
                        navController.navigate(Screen.Profile.route) {
                            launchSingleTop = true
                        }
                    },
                    icon = {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
                    },
                    label = { Text("Profile") }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Feed.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Feed.route) {
                MainScreen(
                    viewModel = viewModel,
                    navController = navController
                )
            }

            composable(Screen.AddRecipe.route) {
                AddRecipeScreen(
                    title = title,
                    onTitleChange = { title = it },
                    category = category,
                    onCategoryChange = { category = it },
                    cookingTime = cookingTime,
                    onCookingTimeChange = { cookingTime = it },
                    description = description,
                    onDescriptionChange = { description = it },
                    onRecipeAdded = { recipe ->
                        title = ""
                        category = ""
                        cookingTime = ""
                        description = ""
                        ingredients = listOf("")
                        instructions = listOf("")
                        viewModel.addRecipe(recipe)
                        navController.popBackStack()
                    },
                    ingredients = ingredients,
                    onIngredientsChange = { ingredients = it },
                    instructions = instructions,
                    onInstructionsChange = { instructions = it },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    viewModel = viewModel
                )
            }

            composable(
                route = Screen.UserProfile.route,
                arguments = listOf(
                    navArgument("userId") {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId")

                if (userId != null) {
                    UserProfileScreen(
                        userId = userId,
                        navController = navController,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}
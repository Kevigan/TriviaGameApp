package com.example.triviagameapp.Views

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.triviagameapp.R
import com.example.triviagameapp.Screen
import com.example.triviagameapp.ui.theme.QuizCyan
import com.example.triviagameapp.ui.theme.SoftWhite

@Composable
fun CategoryView(navController: NavController) {
    // Sample category data, you can replace with your actual categories
    val categories = listOf(
        Category("Science", R.drawable.game_background,17),
        Category("Math", R.drawable.game_background, 19),
        Category("History", R.drawable.game_background, 23),
        Category("Geography", R.drawable.game_background, 22),
        Category("Art", R.drawable.game_background, 25),
        Category("Music", R.drawable.game_background, 12),
        Category("Sports", R.drawable.game_background, 21),
        Category("Technology", R.drawable.game_background, 18),
        Category("Musicals & Theatres", R.drawable.game_background, 11),
        Category("Japanese Anime & Manga", R.drawable.game_background, 11),
        // Add more categories if needed
    )

    // LazyVerticalGrid for displaying categories in 3 columns
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),  // 3 categories in each row
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp) // Adjust padding as necessary
    ) {
        items(categories.size) { index ->
            val category = categories[index]
            CategoryItem(category = category, onClick = {
                // Handle category click (navigate to category screen or perform other actions)
                navController.navigate("gameScreen/${category.id}")
            })
        }
    }
}

@Composable
fun CategoryItem(category: Category, onClick: () -> Unit) {
    // Each category item inside a card, with an image and text
    Card(
        modifier = Modifier
            .padding(8.dp)  // Space between items
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp,
        backgroundColor = QuizCyan
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
                .height(150.dp)
        ) {
            // Category image with fixed size
            Image(
                painter = painterResource(id = category.imageRes),
                contentDescription = category.name,
                modifier = Modifier
                    .size(85.dp) // Set a consistent size for all images
                    .align(Alignment.CenterHorizontally) // Center image inside the card
            )
            Spacer(modifier = Modifier.height(8.dp)) // Spacer between image and text
            // Category name text
            Text(
                text = category.name,
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center, // Center the text
                modifier = Modifier.fillMaxWidth(),
                color = SoftWhite
            )
        }
    }
}

// Category data model
data class Category(val name: String, val imageRes: Int, val id: Int)

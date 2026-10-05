package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.R
import com.example.model.DailyNutritionTrend
import com.example.model.MacroNutrient
import com.example.model.MealItem
import com.example.model.RecipeItem
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun NutritionScreen(
    meals: List<MealItem>,
    macros: List<MacroNutrient>,
    waterGlasses: Int,
    recipes: List<RecipeItem>,
    weeklyNutritionTrends: List<DailyNutritionTrend> = emptyList(),
    onToggleMeal: (String) -> Unit,
    onToggleMealFavorite: (String) -> Unit = {},
    onToggleRecipeFavorite: (String) -> Unit = {},
    onAddRecipeToToday: (RecipeItem) -> Unit = {},
    onAddWater: () -> Unit,
    onRemoveWater: () -> Unit = {},
    onSetWaterGlasses: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf("Today") }
    val tabs = listOf("Today", "Meal Plan", "Recipes", "Nutrition Tips")

    val totalCalories = meals.sumOf { it.calories }
    val eatenCalories = meals.filter { it.isEaten }.sumOf { it.calories }

    // Dialog state for viewing full meal/recipe details
    var selectedMealForDetails by remember { mutableStateOf<MealItem?>(null) }
    var selectedRecipeForDetails by remember { mutableStateOf<RecipeItem?>(null) }

    // Meal Detail Modal
    selectedMealForDetails?.let { meal ->
        MealDetailDialog(
            title = com.example.ui.FitPlanLocalization.getMealName(meal),
            category = com.example.ui.FitPlanLocalization.getMealTypeName(meal.mealType),
            timeOrPrep = meal.time,
            calories = meal.calories,
            protein = meal.protein,
            carbs = meal.carbs,
            fats = meal.fats,
            fullAssetUrl = meal.fullAssetUrl,
            ingredients = com.example.ui.FitPlanLocalization.getMealIngredients(meal),
            instructions = com.example.ui.FitPlanLocalization.getMealInstructions(meal),
            isFavorite = meal.isFavorite,
            onToggleFavorite = {
                onToggleMealFavorite(meal.id)
                selectedMealForDetails = meal.copy(isFavorite = !meal.isFavorite)
            },
            primaryButtonText = if (meal.isEaten) stringResource(R.string.mark_as_not_eaten) else stringResource(R.string.mark_as_eaten),
            primaryButtonColor = if (meal.isEaten) FitPlanCardBorder else FitPlanCyan,
            primaryButtonTextColor = if (meal.isEaten) FitPlanTextWhite else FitPlanBlack,
            onPrimaryAction = {
                onToggleMeal(meal.id)
                selectedMealForDetails = meal.copy(isEaten = !meal.isEaten)
            },
            onDismiss = { selectedMealForDetails = null }
        )
    }

    // Recipe Detail Modal
    selectedRecipeForDetails?.let { recipe ->
        MealDetailDialog(
            title = com.example.ui.FitPlanLocalization.getRecipeTitle(recipe),
            category = com.example.ui.FitPlanLocalization.getCategoryLabel(recipe.category),
            timeOrPrep = "${recipe.prepTimeMinutes} ${stringResource(R.string.min_unit)} • ${com.example.ui.FitPlanLocalization.getDifficultyLabel(recipe.difficulty)}",
            calories = recipe.calories,
            protein = recipe.protein,
            carbs = recipe.carbs,
            fats = recipe.fats,
            fullAssetUrl = recipe.fullAssetUrl,
            ingredients = com.example.ui.FitPlanLocalization.getRecipeIngredients(recipe),
            instructions = com.example.ui.FitPlanLocalization.getRecipeInstructions(recipe),
            isFavorite = recipe.isFavorite,
            onToggleFavorite = {
                onToggleRecipeFavorite(recipe.id)
                selectedRecipeForDetails = recipe.copy(isFavorite = !recipe.isFavorite)
            },
            primaryButtonText = stringResource(R.string.add_to_today_meals),
            primaryButtonColor = FitPlanNeonLime,
            primaryButtonTextColor = FitPlanBlack,
            onPrimaryAction = {
                onAddRecipeToToday(recipe)
                selectedRecipeForDetails = null
            },
            onDismiss = { selectedRecipeForDetails = null }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(FitPlanBlack)
            .testTag("nutrition_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        item {
            FitPlanTopBar()
        }

        // Hero Banner
        item {
            HeroBanner(
                badgeText = stringResource(R.string.good_food_better_you),
                title = stringResource(R.string.nutrition_subtitle),
                subtitle = stringResource(R.string.nutrition_subtitle),
                ctaText = stringResource(R.string.todays_meals).uppercase(),
                accentColor = FitPlanCyan,
                onCtaClick = { selectedTab = "Today" }
            )
        }

        // Nutrition Sub-tabs
        item {
            CategoryTabsRow(
                tabs = tabs,
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                activeColor = FitPlanCyan
            )
        }

        // Calories Today & Macros Card
        item {
            CaloriesMacrosCard(
                consumed = eatenCalories,
                target = 2400,
                macros = macros,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }

        // Weekly Calorie & Protein Nutrition Consumption Trends Visualization (Native Canvas Chart)
        if (weeklyNutritionTrends.isNotEmpty()) {
            item {
                WeeklyNutritionTrendsChart(
                    trends = weeklyNutritionTrends,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
        }

        // Quick Action Shortcuts (Calorie Tracker, Scan Food, Meal Planner, My Foods)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NutritionActionChip(stringResource(R.string.calorie_tracker), Icons.Default.Calculate, Modifier.weight(1f))
                NutritionActionChip(stringResource(R.string.scan_food), Icons.Default.QrCodeScanner, Modifier.weight(1f))
                NutritionActionChip(stringResource(R.string.meal_planner), Icons.Default.CalendarMonth, Modifier.weight(1f))
                NutritionActionChip(stringResource(R.string.my_foods), Icons.Default.Bookmark, Modifier.weight(1f))
            }
        }

        // Meal Logging Reminder Status Strip
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(FitPlanCard)
                    .border(1.dp, FitPlanCardBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = FitPlanCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.meal_logging_reminders),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = FitPlanTextWhite
                        )
                    }
                    Text(
                        text = stringResource(R.string.meal_logging_times),
                        style = MaterialTheme.typography.labelSmall,
                        color = FitPlanCyan,
                        fontWeight = FontWeight.Medium,
                        fontSize = 10.sp
                    )
                }
            }
        }

        // 1. Daily Meals Section (shown in "Today" and "Meal Plan")
        if (selectedTab == "Today" || selectedTab == "Meal Plan") {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(FitPlanCyan)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.todays_meals),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = FitPlanTextWhite
                        )
                    }
                    Text(
                        text = "${meals.count { it.isEaten }}/${meals.size} ${stringResource(R.string.logged_badge)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = FitPlanCyan,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            items(meals) { meal ->
                MealItemCard(
                    meal = meal,
                    onToggle = { onToggleMeal(meal.id) },
                    onOpenDetails = { selectedMealForDetails = meal },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }

        // Water Intake Tracker (shown in "Today")
        if (selectedTab == "Today") {
            item {
                WaterTrackerWidget(
                    currentGlasses = waterGlasses,
                    targetGlasses = 8,
                    onAddGlass = onAddWater,
                    onRemoveGlass = onRemoveWater,
                    onSetGlasses = onSetWaterGlasses,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }
        }

        // 2. Healthy Recipes Section (shown in "Today" and "Recipes")
        if (selectedTab == "Today" || selectedTab == "Recipes") {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(FitPlanNeonLime)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.healthy_recipes),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = FitPlanTextWhite
                        )
                    }
                    Text(
                        text = "${recipes.size} ${stringResource(R.string.chef_recipes_badge)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = FitPlanNeonLime,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            items(recipes) { recipe ->
                RecipeCard(
                    recipe = recipe,
                    onOpenDetails = { selectedRecipeForDetails = recipe },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }

        // 3. Nutrition Tips Section (shown in "Nutrition Tips")
        if (selectedTab == "Nutrition Tips") {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    NutritionTipCard(
                        title = "Protein Timing & Hypertrophy",
                        tip = "Distribute 25-40g of high-quality protein evenly every 3 to 4 hours to maximize muscle protein synthesis throughout the day.",
                        icon = Icons.Default.FitnessCenter,
                        accentColor = FitPlanNeonLime
                    )
                    NutritionTipCard(
                        title = stringResource(R.string.tip_carbs_title),
                        tip = stringResource(R.string.tip_carbs_desc),
                        icon = Icons.Default.Bolt,
                        accentColor = FitPlanMorningOrange
                    )
                    NutritionTipCard(
                        title = stringResource(R.string.tip_hydration_title),
                        tip = stringResource(R.string.tip_hydration_desc),
                        icon = Icons.Default.WaterDrop,
                        accentColor = FitPlanCyan
                    )
                    NutritionTipCard(
                        title = stringResource(R.string.tip_omega3_title),
                        tip = stringResource(R.string.tip_omega3_desc),
                        icon = Icons.Default.Favorite,
                        accentColor = FitPlanHiitRed
                    )
                }
            }
        }
    }
}

@Composable
fun CaloriesMacrosCard(
    consumed: Int,
    target: Int,
    macros: List<MacroNutrient>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(FitPlanCard)
            .border(1.dp, FitPlanCardBorder, RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.calories_today),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = FitPlanTextWhite
                    )
                    Text(
                        text = "$consumed / $target ${stringResource(R.string.kcal_unit)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = FitPlanTextSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .background(FitPlanCyan.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${((consumed.toFloat() / target) * 100).toInt()}% ${stringResource(R.string.done_suffix)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = FitPlanCyan,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Macro Nutrients Row (Protein, Carbs, Fats)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                macros.forEach { macro ->
                    val color = Color(macro.colorHex)
                    val progress = (macro.current.toFloat() / macro.goal).coerceIn(0f, 1f)

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(FitPlanSurface)
                            .padding(10.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = com.example.ui.FitPlanLocalization.getMacroName(macro.name),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = FitPlanTextSecondary
                                )
                                Text(
                                    text = "${macro.current}g",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = color
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(CircleShape),
                                color = color,
                                trackColor = FitPlanCardBorder
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${stringResource(R.string.target)}: ${macro.goal}g",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 9.sp,
                                color = FitPlanTextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Meal Card with Large Real Photograph at the Top, rounded corners,
 * meal name, meal time, calories, protein, and details button.
 */
@Composable
fun MealItemCard(
    meal: MealItem,
    onToggle: () -> Unit,
    onOpenDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(FitPlanCard)
            .border(
                1.dp,
                if (meal.isEaten) FitPlanCyan.copy(alpha = 0.5f) else FitPlanCardBorder,
                RoundedCornerShape(18.dp)
            )
            .clickable { onOpenDetails() }
    ) {
        Column {
            // Big Food Image on Top
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                    .background(FitPlanSurface)
            ) {
                if (meal.fullAssetUrl.isNotEmpty()) {
                    AsyncImage(
                        model = meal.fullAssetUrl,
                        contentDescription = meal.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Gradient overlay for text legibility
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.65f))
                            )
                        )
                )

                // Top-Left Badge: Meal Type & Time
                Box(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopStart)
                        .background(FitPlanBlack.copy(alpha = 0.75f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${com.example.ui.FitPlanLocalization.getMealTypeName(meal.mealType).uppercase()} • ${meal.time}",
                        style = MaterialTheme.typography.labelSmall,
                        color = FitPlanCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }

                // Top-Right: Eaten status toggle button
                IconButton(
                    onClick = onToggle,
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopEnd)
                        .size(36.dp)
                        .background(FitPlanBlack.copy(alpha = 0.75f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (meal.isEaten) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = if (meal.isEaten) "Eaten" else "Mark Eaten",
                        tint = if (meal.isEaten) FitPlanCyan else FitPlanTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Bottom Content Section
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = com.example.ui.FitPlanLocalization.getMealName(meal),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (meal.isEaten) FitPlanTextSecondary else FitPlanTextWhite
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Calories
                        Box(
                            modifier = Modifier
                                .background(FitPlanSurface, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = null,
                                    tint = FitPlanMorningOrange,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "${meal.calories} kcal",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = FitPlanTextWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Protein
                        Box(
                            modifier = Modifier
                                .background(FitPlanCyan.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${meal.protein}g protein",
                                style = MaterialTheme.typography.labelSmall,
                                color = FitPlanCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Button to open meal details
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(FitPlanCyan.copy(alpha = 0.15f))
                            .clickable { onOpenDetails() }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stringResource(R.string.nutrition_details_action),
                                style = MaterialTheme.typography.labelSmall,
                                color = FitPlanCyan,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = FitPlanCyan,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Recipe Card with Large Real Photograph at the Top, rounded corners,
 * recipe title, category/prep time, calories, protein, and details button.
 */
@Composable
fun RecipeCard(
    recipe: RecipeItem,
    onOpenDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(FitPlanCard)
            .border(1.dp, FitPlanCardBorder, RoundedCornerShape(18.dp))
            .clickable { onOpenDetails() }
    ) {
        Column {
            // Big Recipe Image on Top
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                    .background(FitPlanSurface)
            ) {
                if (recipe.fullAssetUrl.isNotEmpty()) {
                    AsyncImage(
                        model = recipe.fullAssetUrl,
                        contentDescription = recipe.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.65f))
                            )
                        )
                )

                // Top-Left Badge: Category & Prep Time
                Box(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopStart)
                        .background(FitPlanBlack.copy(alpha = 0.75f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${com.example.ui.FitPlanLocalization.getCategoryLabel(recipe.category).uppercase()} • ${recipe.prepTimeMinutes} ${stringResource(R.string.min_unit)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = FitPlanNeonLime,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }

                // Top-Right Difficulty Pill
                Box(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopEnd)
                        .background(FitPlanBlack.copy(alpha = 0.75f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = com.example.ui.FitPlanLocalization.getDifficultyLabel(recipe.difficulty),
                        style = MaterialTheme.typography.labelSmall,
                        color = FitPlanTextWhite,
                        fontWeight = FontWeight.Medium,
                        fontSize = 10.sp
                    )
                }
            }

            // Bottom Content
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = com.example.ui.FitPlanLocalization.getRecipeTitle(recipe),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = FitPlanTextWhite
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Calories
                        Box(
                            modifier = Modifier
                                .background(FitPlanSurface, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = null,
                                    tint = FitPlanMorningOrange,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "${recipe.calories} kcal",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = FitPlanTextWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Protein
                        Box(
                            modifier = Modifier
                                .background(FitPlanNeonLime.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${recipe.protein}g protein",
                                style = MaterialTheme.typography.labelSmall,
                                color = FitPlanNeonLime,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Button to open recipe details
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(FitPlanNeonLime.copy(alpha = 0.15f))
                            .clickable { onOpenDetails() }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stringResource(R.string.nutrition_recipe_action),
                                style = MaterialTheme.typography.labelSmall,
                                color = FitPlanNeonLime,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = FitPlanNeonLime,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Meal & Recipe Detail Modal Dialog showcasing Full HD Image,
 * macros breakdown, ingredients, preparation steps, Favorite and Add buttons.
 */
@Composable
fun MealDetailDialog(
    title: String,
    category: String,
    timeOrPrep: String,
    calories: Int,
    protein: Int,
    carbs: Int,
    fats: Int,
    fullAssetUrl: String,
    ingredients: List<String>,
    instructions: List<String>,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    primaryButtonText: String,
    primaryButtonColor: Color,
    primaryButtonTextColor: Color,
    onPrimaryAction: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .background(FitPlanCard)
                .border(1.dp, FitPlanCardBorder, RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Large Full HD Photo Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                        .background(FitPlanSurface)
                ) {
                    if (fullAssetUrl.isNotEmpty()) {
                        AsyncImage(
                            model = fullAssetUrl,
                            contentDescription = title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Contrast gradient
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Black.copy(alpha = 0.5f),
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.85f)
                                    )
                                )
                            )
                    )

                    // Top Floating Close Button
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .padding(14.dp)
                            .align(Alignment.TopEnd)
                            .size(38.dp)
                            .background(FitPlanBlack.copy(alpha = 0.7f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = FitPlanTextWhite,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Top Floating Favorite Button
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier
                            .padding(14.dp)
                            .align(Alignment.TopStart)
                            .size(38.dp)
                            .background(FitPlanBlack.copy(alpha = 0.7f), CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) FitPlanHiitRed else FitPlanTextWhite,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Bottom info on image
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(FitPlanCyan, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = category.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                color = FitPlanBlack,
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = FitPlanTextWhite
                        )
                        Text(
                            text = timeOrPrep,
                            style = MaterialTheme.typography.bodySmall,
                            color = FitPlanTextSecondary
                        )
                    }
                }

                // Body Section
                Column(modifier = Modifier.padding(18.dp)) {
                    // Nutritional Breakdown 4-Cards Grid
                    Text(
                        text = stringResource(R.string.nutritional_facts_title),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = FitPlanTextWhite
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        NutrientStatPill(stringResource(R.string.calories), "$calories", stringResource(R.string.kcal_unit), FitPlanMorningOrange, Modifier.weight(1f))
                        NutrientStatPill(stringResource(R.string.protein), "$protein", "g", FitPlanCyan, Modifier.weight(1f))
                        NutrientStatPill(stringResource(R.string.carbs), "$carbs", "g", FitPlanNeonLime, Modifier.weight(1f))
                        NutrientStatPill(stringResource(R.string.fats), "$fats", "g", FitPlanYogaLavender, Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Ingredients Section
                    if (ingredients.isNotEmpty()) {
                        Text(
                            text = stringResource(R.string.ingredients_title),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = FitPlanTextWhite
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(FitPlanSurface)
                                .padding(14.dp)
                        ) {
                            ingredients.forEach { item ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(FitPlanCyan)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = item,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = FitPlanTextWhite,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))
                    }

                    // Preparation Instructions Section
                    if (instructions.isNotEmpty()) {
                        Text(
                            text = stringResource(R.string.instructions_title),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = FitPlanTextWhite
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(FitPlanSurface)
                                .padding(14.dp)
                        ) {
                            instructions.forEachIndexed { index, step ->
                                Row(verticalAlignment = Alignment.Top) {
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .clip(CircleShape)
                                            .background(FitPlanCyan.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${index + 1}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = FitPlanCyan,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = step,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = FitPlanTextSecondary,
                                        fontSize = 13.sp,
                                        lineHeight = 18.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(22.dp))
                    }

                    // Bottom Action Button
                    Button(
                        onClick = onPrimaryAction,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryButtonColor,
                            contentColor = primaryButtonTextColor
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(
                            text = primaryButtonText,
                            fontWeight = FontWeight.Black,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NutrientStatPill(
    label: String,
    value: String,
    unit: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(FitPlanSurface)
            .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(vertical = 10.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = FitPlanTextSecondary,
                fontSize = 10.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = accentColor
            )
            Text(
                text = unit,
                style = MaterialTheme.typography.labelSmall,
                color = FitPlanTextMuted,
                fontSize = 9.sp
            )
        }
    }
}

@Composable
fun NutritionActionChip(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(FitPlanCard)
            .border(1.dp, FitPlanCardBorder, RoundedCornerShape(12.dp))
            .padding(vertical = 10.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = FitPlanCyan,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = FitPlanTextWhite,
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun NutritionTipCard(
    title: String,
    tip: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(FitPlanCard)
            .border(1.dp, FitPlanCardBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = FitPlanTextWhite
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = tip,
                    style = MaterialTheme.typography.bodyMedium,
                    color = FitPlanTextSecondary,
                    lineHeight = 18.sp,
                    fontSize = 12.sp
                )
            }
        }
    }
}

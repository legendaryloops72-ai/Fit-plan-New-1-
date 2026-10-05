package com.example.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.R
import com.example.model.*

/**
 * Centralized localization resolver for FITPLAN models and domain entities.
 * Ensures all dynamic titles, instructions, descriptions, and categories
 * are mapped to their respective localized Android string resources.
 */
object FitPlanLocalization {

    @Composable
    fun getExerciseName(exercise: ExerciseItem): String {
        return when (exercise.name.trim()) {
            "Push-ups" -> stringResource(R.string.ex_pushups_name)
            "Bodyweight Squats" -> stringResource(R.string.ex_squats_name)
            "Plank Hold", "Plank" -> stringResource(R.string.ex_plank_name)
            "Mountain Climbers" -> stringResource(R.string.ex_climbers_name)
            "Lunges" -> stringResource(R.string.ex_lunges_name)
            "Jumping Jacks" -> stringResource(R.string.ex_jacks_name)
            "Glute Bridges" -> stringResource(R.string.ex_bridges_name)
            "Sun Salutation A" -> stringResource(R.string.ex_sun_salutation_name)
            "Downward-Facing Dog" -> stringResource(R.string.ex_downward_dog_name)
            "Warrior II" -> stringResource(R.string.ex_warrior_ii_name)
            "Triangle Pose" -> stringResource(R.string.ex_triangle_name)
            "Tree Pose" -> stringResource(R.string.ex_tree_name)
            "Child's Pose" -> stringResource(R.string.ex_child_pose_name)
            "Neck & Shoulder Rolls" -> stringResource(R.string.ex_neck_rolls_name)
            "Cat-Cow Stretch" -> stringResource(R.string.ex_cat_cow_name)
            "Seated Hamstring Stretch", "Hamstring Stretch" -> stringResource(R.string.ex_hamstring_name)
            "Hip Flexor Stretch" -> stringResource(R.string.ex_hip_flexor_name)
            "Chest Opener" -> stringResource(R.string.ex_chest_opener_name)
            "Gentle Arm Swings" -> stringResource(R.string.ex_arm_swings_name)
            "High Knees March", "High Knee March" -> stringResource(R.string.ex_high_knees_name)
            "Side Lunges" -> stringResource(R.string.ex_side_lunges_name)
            "Torso Twists" -> stringResource(R.string.ex_torso_twists_name)
            "Calf Raises" -> stringResource(R.string.ex_calf_raises_name)
            "Burpees" -> stringResource(R.string.ex_burpees_name)
            "Speed Skaters" -> stringResource(R.string.ex_speed_skaters_name)
            "Jump Squats" -> stringResource(R.string.ex_jump_squats_name)
            "Sprint in Place" -> stringResource(R.string.ex_sprint_name)
            "Tuck Jumps" -> stringResource(R.string.ex_tuck_jumps_name)
            "Deep Belly Breathing" -> stringResource(R.string.ex_deep_breathing_name)
            "Reclining Butterfly" -> stringResource(R.string.ex_butterfly_name)
            "Supine Spinal Twist" -> stringResource(R.string.ex_spinal_twist_name)
            "Legs Up The Wall" -> stringResource(R.string.ex_legs_wall_name)
            "Savasana" -> stringResource(R.string.ex_savasana_name)
            "Plank with Knee Taps" -> stringResource(R.string.ex_plank_knee_taps_name)
            "Glute Bridges with Hold" -> stringResource(R.string.ex_glute_hold_name)
            "High Plank Shoulder Taps" -> stringResource(R.string.ex_plank_shoulder_taps_name)
            "World's Greatest Stretch" -> stringResource(R.string.ex_worlds_greatest_stretch_name)
            "Cat-Cow Spinal Flow" -> stringResource(R.string.ex_cat_cow_flow_name)
            "Pigeon Pose / Glute Opener" -> stringResource(R.string.ex_pigeon_pose_name)
            "Bird-Dog Balance" -> stringResource(R.string.ex_bird_dog_name)
            "Child's Pose with Side Reach" -> stringResource(R.string.ex_child_side_stretch_name)
            else -> exercise.name
        }
    }

    @Composable
    fun getExerciseInstructions(exercise: ExerciseItem): String {
        return when (exercise.name.trim()) {
            "Push-ups" -> stringResource(R.string.ex_pushups_desc)
            "Bodyweight Squats" -> stringResource(R.string.ex_squats_desc)
            "Plank Hold", "Plank" -> stringResource(R.string.ex_plank_desc)
            "Mountain Climbers" -> stringResource(R.string.ex_climbers_desc)
            "Lunges" -> stringResource(R.string.ex_lunges_desc)
            "Jumping Jacks" -> stringResource(R.string.ex_jacks_desc)
            "Glute Bridges" -> stringResource(R.string.ex_bridges_desc)
            "Sun Salutation A" -> stringResource(R.string.ex_sun_salutation_desc)
            "Downward-Facing Dog" -> stringResource(R.string.ex_downward_dog_desc)
            "Warrior II" -> stringResource(R.string.ex_warrior_ii_desc)
            "Triangle Pose" -> stringResource(R.string.ex_triangle_desc)
            "Tree Pose" -> stringResource(R.string.ex_tree_desc)
            "Child's Pose" -> stringResource(R.string.ex_child_pose_desc)
            "Neck & Shoulder Rolls" -> stringResource(R.string.ex_neck_rolls_desc)
            "Cat-Cow Stretch" -> stringResource(R.string.ex_cat_cow_desc)
            "Seated Hamstring Stretch", "Hamstring Stretch" -> stringResource(R.string.ex_hamstring_desc)
            "Hip Flexor Stretch" -> stringResource(R.string.ex_hip_flexor_desc)
            "Chest Opener" -> stringResource(R.string.ex_chest_opener_desc)
            "Gentle Arm Swings" -> stringResource(R.string.ex_arm_swings_desc)
            "High Knees March", "High Knee March" -> stringResource(R.string.ex_high_knees_desc)
            "Side Lunges" -> stringResource(R.string.ex_side_lunges_desc)
            "Torso Twists" -> stringResource(R.string.ex_torso_twists_desc)
            "Calf Raises" -> stringResource(R.string.ex_calf_raises_desc)
            "Burpees" -> stringResource(R.string.ex_burpees_desc)
            "Speed Skaters" -> stringResource(R.string.ex_speed_skaters_desc)
            "Jump Squats" -> stringResource(R.string.ex_jump_squats_desc)
            "Sprint in Place" -> stringResource(R.string.ex_sprint_desc)
            "Tuck Jumps" -> stringResource(R.string.ex_tuck_jumps_desc)
            "Deep Belly Breathing" -> stringResource(R.string.ex_deep_breathing_desc)
            "Reclining Butterfly" -> stringResource(R.string.ex_butterfly_desc)
            "Supine Spinal Twist" -> stringResource(R.string.ex_spinal_twist_desc)
            "Legs Up The Wall" -> stringResource(R.string.ex_legs_wall_desc)
            "Savasana" -> stringResource(R.string.ex_savasana_desc)
            "Plank with Knee Taps" -> stringResource(R.string.ex_plank_knee_taps_desc)
            "Glute Bridges with Hold" -> stringResource(R.string.ex_glute_hold_desc)
            "High Plank Shoulder Taps" -> stringResource(R.string.ex_plank_shoulder_taps_desc)
            "World's Greatest Stretch" -> stringResource(R.string.ex_worlds_greatest_stretch_desc)
            "Cat-Cow Spinal Flow" -> stringResource(R.string.ex_cat_cow_flow_desc)
            "Pigeon Pose / Glute Opener" -> stringResource(R.string.ex_pigeon_pose_desc)
            "Bird-Dog Balance" -> stringResource(R.string.ex_bird_dog_desc)
            "Child's Pose with Side Reach" -> stringResource(R.string.ex_child_side_stretch_desc)
            else -> exercise.instructions
        }
    }

    @Composable
    fun getWorkoutTitle(workout: WorkoutItem): String {
        return when (workout.title.trim()) {
            "Home Workout" -> stringResource(R.string.workout_home_workout)
            "Yoga" -> stringResource(R.string.workout_yoga)
            "Stretching" -> stringResource(R.string.workout_stretching)
            "Morning Move" -> stringResource(R.string.workout_morning_move)
            "HIIT Cardio" -> stringResource(R.string.workout_hiit_cardio)
            "Relaxing Yoga" -> stringResource(R.string.workout_relaxing_yoga)
            else -> workout.title
        }
    }

    @Composable
    fun getWorkoutSubtitle(workout: WorkoutItem): String {
        return when (workout.title.trim()) {
            "Home Workout" -> stringResource(R.string.workout_home_workout_sub)
            "Yoga" -> stringResource(R.string.workout_yoga_sub)
            "Stretching" -> stringResource(R.string.workout_stretching_sub)
            "Morning Move" -> stringResource(R.string.workout_morning_move_sub)
            "HIIT Cardio" -> stringResource(R.string.workout_hiit_cardio_sub)
            "Relaxing Yoga" -> stringResource(R.string.workout_relaxing_yoga_sub)
            else -> workout.description
        }
    }

    @Composable
    fun getWorkoutBadge(workout: WorkoutItem): String {
        return when (workout.title.trim()) {
            "Home Workout" -> stringResource(R.string.workout_home_workout_badge)
            "Yoga" -> stringResource(R.string.workout_yoga_badge)
            "Stretching" -> stringResource(R.string.workout_stretching_badge)
            "Morning Move" -> stringResource(R.string.workout_morning_move_badge)
            "HIIT Cardio" -> stringResource(R.string.workout_hiit_cardio_badge)
            "Relaxing Yoga" -> stringResource(R.string.workout_relaxing_yoga_badge)
            else -> "FITPLAN"
        }
    }

    @Composable
    fun getProgramTitle(program: WorkoutProgram): String {
        return when (program.title.trim()) {
            "Full Body Shred" -> stringResource(R.string.prog_shred_title)
            "Core Strength Mastery" -> stringResource(R.string.prog_core_title)
            "Mobility & Flow" -> stringResource(R.string.prog_mobility_title)
            else -> program.title
        }
    }

    @Composable
    fun getProgramSubtitle(program: WorkoutProgram): String {
        return when (program.title.trim()) {
            "Full Body Shred" -> stringResource(R.string.prog_shred_sub)
            "Core Strength Mastery" -> stringResource(R.string.prog_core_sub)
            "Mobility & Flow" -> stringResource(R.string.prog_mobility_sub)
            else -> program.subtitle
        }
    }

    @Composable
    fun getMealName(meal: MealItem): String {
        return when (meal.name.trim()) {
            "Oatmeal with Blueberries & Whey" -> stringResource(R.string.meal_oatmeal_name)
            "Grilled Chicken Breast with Quinoa" -> stringResource(R.string.meal_chicken_name)
            "Greek Yogurt & Almonds" -> stringResource(R.string.meal_yogurt_name)
            "Baked Salmon with Steamed Asparagus" -> stringResource(R.string.meal_baked_salmon_name)
            else -> meal.name
        }
    }

    @Composable
    fun getMealTypeName(type: String): String {
        return when (type.lowercase().trim()) {
            "breakfast" -> stringResource(R.string.meal_oatmeal_type)
            "lunch" -> stringResource(R.string.meal_chicken_type)
            "snack" -> stringResource(R.string.meal_yogurt_type)
            "dinner" -> stringResource(R.string.meal_baked_salmon_type)
            else -> type
        }
    }

    @Composable
    fun getRecipeTitle(recipe: RecipeItem): String {
        return when (recipe.title.trim()) {
            "Avocado & Egg Power Bowl" -> stringResource(R.string.recipe_avocado_bowl_name)
            "Lemon Grilled Salmon" -> stringResource(R.string.recipe_lemon_salmon_name)
            "Mediterranean Quinoa Salad" -> stringResource(R.string.recipe_quinoa_salad_name)
            "Protein Peanut Butter Bites" -> stringResource(R.string.recipe_energy_bites_name)
            else -> recipe.title
        }
    }

    @Composable
    fun getCategoryLabel(category: String): String {
        return when (category.lowercase().trim()) {
            "all", "all activities", "all workouts" -> stringResource(R.string.cat_all)
            "strength" -> stringResource(R.string.cat_strength)
            "cardio" -> stringResource(R.string.cat_cardio)
            "hiit" -> stringResource(R.string.cat_hiit)
            "yoga" -> stringResource(R.string.cat_yoga)
            "stretching" -> stringResource(R.string.cat_stretching)
            "outdoor" -> stringResource(R.string.tab_outdoor)
            "indoor" -> stringResource(R.string.tab_indoor)
            "sports" -> stringResource(R.string.tab_sports)
            "mind & body" -> stringResource(R.string.tab_mind_body)
            "today" -> stringResource(R.string.tab_today)
            "meal plan" -> stringResource(R.string.tab_meal_plan)
            "nutrition tips" -> stringResource(R.string.tab_nutrition_tips)
            "recipes" -> stringResource(R.string.tab_recipes)
            else -> category
        }
    }

    @Composable
    fun getMacroName(name: String): String {
        return when (name.lowercase().trim()) {
            "protein" -> stringResource(R.string.protein)
            "carbs" -> stringResource(R.string.carbs)
            "fats", "fat" -> stringResource(R.string.fats)
            "calories" -> stringResource(R.string.calories)
            else -> name
        }
    }

    @Composable
    fun getMealIngredients(meal: MealItem): List<String> {
        return when (meal.name.trim()) {
            "Oatmeal with Blueberries & Whey" -> listOf(
                stringResource(R.string.meal_oatmeal_ing_1),
                stringResource(R.string.meal_oatmeal_ing_2),
                stringResource(R.string.meal_oatmeal_ing_3),
                stringResource(R.string.meal_oatmeal_ing_4),
                stringResource(R.string.meal_oatmeal_ing_5),
                stringResource(R.string.meal_oatmeal_ing_6)
            )
            "Grilled Chicken Breast with Quinoa" -> listOf(
                stringResource(R.string.meal_chicken_ing_1),
                stringResource(R.string.meal_chicken_ing_2),
                stringResource(R.string.meal_chicken_ing_3),
                stringResource(R.string.meal_chicken_ing_4),
                stringResource(R.string.meal_chicken_ing_5),
                stringResource(R.string.meal_chicken_ing_6)
            )
            "Greek Yogurt & Almonds" -> listOf(
                stringResource(R.string.meal_yogurt_ing_1),
                stringResource(R.string.meal_yogurt_ing_2),
                stringResource(R.string.meal_yogurt_ing_3),
                stringResource(R.string.meal_yogurt_ing_4),
                stringResource(R.string.meal_yogurt_ing_5)
            )
            "Baked Salmon with Steamed Asparagus" -> listOf(
                stringResource(R.string.meal_baked_salmon_ing_1),
                stringResource(R.string.meal_baked_salmon_ing_2),
                stringResource(R.string.meal_baked_salmon_ing_3),
                stringResource(R.string.meal_baked_salmon_ing_4),
                stringResource(R.string.meal_baked_salmon_ing_5)
            )
            else -> meal.ingredients
        }
    }

    @Composable
    fun getMealInstructions(meal: MealItem): List<String> {
        return when (meal.name.trim()) {
            "Oatmeal with Blueberries & Whey" -> listOf(
                stringResource(R.string.meal_oatmeal_step_1),
                stringResource(R.string.meal_oatmeal_step_2),
                stringResource(R.string.meal_oatmeal_step_3)
            )
            "Grilled Chicken Breast with Quinoa" -> listOf(
                stringResource(R.string.meal_chicken_step_1),
                stringResource(R.string.meal_chicken_step_2),
                stringResource(R.string.meal_chicken_step_3)
            )
            "Greek Yogurt & Almonds" -> listOf(
                stringResource(R.string.meal_yogurt_step_1),
                stringResource(R.string.meal_yogurt_step_2),
                stringResource(R.string.meal_yogurt_step_3)
            )
            "Baked Salmon with Steamed Asparagus" -> listOf(
                stringResource(R.string.meal_baked_salmon_step_1),
                stringResource(R.string.meal_baked_salmon_step_2),
                stringResource(R.string.meal_baked_salmon_step_3)
            )
            else -> meal.instructions
        }
    }

    @Composable
    fun getRecipeIngredients(recipe: RecipeItem): List<String> {
        return when (recipe.title.trim()) {
            "Avocado & Egg Power Bowl" -> listOf(
                stringResource(R.string.recipe_avocado_bowl_ing_1),
                stringResource(R.string.recipe_avocado_bowl_ing_2),
                stringResource(R.string.recipe_avocado_bowl_ing_3),
                stringResource(R.string.recipe_avocado_bowl_ing_4),
                stringResource(R.string.recipe_avocado_bowl_ing_5)
            )
            "Lemon Grilled Salmon" -> listOf(
                stringResource(R.string.recipe_lemon_salmon_ing_1),
                stringResource(R.string.recipe_lemon_salmon_ing_2),
                stringResource(R.string.recipe_lemon_salmon_ing_3),
                stringResource(R.string.recipe_lemon_salmon_ing_4),
                stringResource(R.string.recipe_lemon_salmon_ing_5)
            )
            "Mediterranean Quinoa Salad" -> listOf(
                stringResource(R.string.recipe_quinoa_salad_ing_1),
                stringResource(R.string.recipe_quinoa_salad_ing_2),
                stringResource(R.string.recipe_quinoa_salad_ing_3),
                stringResource(R.string.recipe_quinoa_salad_ing_4),
                stringResource(R.string.recipe_quinoa_salad_ing_5),
                stringResource(R.string.recipe_quinoa_salad_ing_6)
            )
            "Protein Peanut Butter Bites" -> listOf(
                stringResource(R.string.recipe_energy_bites_ing_1),
                stringResource(R.string.recipe_energy_bites_ing_2),
                stringResource(R.string.recipe_energy_bites_ing_3),
                stringResource(R.string.recipe_energy_bites_ing_4),
                stringResource(R.string.recipe_energy_bites_ing_5),
                stringResource(R.string.recipe_energy_bites_ing_6)
            )
            else -> recipe.ingredients
        }
    }

    @Composable
    fun getRecipeInstructions(recipe: RecipeItem): List<String> {
        return when (recipe.title.trim()) {
            "Avocado & Egg Power Bowl" -> listOf(
                stringResource(R.string.recipe_avocado_bowl_step_1),
                stringResource(R.string.recipe_avocado_bowl_step_2),
                stringResource(R.string.recipe_avocado_bowl_step_3)
            )
            "Lemon Grilled Salmon" -> listOf(
                stringResource(R.string.recipe_lemon_salmon_step_1),
                stringResource(R.string.recipe_lemon_salmon_step_2),
                stringResource(R.string.recipe_lemon_salmon_step_3)
            )
            "Mediterranean Quinoa Salad" -> listOf(
                stringResource(R.string.recipe_quinoa_salad_step_1),
                stringResource(R.string.recipe_quinoa_salad_step_2),
                stringResource(R.string.recipe_quinoa_salad_step_3)
            )
            "Protein Peanut Butter Bites" -> listOf(
                stringResource(R.string.recipe_energy_bites_step_1),
                stringResource(R.string.recipe_energy_bites_step_2),
                stringResource(R.string.recipe_energy_bites_step_3)
            )
            else -> recipe.instructions
        }
    }

    @Composable
    fun getDifficultyLabel(diff: String): String {
        return when (diff.lowercase().trim()) {
            "all levels" -> stringResource(R.string.all_levels)
            "beginner", "easy" -> stringResource(R.string.beginner)
            "intermediate", "medium" -> stringResource(R.string.intermediate)
            "advanced", "quick" -> stringResource(R.string.advanced)
            else -> diff
        }
    }
}

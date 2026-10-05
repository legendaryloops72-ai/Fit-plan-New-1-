package com.example.data

object ExerciseAssetRegistry {

    private val nameToAssetMap = mapOf(
        "push-ups" to "exercises/home_workout/push_ups.webp",
        "pushups" to "exercises/home_workout/push_ups.webp",
        "push up" to "exercises/home_workout/push_ups.webp",
        "bodyweight squats" to "exercises/home_workout/bodyweight_squats.webp",
        "squats" to "exercises/home_workout/bodyweight_squats.webp",
        "squat" to "exercises/home_workout/bodyweight_squats.webp",
        "plank hold" to "exercises/home_workout/plank.webp",
        "plank" to "exercises/home_workout/plank.webp",
        "mountain climbers" to "exercises/home_workout/mountain_climbers.webp",
        "mountain climber" to "exercises/home_workout/mountain_climbers.webp",
        "lunges" to "exercises/home_workout/lunges.webp",
        "lunge" to "exercises/home_workout/lunges.webp",
        "jumping jacks" to "exercises/home_workout/jumping_jacks.webp",
        "jumping jack" to "exercises/home_workout/jumping_jacks.webp",
        "glute bridges" to "exercises/home_workout/glute_bridges.webp",
        "glute bridge" to "exercises/home_workout/glute_bridges.webp",
        "glute bridges with hold" to "exercises/home_workout/glute_bridges.webp",
        "glute bridge hold" to "exercises/home_workout/glute_bridges.webp",

        "sun salutation a" to "exercises/yoga/sun_salutation_a.webp",
        "sun salutation" to "exercises/yoga/sun_salutation_a.webp",
        "surya namaskar" to "exercises/yoga/sun_salutation_a.webp",
        "downward-facing dog" to "exercises/yoga/downward_facing_dog.webp",
        "downward facing dog" to "exercises/yoga/downward_facing_dog.webp",
        "downward dog" to "exercises/yoga/downward_facing_dog.webp",
        "adho mukha svanasana" to "exercises/yoga/downward_facing_dog.webp",
        "warrior ii" to "exercises/yoga/warrior_ii.webp",
        "warrior 2" to "exercises/yoga/warrior_ii.webp",
        "virabhadrasana ii" to "exercises/yoga/warrior_ii.webp",
        "triangle pose" to "exercises/yoga/triangle_pose.webp",
        "triangle" to "exercises/yoga/triangle_pose.webp",
        "trikonasana" to "exercises/yoga/triangle_pose.webp",
        "tree pose" to "exercises/yoga/tree_pose.webp",
        "vrikshasana" to "exercises/yoga/tree_pose.webp",
        "child's pose" to "exercises/yoga/childs_pose.webp",
        "childs pose" to "exercises/yoga/childs_pose.webp",
        "child's pose with side reach" to "exercises/yoga/childs_pose.webp",
        "child's pose with side stretch" to "exercises/yoga/childs_pose.webp",
        "balasana" to "exercises/yoga/childs_pose.webp",

        "neck & shoulder rolls" to "exercises/stretching/neck_shoulder_rolls.webp",
        "neck and shoulder rolls" to "exercises/stretching/neck_shoulder_rolls.webp",
        "neck rolls" to "exercises/stretching/neck_shoulder_rolls.webp",
        "cat-cow stretch" to "exercises/stretching/cat_cow.webp",
        "cat-cow spinal flow" to "exercises/stretching/cat_cow.webp",
        "cat-cow" to "exercises/stretching/cat_cow.webp",
        "cat cow" to "exercises/stretching/cat_cow.webp",
        "seated hamstring stretch" to "exercises/stretching/hamstring_stretch.webp",
        "hamstring stretch" to "exercises/stretching/hamstring_stretch.webp",
        "hip flexor stretch" to "exercises/stretching/hip_flexor_stretch.webp",
        "chest opener" to "exercises/stretching/chest_opener.webp",

        "gentle arm swings" to "exercises/morning_move/gentle_arm_swings.webp",
        "arm swings" to "exercises/morning_move/gentle_arm_swings.webp",
        "high knees march" to "exercises/morning_move/high_knee_march.webp",
        "high knee march" to "exercises/morning_move/high_knee_march.webp",
        "high knees" to "exercises/morning_move/high_knee_march.webp",
        "side lunges" to "exercises/morning_move/side_lunges.webp",
        "torso twists" to "exercises/morning_move/torso_twists.webp",
        "calf raises" to "exercises/morning_move/calf_raises.webp",

        "burpees" to "exercises/hiit/burpees.webp",
        "burpee" to "exercises/hiit/burpees.webp",
        "speed skaters" to "exercises/hiit/speed_skaters.webp",
        "speed skater" to "exercises/hiit/speed_skaters.webp",
        "jump squats" to "exercises/hiit/jump_squats.webp",
        "jump squat" to "exercises/hiit/jump_squats.webp",
        "sprint in place" to "exercises/hiit/sprint_in_place.webp",
        "sprint" to "exercises/hiit/sprint_in_place.webp",
        "tuck jumps" to "exercises/hiit/tuck_jumps.webp",
        "tuck jump" to "exercises/hiit/tuck_jumps.webp",

        "deep belly breathing" to "exercises/relaxing_yoga/deep_belly_breathing.webp",
        "belly breathing" to "exercises/relaxing_yoga/deep_belly_breathing.webp",
        "reclining butterfly" to "exercises/relaxing_yoga/reclining_butterfly.webp",
        "supine spinal twist" to "exercises/relaxing_yoga/supine_spinal_twist.webp",
        "legs up the wall" to "exercises/relaxing_yoga/legs_up_the_wall.webp",
        "savasana" to "exercises/relaxing_yoga/savasana.webp",

        "plank with knee taps" to "exercises/gemini/plank_knee_taps.webp",
        "plank knee taps" to "exercises/gemini/plank_knee_taps.webp",
        "high plank shoulder taps" to "exercises/gemini/plank_shoulder_taps.webp",
        "plank shoulder taps" to "exercises/gemini/plank_shoulder_taps.webp",
        "world's greatest stretch" to "exercises/gemini/worlds_greatest_stretch.webp",
        "pigeon pose / glute opener" to "exercises/gemini/pigeon_pose.webp",
        "pigeon pose" to "exercises/gemini/pigeon_pose.webp",
        "bird-dog balance" to "exercises/gemini/bird_dog.webp",
        "bird-dog" to "exercises/gemini/bird_dog.webp",
        "bird dog" to "exercises/gemini/bird_dog.webp"
    )

    fun getAssetForName(name: String): String {
        val clean = name.trim().lowercase()
        nameToAssetMap[clean]?.let { return it }
        for ((key, path) in nameToAssetMap) {
            if (clean.contains(key) || key.contains(clean)) {
                return path
            }
        }
        return "exercises/home_workout/push_ups.webp"
    }

    fun getAssetUrl(assetPath: String): String =
        if (assetPath.isNotEmpty()) "file:///android_asset/$assetPath" else ""

    fun getAssetUrlForName(name: String): String =
        getAssetUrl(getAssetForName(name))
}

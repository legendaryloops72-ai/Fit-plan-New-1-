package com.example.data.gemini

import android.util.Log
import com.example.BuildConfig
import com.example.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiFitnessAdvisor {

    private val apiService = GeminiRetrofitClient.service
    private val moshi = GeminiRetrofitClient.jsonMoshi

    /**
     * Generates personalized daily health tips and workout recommendations
     * based on user input data stored in the app.
     */
    suspend fun getDailyRecommendations(
        userProfile: UserProfile,
        todaySteps: Int,
        todayCalories: Int,
        todayMinutes: Int,
        waterGlasses: Int,
        meals: List<MealItem>,
        activitySessions: List<ActivitySession>
    ): GeminiHealthRecommendations = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        val eatenMeals = meals.filter { it.isEaten }
        val eatenCalories = eatenMeals.sumOf { it.calories }
        val eatenProtein = eatenMeals.sumOf { it.protein }
        val eatenCarbs = eatenMeals.sumOf { it.carbs }
        val eatenFats = eatenMeals.sumOf { it.fats }

        val heightM = userProfile.heightCm / 100f
        val bmi = if (heightM > 0) String.format("%.1f", userProfile.weightKg / (heightM * heightM)) else "23.0"

        val recentSession = activitySessions.firstOrNull()?.let {
            "${it.type.name} for ${it.durationMinutes} mins (${it.distanceKm} km, burned ${it.caloriesBurned} kcal)"
        } ?: "No recent outdoor workouts logged"

        val langCode = userProfile.selectedLanguage
        val langInstruction = when (langCode) {
            "ar" -> "CRITICAL REQUIREMENT: The user selected Arabic (العربية). You MUST provide ALL text values (headline, hydrationTip, nutritionTip, movementTip, mindsetQuote, workoutTitle, workoutCategory, workoutDifficulty, workoutRationale, exercise names, instructions) in clear, professional Arabic. Do NOT output English text."
            "es" -> "CRITICAL REQUIREMENT: The user selected Spanish (Español). You MUST provide ALL text values (headline, hydrationTip, nutritionTip, movementTip, mindsetQuote, workoutTitle, workoutCategory, workoutDifficulty, workoutRationale, exercise names, instructions) in natural, professional Spanish. Do NOT output English text."
            else -> "The user selected English. Provide all text in English."
        }

        val prompt = """
            You are FITPLAN's elite AI Fitness & Health Coach. Analyze this user's profile and real-time stored app data:
            - Language: $langCode ($langInstruction)
            - User Profile: ${userProfile.fullName}, Level: ${userProfile.level}, Weight: ${userProfile.weightKg} kg (Target: ${userProfile.targetWeightKg} kg), Height: ${userProfile.heightCm} cm (BMI: $bmi)
            - Goals: Step Goal: ${userProfile.dailyStepGoal}, Calorie Goal: ${userProfile.dailyCalorieGoal} kcal, Active Minutes Goal: ${userProfile.dailyActiveMinutesGoal} min
            - Today's Live Stats: Steps: $todaySteps / ${userProfile.dailyStepGoal}, Active Calories Burned: $todayCalories kcal, Active Time: $todayMinutes min
            - Water Intake: $waterGlasses / 8 glasses (${waterGlasses * 250} ml / 2000 ml)
            - Meals Eaten Today: ${eatenMeals.size} logged (${eatenMeals.joinToString { it.name }}). Total macros eaten: $eatenCalories kcal, $eatenProtein g protein, $eatenCarbs g carbs, $eatenFats g fats.
            - Recent Activity: $recentSession

            $langInstruction

            Generate a personalized daily health tip & workout recommendation in JSON format matching this exact schema:
            {
              "headline": "Short energizing headline tailored to today's progress",
              "hydrationTip": "Actionable advice based on logged $waterGlasses glasses (${waterGlasses * 250}ml)",
              "nutritionTip": "Actionable macro/meal tip based on current ${eatenProtein}g protein & ${eatenCalories} kcal logged",
              "movementTip": "Tailored movement/recovery advice based on $todaySteps steps & $todayMinutes active minutes",
              "mindsetQuote": "Punchy motivational athlete quote",
              "workoutTitle": "Recommended Workout Title (e.g. Core & Metabolic Burn)",
              "workoutCategory": "Full Body / Cardio / Strength / HIIT / Yoga / Stretching",
              "workoutDurationMinutes": 20,
              "workoutDifficulty": "Beginner / Intermediate / Advanced",
              "workoutCalories": 240,
              "workoutRationale": "Clear 2-sentence rationale explaining WHY this workout fits today's data (step deficit, muscle recovery, calorie goals)",
              "exercises": [
                {
                  "name": "Exercise Name",
                  "sets": 3,
                  "reps": "12 reps",
                  "durationSec": 45,
                  "restSec": 20,
                  "instructions": "Key form instruction"
                }
              ]
            }
            Provide 4 to 6 specific exercises suitable for the workout.
            Return only valid JSON.
        """.trimIndent()

        if (apiKey.isNotEmpty() && !apiKey.contains("MY_GEMINI_API_KEY", ignoreCase = true)) {
            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(parts = listOf(GeminiPart(text = prompt)))
                ),
                generationConfig = GeminiGenerationConfig(
                    temperature = 0.7f,
                    responseMimeType = "application/json"
                ),
                systemInstruction = GeminiContent(
                    parts = listOf(
                        GeminiPart(text = "You are FITPLAN AI, a certified sports scientist and nutritionist. Always return strictly valid JSON matching the schema provided.")
                    )
                )
            )

            // Multi-model resilience: try primary model first, fallback to alternate high-availability models if overloaded (HTTP 503)
            val candidateModels = listOf("gemini-3.5-flash", "gemini-flash-latest", "gemini-2.5-flash", "gemini-3.1-flash-lite-preview")
            for (model in candidateModels) {
                try {
                    val response = apiService.generateContentWithModel(model, apiKey, request)
                    val responseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text

                    if (!responseText.isNullOrBlank()) {
                        val cleanJson = cleanJsonResponse(responseText)
                        val adapter = moshi.adapter(GeminiHealthRecommendations::class.java)
                        val parsed = adapter.fromJson(cleanJson)
                        if (parsed != null) {
                            return@withContext parsed
                        }
                    }
                } catch (e: retrofit2.HttpException) {
                    Log.w("GeminiFitnessAdvisor", "Gemini model $model returned HTTP ${e.code()}. Trying fallback...")
                } catch (e: Exception) {
                    Log.w("GeminiFitnessAdvisor", "Gemini model $model issue: ${e.message}")
                }
            }
        }

        // Context-aware dynamic fallback generated directly from the user's live data
        buildDataDrivenFallback(
            userProfile = userProfile,
            todaySteps = todaySteps,
            todayCalories = todayCalories,
            todayMinutes = todayMinutes,
            waterGlasses = waterGlasses,
            eatenProtein = eatenProtein,
            eatenCalories = eatenCalories
        )
    }

    /**
     * Interactive AI Coach Q&A query using Gemini
     */
    suspend fun askCoach(
        userQuestion: String,
        userProfile: UserProfile,
        todaySteps: Int,
        todayCalories: Int,
        waterGlasses: Int
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        val langCode = userProfile.selectedLanguage
        val langDirective = when (langCode) {
            "ar" -> "CRITICAL REQUIREMENT: You MUST respond strictly in natural, professional, encouraging Arabic (العربية). Do NOT write in English."
            "es" -> "CRITICAL REQUIREMENT: You MUST respond strictly in natural, professional, encouraging Spanish (Español). Do NOT write in English."
            else -> "Respond in English."
        }

        val prompt = """
            User Profile: ${userProfile.fullName}, Weight: ${userProfile.weightKg}kg, Target: ${userProfile.targetWeightKg}kg, Level: ${userProfile.level}
            Current Today Stats: $todaySteps steps, $todayCalories kcal burned, $waterGlasses glasses water.
            Language: $langCode
            User Question: "$userQuestion"

            $langDirective
            Respond concisely in 2-3 friendly, practical, and highly motivating sentences as an expert fitness and nutrition coach.
        """.trimIndent()

        if (apiKey.isNotEmpty() && !apiKey.contains("MY_GEMINI_API_KEY", ignoreCase = true)) {
            val systemPrompt = when (langCode) {
                "ar" -> "أنت مدرب اللياقة والصحة الذكي لتطبيق FITPLAN. قدم نصائح علمية وعملية ومشجعة وموجزة باللغة العربية حصراً."
                "es" -> "Eres el Entrenador Personal IA de FITPLAN. Proporciona consejos directos, empáticos, científicamente sólidos y motivadores en español únicamente."
                else -> "You are FITPLAN's AI Personal Coach. Give direct, empathetic, and scientifically sound advice."
            }

            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(parts = listOf(GeminiPart(text = prompt)))
                ),
                generationConfig = GeminiGenerationConfig(
                    temperature = 0.7f,
                    responseMimeType = null
                ),
                systemInstruction = GeminiContent(
                    parts = listOf(
                        GeminiPart(text = systemPrompt)
                    )
                )
            )

            val candidateModels = listOf("gemini-3.5-flash", "gemini-flash-latest", "gemini-2.5-flash", "gemini-3.1-flash-lite-preview")
            for (model in candidateModels) {
                try {
                    val response = apiService.generateContentWithModel(model, apiKey, request)
                    val answer = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    if (!answer.isNullOrBlank()) {
                        return@withContext answer.trim()
                    }
                } catch (e: retrofit2.HttpException) {
                    Log.w("GeminiFitnessAdvisor", "Coach model $model returned HTTP ${e.code()}. Trying fallback...")
                } catch (e: Exception) {
                    Log.w("GeminiFitnessAdvisor", "Coach model $model issue: ${e.message}")
                }
            }
        }

        // Fallback intelligent responses for common questions
        generateFallbackCoachAnswer(userQuestion, todaySteps, waterGlasses, userProfile)
    }

    private fun cleanJsonResponse(raw: String): String {
        var clean = raw.trim()
        if (clean.startsWith("```json")) {
            clean = clean.removePrefix("```json").trim()
        } else if (clean.startsWith("```")) {
            clean = clean.removePrefix("```").trim()
        }
        if (clean.endsWith("```")) {
            clean = clean.removeSuffix("```").trim()
        }
        return clean
    }

    private fun buildDataDrivenFallback(
        userProfile: UserProfile,
        todaySteps: Int,
        todayCalories: Int,
        todayMinutes: Int,
        waterGlasses: Int,
        eatenProtein: Int,
        eatenCalories: Int
    ): GeminiHealthRecommendations {
        val lang = userProfile.selectedLanguage
        val remainingSteps = maxOf(0, userProfile.dailyStepGoal - todaySteps)
        val waterRemaining = maxOf(0, 8 - waterGlasses)

        val hydrationTip = when (lang) {
            "ar" -> if (waterGlasses >= 8) {
                "ترطيب ممتاز! لقد وصلت إلى هدف ${waterGlasses * 250} مل. اشرب إلكتروليتات بعد التمرين للحفاظ على مرونة العضلات."
            } else {
                "أنت عند ${waterGlasses * 250} مل (${waterGlasses}/8 أكواب). اشرب $waterRemaining أكواب إضافية (${waterRemaining * 250} مل) لضمان أفضل معدل حرق."
            }
            "es" -> if (waterGlasses >= 8) {
                "¡Excelente hidratación! Has alcanzado tu meta de ${waterGlasses * 250} ml. Toma electrolitos tras entrenar para cuidar tus músculos."
            } else {
                "Llevas ${waterGlasses * 250} ml (${waterGlasses}/8 vasos). Bebe $waterRemaining vasos más (${waterRemaining * 250} ml) para optimizar tu metabolismo."
            }
            else -> if (waterGlasses >= 8) {
                "Outstanding hydration! You've reached your ${waterGlasses * 250} ml goal. Sip electrolytes post-workout to sustain muscle elasticity."
            } else {
                "You are at ${waterGlasses * 250} ml (${waterGlasses}/8 glasses). Drink $waterRemaining more glasses (${waterRemaining * 250} ml) to ensure optimal metabolic rate."
            }
        }

        val nutritionTip = when (lang) {
            "ar" -> if (eatenProtein < 100) {
                "سجلت حتى الآن ${eatenProtein} غرام من البروتين. أضف زبادي يوناني أو سلمون أو مخفوق بروتين لتغذية عضلاتك."
            } else {
                "استهلاك ممتاز للبروتين مع ${eatenProtein} غرام مسجلة! حافظ على التوازن مع الخضروات الورقية والكربوهيدرات المعقدة."
            }
            "es" -> if (eatenProtein < 100) {
                "Has registrado ${eatenProtein}g de proteína. Añade yogur griego, salmón o un batido de proteínas para recuperar masa muscular."
            } else {
                "¡Gran ritmo proteico con ${eatenProtein}g registrados! Mantén el equilibrio con verduras ricas en fibra y carbohidratos complejos."
            }
            else -> if (eatenProtein < 100) {
                "You have logged ${eatenProtein}g of protein so far. Add Greek yogurt, salmon, or a protein shake to fuel muscle synthesis."
            } else {
                "Great protein pacing with ${eatenProtein}g logged! Maintain balance with fiber-rich greens and complex carbs for dinner."
            }
        }

        val movementTip = when (lang) {
            "ar" -> if (remainingSteps > 3000) {
                "متبقي لك $remainingSteps خطوة للوصول لهدف ${userProfile.dailyStepGoal}. المشي السريع لمدة 20 دقيقة مساءً سيكمل الهدف بنجاح."
            } else {
                "أداء رائع! مع $todaySteps خطوة و $todayMinutes دقيقة نشطة، نشاطك البدني والقلبي في أفضل حالاته اليوم."
            }
            "es" -> if (remainingSteps > 3000) {
                "Te faltan $remainingSteps pasos para tu meta de ${userProfile.dailyStepGoal}. Una caminata activa de 20 minutos por la tarde cerrará el día perfecto."
            } else {
                "¡Superando tu meta de pasos! Con $todaySteps pasos y $todayMinutes minutos activos, tu salud cardiovascular va genial hoy."
            }
            else -> if (remainingSteps > 3000) {
                "You have $remainingSteps steps left to reach your ${userProfile.dailyStepGoal} goal. A brisk 20-minute evening walk will close the loop nicely."
            } else {
                "Crushing your step target! With $todaySteps steps and $todayMinutes active minutes, your cardiovascular volume is on point today."
            }
        }

        val mindsetQuote = when (lang) {
            "ar" -> "جسمك يستطيع تحمل أي شيء تقريباً؛ عقلك فقط هو ما تحتاجه لإقناعه."
            "es" -> "Tu cuerpo puede soportar casi todo; es a tu mente a la que tienes que convencer."
            else -> "Your body can stand almost anything; it's your mind that you have to convince."
        }

        val headline = when (lang) {
            "ar" -> "توصيات ذكية مخصصة لـ ${userProfile.fullName}"
            "es" -> "Recomendaciones personalizadas para ${userProfile.fullName}"
            else -> "Personalized Insights for ${userProfile.fullName}"
        }

        val (workoutTitle, category, duration, calories, rationale, exercises) = if (todayMinutes < 30) {
            when (lang) {
                "ar" -> Tuple6(
                    "حارق السعرات وتمارين الكور بالذكاء الاصطناعي",
                    "HIIT",
                    25,
                    290,
                    "بناءً على $todayMinutes دقيقة نشطة وهدف ${userProfile.targetWeightKg} كجم، هذا التمرين الفاصل عالي الكثافة يزيد معدل حرق السعرات لما بعد التمرين.",
                    listOf(
                        GeminiExerciseItem("Jump Squats", 3, "15 تكرار", 45, 20, "اهبط بخفة مع الحفاظ على الصدر مرفوعاً."),
                        GeminiExerciseItem("Plank with Knee Taps", 3, "20 إجمالي", 40, 20, "حافظ على ثبات الوركين وشد البطن."),
                        GeminiExerciseItem("Mountain Climbers", 3, "30 ثانية", 30, 15, "ادفع الركبتين بسرعة نحو الصدر."),
                        GeminiExerciseItem("Glute Bridges with Hold", 3, "15 تكرار", 45, 20, "اضغط العضلات بقوة لمدة ثانيتين في القمة."),
                        GeminiExerciseItem("High Plank Shoulder Taps", 3, "16 تكرار", 40, 20, "قلل من دوران الحوض.")
                    )
                )
                "es" -> Tuple6(
                    "Quemador Metabólico HIIT y Core IA",
                    "HIIT",
                    25,
                    290,
                    "Basado en tus $todayMinutes minutos activos y tu objetivo de ${userProfile.targetWeightKg}kg, esta rutina maximiza la quema calórica tras el ejercicio.",
                    listOf(
                        GeminiExerciseItem("Jump Squats", 3, "15 reps", 45, 20, "Aterriza suavemente con el pecho erguido."),
                        GeminiExerciseItem("Plank with Knee Taps", 3, "20 en total", 40, 20, "Mantén la cadera firme y activa el abdomen."),
                        GeminiExerciseItem("Mountain Climbers", 3, "30 seg", 30, 15, "Lleva las rodillas explosivamente al pecho."),
                        GeminiExerciseItem("Glute Bridges with Hold", 3, "15 reps", 45, 20, "Aprieta glúteos arriba durante 2 segundos."),
                        GeminiExerciseItem("High Plank Shoulder Taps", 3, "16 reps", 40, 20, "Minimiza la rotación de cadera.")
                    )
                )
                else -> Tuple6(
                    "AI Metabolic HIIT & Core Burner",
                    "HIIT",
                    25,
                    290,
                    "Based on your $todayMinutes active minutes and ${userProfile.targetWeightKg}kg goal, this high-efficiency interval workout maximizes EPOC calorie afterburn.",
                    listOf(
                        GeminiExerciseItem("Jump Squats", 3, "15 reps", 45, 20, "Land softly with chest proud."),
                        GeminiExerciseItem("Plank with Knee Taps", 3, "20 total", 40, 20, "Keep hips stable and brace core."),
                        GeminiExerciseItem("Mountain Climbers", 3, "30 sec", 30, 15, "Drive knees explosively toward chest."),
                        GeminiExerciseItem("Glute Bridges with Hold", 3, "15 reps", 45, 20, "Squeeze glutes for 2 seconds at peak."),
                        GeminiExerciseItem("High Plank Shoulder Taps", 3, "16 reps", 40, 20, "Minimize hip rotation.")
                    )
                )
            }
        } else {
            when (lang) {
                "ar" -> Tuple6(
                    "جلسة الاستشفاء والمرونة بالذكاء الاصطناعي",
                    "Stretching",
                    20,
                    140,
                    "سجلت نشاطاً رائعاً اليوم ($todayMinutes دقيقة، $todayCalories سعرة). هذا الروتين يخفف الضغط عن المفاصل ويعزز التعافي العميق.",
                    listOf(
                        GeminiExerciseItem("World's Greatest Stretch", 2, "6 لكل جانب", 60, 20, "تقدم للأمام في وضعية الاندفاع وافتح صدرك للأعلى."),
                        GeminiExerciseItem("Cat-Cow Spinal Flow", 3, "10 تكرار", 45, 15, "نسق التنفس مع تمدد وتقوس الظهر."),
                        GeminiExerciseItem("Pigeon Pose / Glute Opener", 2, "45 ثانية لكل جانب", 90, 15, "تنفس بعمق واسترخِ داخل الإطالة."),
                        GeminiExerciseItem("Bird-Dog Balance", 3, "10 لكل جانب", 45, 15, "مد الذراع والساق المعاكسة بثبات."),
                        GeminiExerciseItem("Child's Pose with Side Reach", 2, "60 ثانية", 60, 10, "أطل عضلات الظهر وأسفل العمود الفقري.")
                    )
                )
                "es" -> Tuple6(
                    "Movilidad Restaurativa y Core Flow IA",
                    "Stretching",
                    20,
                    140,
                    "Has registrado una excelente actividad hoy ($todayMinutes min, $todayCalories kcal). Esta rutina descomprime las articulaciones y favorece la recuperación.",
                    listOf(
                        GeminiExerciseItem("World's Greatest Stretch", 2, "6 por lado", 60, 20, "Paso en zancada, rota el pecho hacia arriba."),
                        GeminiExerciseItem("Cat-Cow Spinal Flow", 3, "10 reps", 45, 15, "Sincroniza la respiración con la extensión de espalda."),
                        GeminiExerciseItem("Pigeon Pose / Glute Opener", 2, "45 seg por lado", 90, 15, "Respira hondo y relájate en la postura."),
                        GeminiExerciseItem("Bird-Dog Balance", 3, "10 por lado", 45, 15, "Extiende brazo y pierna opuestos firmemente."),
                        GeminiExerciseItem("Child's Pose with Side Reach", 2, "60 seg", 60, 10, "Alarga los dorsales y la zona lumbar.")
                    )
                )
                else -> Tuple6(
                    "AI Restorative Mobility & Core Flow",
                    "Stretching",
                    20,
                    140,
                    "You've logged solid activity today ($todayMinutes mins, $todayCalories kcal). This routine decompresses joints, opens tight hip flexors, and aids deep recovery.",
                    listOf(
                        GeminiExerciseItem("World's Greatest Stretch", 2, "6 each side", 60, 20, "Step into lunge, rotate chest upward."),
                        GeminiExerciseItem("Cat-Cow Spinal Flow", 3, "10 reps", 45, 15, "Synchronize breath with spinal extension."),
                        GeminiExerciseItem("Pigeon Pose / Glute Opener", 2, "45 sec each", 90, 15, "Breathe deeply and relax into the stretch."),
                        GeminiExerciseItem("Bird-Dog Balance", 3, "10 each side", 45, 15, "Extend opposite arm and leg steadily."),
                        GeminiExerciseItem("Child's Pose with Side Reach", 2, "60 sec", 60, 10, "Lengthen lats and lower back.")
                    )
                )
            }
        }

        return GeminiHealthRecommendations(
            headline = headline,
            hydrationTip = hydrationTip,
            nutritionTip = nutritionTip,
            movementTip = movementTip,
            mindsetQuote = mindsetQuote,
            workoutTitle = workoutTitle,
            workoutCategory = category,
            workoutDurationMinutes = duration,
            workoutDifficulty = userProfile.level,
            workoutCalories = calories,
            workoutRationale = rationale,
            exercises = exercises
        )
    }

    private fun generateFallbackCoachAnswer(
        question: String,
        steps: Int,
        water: Int,
        profile: UserProfile
    ): String {
        val q = question.lowercase()
        val lang = profile.selectedLanguage

        return when (lang) {
            "ar" -> when {
                q.contains("ماء") || q.contains("شرب") || q.contains("سوائل") || q.contains("water") || q.contains("hydrate") ->
                    "الترطيب أساسي للنشاط! لقد شربت $water أكواب اليوم. احرص على الوصول إلى 8 أكواب (2 لتر) لدعم الهضم وزيادة طاقة العضلات."
                q.contains("بروتين") || q.contains("أكل") || q.contains("طعام") || q.contains("وجبة") || q.contains("دايت") || q.contains("protein") ->
                    "لهدفك في وزن ${profile.targetWeightKg} كجم، تناول من 1.6 إلى 2.2 غرام بروتين لكل كجم من وزنك، مع الخضروات الطازجة والحبوب الكاملة."
                q.contains("تعب") || q.contains("ألم") || q.contains("استشفاء") || q.contains("راحة") || q.contains("sore") || q.contains("rest") ->
                    "إذا شعرت بإجهاد عضلي، احرص على النوم 7-8 ساعات والإطالات الخفيفة والترطيب الجيد لتسريع تعافي الأنسجة."
                q.contains("خطوات") || q.contains("مشي") || q.contains("كارديو") || q.contains("step") || q.contains("walk") ->
                    "أنت حالياً عند $steps خطوة. المشي السريع لمدة 15 دقيقة بعد الوجبات ينشط الدورة الدموية ويقربك من هدفك اليومي!"
                else ->
                    "سؤال رائع! ركز على الانتظام في التمرين، والزيادة التدريجية، وشرب 2 لتر ماء يومياً، مع النوم الجيد للوصول لهدفك (${profile.targetWeightKg} كجم)."
            }
            "es" -> when {
                q.contains("agua") || q.contains("hidrat") || q.contains("water") ->
                    "¡La hidratación es clave! Llevas $water vasos hoy. Intenta alcanzar al menos 8 vasos (2 litros) para potenciar tu energía y digestión."
                q.contains("prote") || q.contains("comida") || q.contains("dieta") || q.contains("alimento") || q.contains("protein") ->
                    "Para tu meta de ${profile.targetWeightKg}kg, prioriza entre 1.6g y 2.2g de proteína por kg de peso corporal, junto con vegetales frescos y granos enteros."
                q.contains("dolor") || q.contains("agujetas") || q.contains("recupera") || q.contains("descanso") || q.contains("sore") ->
                    "Si sientes fatiga muscular, prioriza 7-8 horas de sueño, estiramientos suaves y una buena hidratación para acelerar la reparación celular."
                q.contains("pasos") || q.contains("caminar") || q.contains("cardio") || q.contains("step") ->
                    "Llevas $steps pasos acumulados hoy. Una caminata de 15 minutos tras comer ayuda a regular la glucosa y alcanzar tu meta diaria con facilidad."
                else ->
                    "¡Excelente pregunta! Enfócate en la constancia en tus entrenamientos, sobrecarga progresiva, beber 2L de agua y descansar para lograr tu meta de ${profile.targetWeightKg}kg."
            }
            else -> when {
                q.contains("water") || q.contains("hydrate") ->
                    "Hydration is key! You've logged $water glasses today. Aim for at least 8 glasses (2 Liters) to support digestion and muscle power."
                q.contains("protein") || q.contains("eat") || q.contains("food") || q.contains("diet") ->
                    "For your goal of ${profile.targetWeightKg}kg, prioritize 1.6g to 2.2g of protein per kg of body weight, accompanied by fresh vegetables and whole grains."
                q.contains("sore") || q.contains("recovery") || q.contains("rest") ->
                    "If your muscles feel fatigued, prioritize 7-8 hours of sleep, light dynamic stretching, and adequate magnesium and hydration to accelerate tissue repair."
                q.contains("step") || q.contains("walk") || q.contains("cardio") ->
                    "You're currently at $steps steps. Adding a brisk 15-minute walk after meals helps regulate blood sugar and easily hits your daily step target!"
                else ->
                    "Great question! Focus on consistent training, progressive overload, drinking at least 2L of water, and getting adequate rest to hit your target of ${profile.targetWeightKg}kg."
            }
        }
    }

    private data class Tuple6<A, B, C, D, E, F>(
        val a: A, val b: B, val c: C, val d: D, val e: E, val f: F
    )
}

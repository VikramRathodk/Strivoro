package com.devvikram.striveo.ui.screens.onboarding

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.devvikram.striveo.R
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

data class StriveoFeature(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val color: Color
)

data class OnboardingPage(
    val title: String,
    val subtitle: String,
    val question: String? = null,
    val options: List<String> = emptyList(),
    val isMultiSelect: Boolean = false,
    val hasSlider: Boolean = false,
    val sliderRange: ClosedFloatingPointRange<Float> = 1f..10f,
    val sliderLabel: String = "",
    val hasFeatures: Boolean = false,
    val features: List<StriveoFeature> = emptyList(),
    val pageType: PageType = PageType.STANDARD
)

enum class PageType {
    WELCOME,
    FEATURES,
    STANDARD,
    PREFERENCES,
    COMPLETION
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnBoardingScreen(
    viewModel: OnboardingViewModel,
    onOnboardingComplete: () -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val coroutineScope = rememberCoroutineScope()
    var selectedAnswers by remember { mutableStateOf(mutableMapOf<Int, List<String>>()) }
    var sliderValues by remember { mutableStateOf(mutableMapOf<Int, Float>()) }


    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Enhanced Animated Background
            EnhancedAnimatedBackground()

            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                AnimatedVisibility(
                    visible = onboardingPages[pagerState.currentPage].pageType != PageType.WELCOME,
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically()
                ) {
                    StepIndicator(
                        currentStep = pagerState.currentPage,
                        totalSteps = onboardingPages.size,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Pager Content
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f)
                ) { page ->
                    val currentPage = onboardingPages[page]

                    when (currentPage.pageType) {
                        PageType.WELCOME -> WelcomePageContent()
                        PageType.FEATURES -> FeaturesPageContent(currentPage)
                        PageType.PREFERENCES -> PreferencesPageContent(
                            page = currentPage,
                            pageIndex = page,
                            sliderValue = sliderValues[page] ?: currentPage.sliderRange.start,
                            onSliderChanged = { value ->
                                sliderValues = sliderValues.toMutableMap().apply {
                                    put(page, value)
                                }
                            }
                        )

                        else -> OnboardingPageContent(
                            page = currentPage,
                            pageIndex = page,
                            selectedAnswers = selectedAnswers[page] ?: emptyList(),
                            onAnswerSelected = { answers ->
                                selectedAnswers = selectedAnswers.toMutableMap().apply {
                                    put(page, answers)
                                }
                            }
                        )
                    }
                }

                //  Navigation
                NavigationSection(
                    pagerState = pagerState,
                    selectedAnswers = selectedAnswers,
                    sliderValues = sliderValues,
                    onPrevious = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    },
                    onNext = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    },
                    onComplete = onOnboardingComplete
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepIndicator(
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = currentStep.toFloat() / (totalSteps - 1).coerceAtLeast(1),
        animationSpec = tween(
            durationMillis = 600,
            easing = FastOutSlowInEasing
        ),
        label = "progress_animation"
    )

    val primary = MaterialTheme.colorScheme.primary
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val onPrimary = MaterialTheme.colorScheme.onPrimary

    Canvas(modifier = modifier.height(40.dp)) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val centerY = canvasHeight / 2
        val circleRadius = 12.dp.toPx()
        val lineHeight = 4.dp.toPx()

        // Calculate positions
        val stepWidth = if (totalSteps > 1) canvasWidth / (totalSteps - 1) else canvasWidth

        // Draw background line
        drawRoundRect(
            color = surfaceVariant,
            topLeft = Offset(circleRadius, centerY - lineHeight / 2),
            size = Size(canvasWidth - 2 * circleRadius, lineHeight),
            cornerRadius = CornerRadius(lineHeight / 2)
        )

        // Draw progress line
        val progressWidth = (canvasWidth - 2 * circleRadius) * animatedProgress
        if (progressWidth > 0) {
            drawRoundRect(
                color = primary,
                topLeft = Offset(circleRadius, centerY - lineHeight / 2),
                size = Size(progressWidth, lineHeight),
                cornerRadius = CornerRadius(lineHeight / 2)
            )
        }

        // Draw step circles
        repeat(totalSteps) { index ->
            val centerX = stepWidth * index
            val isCompleted = index < currentStep
            val isCurrent = index == currentStep

            // Circle background
            drawCircle(
                color = when {
                    isCompleted -> primary
                    isCurrent -> primary
                    else -> surfaceVariant
                },
                radius = circleRadius,
                center = Offset(centerX, centerY)
            )

            // Inner circle for current step
            if (isCurrent) {
                drawCircle(
                    color = onPrimary,
                    radius = circleRadius * 0.4f,
                    center = Offset(centerX, centerY)
                )
            }

            // Check mark for completed steps
            if (isCompleted) {
                val checkSize = circleRadius * 0.6f
                val checkStroke = 2.dp.toPx()

                // Simple check mark using lines
                drawLine(
                    color = onPrimary,
                    start = Offset(centerX - checkSize * 0.3f, centerY),
                    end = Offset(centerX - checkSize * 0.1f, centerY + checkSize * 0.3f),
                    strokeWidth = checkStroke,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = onPrimary,
                    start = Offset(centerX - checkSize * 0.1f, centerY + checkSize * 0.3f),
                    end = Offset(centerX + checkSize * 0.4f, centerY - checkSize * 0.2f),
                    strokeWidth = checkStroke,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

@Composable
fun WelcomePageContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Animated Logo/Icon
        val scale by rememberInfiniteTransition(label = "logo").animateFloat(
            initialValue = 1f,
            targetValue = 1.1f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "logo_scale"
        )
        val lottieComposition by rememberLottieComposition(
            spec = LottieCompositionSpec.RawRes(R.raw.welcome_goal_animation),
            )
        Box(
            modifier = Modifier
                .size(120.dp)
                .scale(scale)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                        )
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            LottieAnimation(
                composition = lottieComposition,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier.size(120.dp)
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "Transform Your Productivity",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Join thousands of users who've revolutionized their workflow with Striveo's intelligent task management",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Quick stats
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            StatCard("50K+", "Active Users")
            StatCard("95%", "Productivity Boost")
        }
    }
}

@Composable
fun StatCard(value: String, label: String) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun FeaturesPageContent(page: OnboardingPage) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = page.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                // Group features into groups of 3 for vertical display
                val featureGroups = page.features.chunked(3)

                items(featureGroups) { group ->
                    Column(
                        modifier = Modifier.width(280.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Display up to 3 features vertically in each column
                        group.forEach { feature ->
                            FeatureCard(feature = feature)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FeatureCard(feature: StriveoFeature) {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "feature_scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        colors = CardDefaults.cardColors(
            containerColor = feature.color.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = feature.color.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    feature.icon,
                    contentDescription = null,
                    tint = feature.color,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = feature.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = feature.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
fun PreferencesPageContent(
    page: OnboardingPage,
    pageIndex: Int,
    sliderValue: Float,
    onSliderChanged: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = page.subtitle,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(48.dp))

        if (page.hasSlider) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = page.sliderLabel,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "${sliderValue.toInt()} tasks per day",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Slider(
                        value = sliderValue,
                        onValueChange = onSliderChanged,
                        valueRange = page.sliderRange,
                        steps = (page.sliderRange.endInclusive - page.sliderRange.start).toInt() - 1,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Light",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "Heavy",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OnboardingPageContent(
    page: OnboardingPage,
    pageIndex: Int,
    selectedAnswers: List<String>,
    onAnswerSelected: (List<String>) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = page.subtitle,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        page.question?.let { question ->
            Text(
                text = question,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(32.dp))
        }

        // Options with LazyColumn
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(page.options) { index, option ->
                val isSelected = selectedAnswers.contains(option)
                val animatedScale by animateFloatAsState(
                    targetValue = if (isSelected) 1.02f else 1f,
                    animationSpec = tween(200, easing = FastOutSlowInEasing),
                    label = "option_scale"
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .scale(animatedScale)
                        .clickable {
                            if (page.isMultiSelect) {
                                val newAnswers = if (isSelected) {
                                    selectedAnswers - option
                                } else {
                                    selectedAnswers + option
                                }
                                onAnswerSelected(newAnswers)
                            } else {
                                onAnswerSelected(listOf(option))
                            }
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected)
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        else
                            MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (isSelected)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.outline
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isSelected)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )

                        AnimatedVisibility(
                            visible = isSelected,
                            enter = fadeIn() + slideInVertically(),
                            exit = fadeOut() + slideOutVertically()
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NavigationSection(
    pagerState: PagerState,
    selectedAnswers: Map<Int, List<String>>,
    sliderValues: Map<Int, Float>,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onComplete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Page Indicators at the top
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                repeat(onboardingPages.size) { index ->
                    val isActive = index == pagerState.currentPage
                    val animatedWidth by animateFloatAsState(
                        targetValue = if (isActive) 28f else 10f,
                        animationSpec = tween(300, easing = FastOutSlowInEasing),
                        label = "indicator_width"
                    )

                    Box(
                        modifier = Modifier
                            .width(animatedWidth.dp)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                if (isActive)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                            )
                    )
                }
            }

            // Buttons row below indicators
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Previous Button (Left side)
                if (pagerState.currentPage > 0) {
                    OutlinedButton(
                        onClick = onPrevious,
                        shape = CircleShape,
                        modifier = Modifier.align(Alignment.CenterStart),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            Icons.Default.KeyboardArrowLeft,
                            contentDescription = "Previous",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Next/Complete Button (Right side)
                val currentPage = onboardingPages[pagerState.currentPage]
                val hasRequiredInput = when (currentPage.pageType) {
                    PageType.WELCOME, PageType.FEATURES, PageType.COMPLETION -> true
                    PageType.PREFERENCES -> sliderValues[pagerState.currentPage] != null
                    else -> selectedAnswers[pagerState.currentPage]?.isNotEmpty() == true
                }

                if (pagerState.currentPage < onboardingPages.size - 1) {
                    Button(
                        onClick = onNext,
                        enabled = hasRequiredInput,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.align(Alignment.CenterEnd),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(
                            "Next",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            Icons.Default.KeyboardArrowRight,
                            contentDescription = "Next",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else {
                    Button(
                        onClick = onComplete,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.align(Alignment.CenterEnd),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            "Start Journey 🚀",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun EnhancedAnimatedBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "background")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(45000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val float1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 80f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float1"
    )

    val float2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -60f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float2"
    )

    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .alpha(0.08f)
    ) {
        val centerX = size.width / 2
        val centerY = size.height / 2

        // Enhanced floating elements
        drawEnhancedTaskCard(
            center = Offset(centerX - 180 + float1, centerY - 250),
            rotation = rotation * 0.3f,
            scale = pulse
        )

        drawEnhancedTaskCard(
            center = Offset(centerX + 220 + float2, centerY + 120),
            rotation = -rotation * 0.4f,
            scale = pulse * 0.8f
        )

        // AI Brain visualization
        drawAIBrain(
            center = Offset(centerX - 250, centerY - 80 + float1 * 0.3f),
            rotation = rotation * 0.1f
        )

        // Analytics chart
        drawAnalyticsChart(
            center = Offset(centerX + 180, centerY - 180 + float2 * 0.5f),
            pulse = pulse
        )

        // Productivity rings
        drawProductivityRings(
            center = Offset(centerX - 100, centerY + 200 + float1 * 0.4f),
            rotation = rotation
        )

        // Goal targets
        drawGoalTarget(
            center = Offset(centerX + 150, centerY + 250 + float2 * 0.6f),
            pulse = pulse
        )
    }
}

fun DrawScope.drawEnhancedTaskCard(center: Offset, rotation: Float, scale: Float) {
    rotate(rotation, pivot = center) {
        val cardSize = Size(140f * scale, 90f * scale)
        val topLeft = Offset(center.x - cardSize.width / 2, center.y - cardSize.height / 2)

        // Enhanced shadow with gradient
        drawRoundRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.Black.copy(alpha = 0.15f),
                    Color.Transparent
                ),
                center = center + Offset(6f, 6f),
                radius = cardSize.width / 2
            ),
            topLeft = topLeft + Offset(6f, 6f),
            size = cardSize,
            cornerRadius = CornerRadius(16f)
        )

        // Card background with gradient
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.9f),
                    Color.White.copy(alpha = 0.7f)
                )
            ),
            topLeft = topLeft,
            size = cardSize,
            cornerRadius = CornerRadius(16f)
        )

        // Header section
        drawRoundRect(
            color = Color.Blue.copy(alpha = 0.2f),
            topLeft = topLeft,
            size = Size(cardSize.width, 20f),
            cornerRadius = CornerRadius(
                packedValue = 8
            )
        )

        // Task progress bars
        repeat(3) { i ->
            val progress = listOf(0.8f, 0.6f, 0.3f)[i]
            drawRoundRect(
                color = Color.Gray.copy(alpha = 0.2f),
                topLeft = topLeft + Offset(12f, 30f + i * 16f),
                size = Size(cardSize.width - 24f, 6f),
                cornerRadius = CornerRadius(3f)
            )
            drawRoundRect(
                color = Color.Green.copy(alpha = 0.6f),
                topLeft = topLeft + Offset(12f, 30f + i * 16f),
                size = Size((cardSize.width - 24f) * progress, 6f),
                cornerRadius = CornerRadius(3f)
            )
        }
    }
}

fun DrawScope.drawAIBrain(center: Offset, rotation: Float) {
    rotate(rotation, pivot = center) {
        // Brain outline
        val path = Path().apply {
            moveTo(center.x - 25, center.y)
            cubicTo(
                center.x - 35, center.y - 20,
                center.x - 20, center.y - 35,
                center.x, center.y - 30
            )
            cubicTo(
                center.x + 20, center.y - 35,
                center.x + 35, center.y - 20,
                center.x + 25, center.y
            )
            cubicTo(
                center.x + 30, center.y + 15,
                center.x + 15, center.y + 25,
                center.x, center.y + 20
            )
            cubicTo(
                center.x - 15, center.y + 25,
                center.x - 30, center.y + 15,
                center.x - 25, center.y
            )
            close()
        }

        drawPath(
            path = path,
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.Magenta.copy(alpha = 0.3f),
                    Color.Blue.copy(alpha = 0.1f)
                ),
                center = center
            )
        )

        // Neural connections
        repeat(6) { i ->
            val angle = (i * 60f) * (Math.PI / 180).toFloat()
            val startRadius = 15f
            val endRadius = 25f

            drawLine(
                color = Color.Cyan.copy(alpha = 0.4f),
                start = center + Offset(
                    cos(angle) * startRadius,
                    sin(angle) * startRadius
                ),
                end = center + Offset(
                    cos(angle) * endRadius,
                    sin(angle) * endRadius
                ),
                strokeWidth = 2f,
                cap = StrokeCap.Round
            )
        }
    }
}

fun DrawScope.drawAnalyticsChart(center: Offset, pulse: Float) {
    val chartWidth = 80f * pulse
    val chartHeight = 60f * pulse
    val bars = listOf(0.3f, 0.7f, 0.5f, 0.9f, 0.6f)

    // Chart background
    drawRoundRect(
        color = Color.White.copy(alpha = 0.8f),
        topLeft = center - Offset(chartWidth / 2, chartHeight / 2),
        size = Size(chartWidth, chartHeight),
        cornerRadius = CornerRadius(8f)
    )

    // Draw bars
    bars.forEachIndexed { index, height ->
        val barWidth = 8f
        val barHeight = chartHeight * height * 0.6f
        val x = center.x - chartWidth / 2 + 10f + index * 12f
        val y = center.y + chartHeight / 2 - 10f - barHeight

        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.Blue.copy(alpha = 0.6f),
                    Color.Blue.copy(alpha = 0.3f)
                )
            ),
            topLeft = Offset(x, y),
            size = Size(barWidth, barHeight),
            cornerRadius = CornerRadius(2f)
        )
    }
}

fun DrawScope.drawProductivityRings(center: Offset, rotation: Float) {
    val rings = listOf(
        Triple(30f, Color.Green.copy(alpha = 0.4f), 0.8f),
        Triple(40f, Color.Magenta.copy(alpha = 0.3f), 0.6f),
        Triple(50f, Color.Red.copy(alpha = 0.2f), 0.9f)
    )

    rings.forEachIndexed { index, (radius, color, progress) ->
        rotate(rotation + index * 45f, pivot = center) {
            val sweepAngle = 360f * progress
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = center - Offset(radius, radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = 4f, cap = StrokeCap.Round)
            )
        }
    }
}

fun DrawScope.drawGoalTarget(center: Offset, pulse: Float) {
    val targetSize = 60f * pulse

    // Outer rings
    repeat(3) { i ->
        val radius = (targetSize / 2) - (i * 8f)
        drawCircle(
            color = Color.Red.copy(alpha = 0.2f - i * 0.05f),
            radius = radius,
            center = center,
            style = Stroke(width = 2f)
        )
    }

    // Center dot
    drawCircle(
        color = Color.Red.copy(alpha = 0.6f),
        radius = 4f,
        center = center
    )

    // Arrow pointing to target
    val arrowPath = Path().apply {
        moveTo(center.x - 15, center.y - 5)
        lineTo(center.x - 8, center.y)
        lineTo(center.x - 15, center.y + 5)
        moveTo(center.x - 20, center.y)
        lineTo(center.x - 8, center.y)
    }

    drawPath(
        path = arrowPath,
        color = Color.Red.copy(alpha = 0.5f),
        style = Stroke(width = 2f, cap = StrokeCap.Round)
    )
}

val striveoFeatures = listOf(
    StriveoFeature(
        icon = Icons.Default.CheckCircle,
        title = "AI-Powered Task Prioritization",
        description = "Smart algorithms analyze your habits and deadlines to automatically prioritize your tasks for maximum productivity.",
        color = Color(0xFF6366F1)
    ),
    StriveoFeature(
        icon = Icons.Default.DateRange,
        title = "Dynamic Time Blocking",
        description = "Intelligent calendar integration that adapts to your energy levels and automatically schedules focused work sessions.",
        color = Color(0xFF10B981)
    ),
    StriveoFeature(
        icon = Icons.Default.ThumbUp,
        title = "Productivity Analytics",
        description = "Detailed insights into your work patterns, peak performance hours, and personalized recommendations for improvement.",
        color = Color(0xFFF59E0B)
    ),
    StriveoFeature(
        icon = Icons.Default.Notifications,
        title = "Smart Reminders",
        description = "Context-aware notifications that remind you at the perfect moment, considering your location, schedule, and preferences.",
        color = Color(0xFFEF4444)
    ),
    StriveoFeature(
        icon = Icons.Default.DateRange,
        title = "Team Collaboration",
        description = "Seamlessly collaborate with teammates, share progress, and coordinate projects with real-time synchronization.",
        color = Color(0xFF8B5CF6)
    ),
    StriveoFeature(
        icon = Icons.Default.Star,
        title = "Goal Achievement System",
        description = "Break down big goals into manageable steps with milestone tracking and celebration of your achievements.",
        color = Color(0xFFEC4899)
    )
)

val onboardingPages = listOf(
    OnboardingPage(
        title = "Welcome to Striveo! 🎯",
        subtitle = "Your journey to peak productivity starts here. Let's create a personalized experience that transforms how you work and achieve your goals.",
        pageType = PageType.WELCOME
    ),
    OnboardingPage(
        title = "Meet Your Productivity Superpowers 🚀",
        subtitle = "Discover how Striveo's intelligent features will revolutionize your workflow and help you achieve more than ever before.",
        features = striveoFeatures,
        pageType = PageType.FEATURES
    ),
    OnboardingPage(
        title = "What's Your Productivity Challenge? 🎯",
        subtitle = "Understanding your biggest hurdles helps us customize Striveo to address your specific needs.",
        question = "What's your main productivity challenge?",
        options = listOf(
            "I get overwhelmed by too many tasks",
            "I struggle with procrastination",
            "I can't prioritize effectively",
            "I get distracted easily",
            "I don't track my progress well",
            "I have trouble meeting deadlines"
        )
    ),
    OnboardingPage(
        title = "How's Your Current Task Load? 📊",
        subtitle = "This helps us calibrate the perfect amount of daily tasks and set realistic expectations for your productivity journey.",
        sliderLabel = "How many tasks do you typically handle per day?",
        hasSlider = true,
        sliderRange = 1f..25f,
        pageType = PageType.PREFERENCES
    ),
    OnboardingPage(
        title = "What's Your Work Environment? 🏢",
        subtitle = "Your workspace greatly influences your productivity patterns. Let's optimize Striveo for your environment.",
        question = "Where do you primarily work?",
        options = listOf(
            "Traditional office setting",
            "Home office / Remote work",
            "Co-working spaces",
            "Mobile / Always on the go",
            "Hybrid (mix of office and remote)",
            "Student environment"
        )
    ),
    OnboardingPage(
        title = "When Is Your Peak Performance Time? ⏰",
        subtitle = "Knowing your natural energy rhythms helps us schedule your most important tasks when you're at your best.",
        question = "When do you feel most productive?",
        options = listOf(
            "Early morning (6-9 AM)",
            "Mid-morning (9 AM-12 PM)",
            "Afternoon (12-3 PM)",
            "Late afternoon (3-6 PM)",
            "Evening (6-9 PM)",
            "Night owl (9 PM+)"
        )
    ),
    OnboardingPage(
        title = "What Are Your Focus Areas? 🎯",
        subtitle = "Select all areas you want to improve. We'll create custom categories and insights for each one.",
        question = "Which areas do you want to focus on? (Select all that apply)",
        options = listOf(
            "Career & Professional Growth",
            "Health & Wellness",
            "Learning & Skill Development",
            "Personal Projects & Hobbies",
            "Relationships & Family Time",
            "Financial Goals & Planning",
            "Creative Pursuits",
            "Community & Volunteering"
        ),
        isMultiSelect = true
    ),
//    OnboardingPage(
//        title = "How Do You Prefer Task Reminders? 📱",
//        subtitle = "Customize your notification experience to stay on track without feeling overwhelmed.",
//        question = "What's your preferred reminder style?",
//        options = listOf(
//            "Gentle nudges throughout the day",
//            "Focused morning planning session",
//            "Evening review and next-day prep",
//            "Just-in-time contextual reminders",
//            "Weekly batch planning sessions",
//            "Minimal reminders, I'll check manually"
//        )
//    ),
//    OnboardingPage(
//        title = "What Motivates You Most? 💪",
//        subtitle = "Understanding your motivation style helps us celebrate your wins and keep you engaged long-term.",
//        question = "What keeps you motivated to complete tasks?",
//        options = listOf(
//            "Visual progress tracking & charts",
//            "Achievement badges & milestones",
//            "Streak counters & consistency",
//            "Positive reinforcement & encouragement",
//            "Competition with friends/colleagues",
//            "Personal satisfaction & inner drive"
//        )
//    ),
//    OnboardingPage(
//        title = "How Tech-Savvy Are You? 💻",
//        subtitle = "This helps us adjust the interface complexity and feature recommendations to match your comfort level.",
//        question = "How would you describe your tech comfort level?",
//        options = listOf(
//            "Tech enthusiast - I love advanced features",
//            "Comfortable with most apps and tools",
//            "I prefer simple, intuitive interfaces",
//            "I need guidance with new technology",
//            "Keep it minimal - just the basics please"
//        )
//    ),
    OnboardingPage(
        title = "Perfect! You're All Set! 🎉",
        subtitle = "Your personalized Striveo experience is ready! Based on your preferences, we've customized everything from task suggestions to notification timing. Time to transform your productivity!",
        question = "Ready to start your productivity transformation?",
        options = listOf(
            "Yes! Let's boost my productivity! 🚀",
            "I'm excited to get organized! ✨",
            "Ready to achieve my goals! 🎯"
        ),
        pageType = PageType.COMPLETION
    )
)





// Preview parameter provider for different screen states
class OnboardingPreviewParameterProvider : PreviewParameterProvider<Int> {
    override val values = sequenceOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
}

@Preview(
    name = "Welcome Screen",
    showBackground = true,
    backgroundColor = 0xFFF5F5F5
)
@Composable
fun WelcomePagePreview() {
    MaterialTheme {
        Surface {
            Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                WelcomePageContent()
            }
        }
    }
}

@Preview(
    name = "Features Screen",
    showBackground = true,
    backgroundColor = 0xFFF5F5F5
)
@Composable
fun FeaturesPagePreview() {
    MaterialTheme {
        Surface {
            Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                FeaturesPageContent(
                    page = OnboardingPage(
                        title = "Meet Your Productivity Superpowers 🚀",
                        subtitle = "Discover how Striveo's intelligent features will revolutionize your workflow and help you achieve more than ever before.",
                        features = striveoFeatures,
                        pageType = PageType.FEATURES
                    )
                )
            }
        }
    }
}

@Preview(
    name = "Question Screen",
    showBackground = true,
    backgroundColor = 0xFFF5F5F5
)
@Composable
fun QuestionPagePreview() {
    var selectedAnswers by remember { mutableStateOf(listOf("I get overwhelmed by too many tasks")) }

    MaterialTheme {
        Surface {
            Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                OnboardingPageContent(
                    page = OnboardingPage(
                        title = "What's Your Productivity Challenge? 🎯",
                        subtitle = "Understanding your biggest hurdles helps us customize Striveo to address your specific needs.",
                        question = "What's your main productivity challenge?",
                        options = listOf(
                            "I get overwhelmed by too many tasks",
                            "I struggle with procrastination",
                            "I can't prioritize effectively",
                            "I get distracted easily",
                            "I don't track my progress well",
                            "I have trouble meeting deadlines"
                        )
                    ),
                    pageIndex = 2,
                    selectedAnswers = selectedAnswers,
                    onAnswerSelected = { answers -> selectedAnswers = answers }
                )
            }
        }
    }
}

@Preview(
    name = "Multi-Select Question",
    showBackground = true,
    backgroundColor = 0xFFF5F5F5
)
@Composable
fun MultiSelectQuestionPreview() {
    var selectedAnswers by remember {
        mutableStateOf(listOf("Career & Professional Growth", "Health & Wellness"))
    }

    MaterialTheme {
        Surface {
            Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                OnboardingPageContent(
                    page = OnboardingPage(
                        title = "What Are Your Focus Areas? 🎯",
                        subtitle = "Select all areas you want to improve. We'll create custom categories and insights for each one.",
                        question = "Which areas do you want to focus on? (Select all that apply)",
                        options = listOf(
                            "Career & Professional Growth",
                            "Health & Wellness",
                            "Learning & Skill Development",
                            "Personal Projects & Hobbies",
                            "Relationships & Family Time",
                            "Financial Goals & Planning"
                        ),
                        isMultiSelect = true
                    ),
                    pageIndex = 6,
                    selectedAnswers = selectedAnswers,
                    onAnswerSelected = { answers -> selectedAnswers = answers }
                )
            }
        }
    }
}

@Preview(
    name = "Slider Preferences",
    showBackground = true,
    backgroundColor = 0xFFF5F5F5
)
@Composable
fun SliderPreferencesPreview() {
    var sliderValue by remember { mutableStateOf(8f) }

    MaterialTheme {
        Surface {
            Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                PreferencesPageContent(
                    page = OnboardingPage(
                        title = "How's Your Current Task Load? 📊",
                        subtitle = "This helps us calibrate the perfect amount of daily tasks and set realistic expectations for your productivity journey.",
                        sliderLabel = "How many tasks do you typically handle per day?",
                        hasSlider = true,
                        sliderRange = 1f..25f,
                        pageType = PageType.PREFERENCES
                    ),
                    pageIndex = 3,
                    sliderValue = sliderValue,
                    onSliderChanged = { value -> sliderValue = value }
                )
            }
        }
    }
}

@Preview(
    name = "Step Indicator",
    showBackground = true
)
@Composable
fun StepIndicatorPreview() {
    MaterialTheme {
        Surface {
            Box(modifier = Modifier.padding(16.dp)) {
                StepIndicator(
                    currentStep = 3,
                    totalSteps = 11,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Preview(
    name = "Feature Card",
    showBackground = true
)
@Composable
fun FeatureCardPreview() {
    MaterialTheme {
        Surface {
            Box(modifier = Modifier.padding(16.dp)) {
                FeatureCard(
                    feature = StriveoFeature(
                        icon = androidx.compose.material.icons.Icons.Default.CheckCircle,
                        title = "AI-Powered Task Prioritization",
                        description = "Smart algorithms analyze your habits and deadlines to automatically prioritize your tasks for maximum productivity.",
                        color = androidx.compose.ui.graphics.Color(0xFF6366F1)
                    )
                )
            }
        }
    }
}

@Preview(
    name = "Stat Card",
    showBackground = true
)
@Composable
fun StatCardPreview() {
    MaterialTheme {
        Surface {
            Box(modifier = Modifier.padding(16.dp)) {
                StatCard(value = "50K+", label = "Active Users")
            }
        }
    }
}

@Preview(
    name = "Navigation Section - Middle",
    showBackground = true
)
@Composable
fun NavigationSectionPreview() {
    MaterialTheme {
        Surface {
            Box(modifier = Modifier.padding(16.dp)) {
                NavigationSection(
                    pagerState = androidx.compose.foundation.pager.rememberPagerState(
                        initialPage = 5,
                        pageCount = { 11 }
                    ),
                    selectedAnswers = mapOf(
                        2 to listOf("I get overwhelmed by too many tasks"),
                        5 to listOf("Mid-morning (9 AM-12 PM)"),
                        6 to listOf("Career & Professional Growth", "Health & Wellness")
                    ),
                    sliderValues = mapOf(3 to 8f),
                    onPrevious = {},
                    onNext = {},
                    onComplete = {}
                )
            }
        }
    }
}

@Preview(
    name = "Navigation Section - First Page",
    showBackground = true
)
@Composable
fun NavigationSectionFirstPagePreview() {
    MaterialTheme {
        Surface {
            Box(modifier = Modifier.padding(16.dp)) {
                NavigationSection(
                    pagerState = androidx.compose.foundation.pager.rememberPagerState(
                        initialPage = 0,
                        pageCount = { 11 }
                    ),
                    selectedAnswers = emptyMap(),
                    sliderValues = emptyMap(),
                    onPrevious = {},
                    onNext = {},
                    onComplete = {}
                )
            }
        }
    }
}

@Preview(
    name = "Navigation Section - Last Page",
    showBackground = true
)
@Composable
fun NavigationSectionLastPagePreview() {
    MaterialTheme {
        Surface {
            Box(modifier = Modifier.padding(16.dp)) {
                NavigationSection(
                    pagerState = androidx.compose.foundation.pager.rememberPagerState(
                        initialPage = 10,
                        pageCount = { 11 }
                    ),
                    selectedAnswers = mapOf(10 to listOf("Yes! Let's boost my productivity! 🚀")),
                    sliderValues = emptyMap(),
                    onPrevious = {},
                    onNext = {},
                    onComplete = {}
                )
            }
        }
    }
}

@Preview(
    name = "Enhanced Animated Background",
    showBackground = true,
    backgroundColor = 0xFFF5F5F5
)
@Composable
fun EnhancedAnimatedBackgroundPreview() {
    MaterialTheme {
        Surface {
            Box(modifier = Modifier.fillMaxSize()) {
                EnhancedAnimatedBackground()
            }
        }
    }
}

@Preview()
@Composable
fun CompleteOnboardingScreenPreview(
    @PreviewParameter(OnboardingPreviewParameterProvider::class) pageIndex: Int
) {
    MaterialTheme {
        Surface {
            // Mock the complete screen with state management
            var currentPage by remember { mutableStateOf(pageIndex.coerceIn(0, onboardingPages.size - 1)) }
            var selectedAnswers by remember {
                mutableStateOf(
                    mapOf(
                        2 to listOf("I get overwhelmed by too many tasks"),
                        4 to listOf("Home office / Remote work"),
                        5 to listOf("Mid-morning (9 AM-12 PM)"),
                        6 to listOf("Career & Professional Growth", "Health & Wellness"),
                        7 to listOf("Gentle nudges throughout the day"),
                        8 to listOf("Visual progress tracking & charts"),
                        9 to listOf("Comfortable with most apps and tools"),
                        10 to listOf("Yes! Let's boost my productivity! 🚀")
                    )
                )
            }
            var sliderValues by remember { mutableStateOf(mapOf(3 to 8f)) }

//            OnBoardingScreen(
//                viewModel = MockOnboardingViewModel(),
//                onOnboardingComplete = { /* Mock completion */ }
//            )
        }
    }
}



// Individual page previews for easier development
@Preview(name = "Page 0 - Welcome", showBackground = true)
@Composable fun Page0Preview() = PagePreview(0)

@Preview(name = "Page 1 - Features", showBackground = true)
@Composable fun Page1Preview() = PagePreview(1)

@Preview(name = "Page 2 - Challenge", showBackground = true)
@Composable fun Page2Preview() = PagePreview(2)

@Preview(name = "Page 3 - Task Load", showBackground = true)
@Composable fun Page3Preview() = PagePreview(3)

@Preview(name = "Page 4 - Environment", showBackground = true)
@Composable fun Page4Preview() = PagePreview(4)

@Preview(name = "Page 5 - Peak Time", showBackground = true)
@Composable fun Page5Preview() = PagePreview(5)

@Preview(name = "Page 6 - Focus Areas", showBackground = true)
@Composable fun Page6Preview() = PagePreview(6)

@Preview(name = "Page 7 - Reminders", showBackground = true)
@Composable fun Page7Preview() = PagePreview(7)

@Preview(name = "Page 8 - Motivation", showBackground = true)
@Composable fun Page8Preview() = PagePreview(8)

@Preview(name = "Page 9 - Tech Level", showBackground = true)
@Composable fun Page9Preview() = PagePreview(9)

@Preview(name = "Page 10 - Completion", showBackground = true)
@Composable fun Page10Preview() = PagePreview(10)

@Composable
private fun PagePreview(pageIndex: Int) {
    MaterialTheme {
        Surface {
            Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                val page = onboardingPages[pageIndex]
                when (page.pageType) {
                    PageType.WELCOME -> WelcomePageContent()
                    PageType.FEATURES -> FeaturesPageContent(page)
                    PageType.PREFERENCES -> {
                        var sliderValue by remember { mutableStateOf(8f) }
                        PreferencesPageContent(
                            page = page,
                            pageIndex = pageIndex,
                            sliderValue = sliderValue,
                            onSliderChanged = { sliderValue = it }
                        )
                    }
                    else -> {
                        var selectedAnswers by remember {
                            mutableStateOf(
                                if (page.isMultiSelect) {
                                    listOf(page.options.first(), page.options.getOrNull(1) ?: "")
                                        .filter { it.isNotEmpty() }
                                } else {
                                    listOf(page.options.firstOrNull() ?: "")
                                        .filter { it.isNotEmpty() }
                                }
                            )
                        }
                        OnboardingPageContent(
                            page = page,
                            pageIndex = pageIndex,
                            selectedAnswers = selectedAnswers,
                            onAnswerSelected = { selectedAnswers = it }
                        )
                    }
                }
            }
        }
    }
}

// Dark theme previews
@Preview(
    name = "Welcome Screen - Dark",
    showBackground = true,
    backgroundColor = 0xFF121212,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun WelcomePageDarkPreview() {
    MaterialTheme {
        Surface {
            Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                WelcomePageContent()
            }
        }
    }
}

@Preview(
    name = "Complete Screen - Dark",
    showBackground = true,
    backgroundColor = 0xFF121212,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=411dp,height=891dp"
)
@Composable
fun CompleteOnboardingScreenDarkPreview() {
    MaterialTheme {
        Surface {
//            OnBoardingScreen(
//                viewModel = MockOnboardingViewModel(),
//                onOnboardingComplete = { /* Mock completion */ }
//            )
        }
    }
}
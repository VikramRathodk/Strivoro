package com.devvikram.striveo.ui.reuseables.backgrounds

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.math.*
import kotlin.random.Random

object BackGrounds {

    /**
     * Military Camouflage Pattern Background
     * Perfect for: Login screens, main dashboard
     * Features: Digital camo pattern with tactical colors
     */
    @Composable
    fun CamouflageBackground(
        modifier: Modifier = Modifier,
        primaryColor: Color = Color(0xFF2D4A3E),
        secondaryColor: Color = Color(0xFF1A2E23),
        accentColor: Color = Color(0xFF3D5A4F),
        highlightColor: Color = Color(0xFF4A6B5A)
    ) {
        Canvas(modifier = modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Base layer
            drawRect(color = primaryColor, size = size)

            // Generate camo patches
            val random = Random(42) // Fixed seed for consistent pattern
            repeat(150) {
                val x = random.nextFloat() * width
                val y = random.nextFloat() * height
                val w = random.nextFloat() * 80f + 40f
                val h = random.nextFloat() * 60f + 30f
                val rotation = random.nextFloat() * 360f

                val colors = listOf(secondaryColor, accentColor, highlightColor)
                val color = colors[random.nextInt(colors.size)]

                rotate(rotation, pivot = Offset(x + w/2, y + h/2)) {
                    drawRoundRect(
                        color = color,
                        topLeft = Offset(x, y),
                        size = Size(w, h),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
                    )
                }
            }

            // Add tactical grid overlay
            drawTacticalGrid(width, height, Color.White.copy(alpha = 0.05f))
        }
    }

    /**
     * Hexagonal Tactical Grid Background
     * Perfect for: Registration, settings, profile screens
     * Features: Military hex grid with radar-like elements
     */
    @Composable
    fun TacticalHexGrid(
        modifier: Modifier = Modifier,
        backgroundColor: Color = Color(0xFF1B2838),
        gridColor: Color = Color(0xFF2D4A62),
        accentColor: Color = Color(0xFF00FF88),
        pulseColor: Color = Color(0xFFFF6B35)
    ) {
        var animationTime by remember { mutableFloatStateOf(0f) }

        LaunchedEffect(Unit) {
            while (true) {
                animationTime += 0.016f // ~60fps
                kotlinx.coroutines.delay(16)
            }
        }

        Canvas(modifier = modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Dark base
            drawRect(color = backgroundColor, size = size)

            // Draw hex grid
            val hexSize = 40f
            val rows = (height / (hexSize * 1.5f)).toInt() + 2
            val cols = (width / (hexSize * sqrt(3f))).toInt() + 2

            for (row in 0..rows) {
                for (col in 0..cols) {
                    val x = col * hexSize * sqrt(3f) + if (row % 2 == 1) hexSize * sqrt(3f) / 2 else 0f
                    val y = row * hexSize * 1.5f

                    if (x < width + hexSize && y < height + hexSize) {
                        drawHexagon(
                            center = Offset(x, y),
                            size = hexSize,
                            color = gridColor.copy(alpha = 0.3f),
                            strokeWidth = 1f
                        )

                        // Add pulsing effect to random hexagons
                        val pulsePhase = sin(animationTime * 2f + (x + y) * 0.01f)
                        if (pulsePhase > 0.7f) {
                            drawHexagon(
                                center = Offset(x, y),
                                size = hexSize * 0.8f,
                                color = accentColor.copy(alpha = pulsePhase * 0.5f),
                                filled = true
                            )
                        }
                    }
                }
            }

            // Add scanning lines
            val scanY = (sin(animationTime * 0.8f) * 0.5f + 0.5f) * height
            drawLine(
                color = pulseColor.copy(alpha = 0.6f),
                start = Offset(0f, scanY),
                end = Offset(width, scanY),
                strokeWidth = 2f
            )
        }
    }

    /**
     * Command Center Circuit Board Background
     * Perfect for: Task lists, detailed views, command panels
     * Features: Circuit board patterns with data flow animations
     */
    @Composable
    fun CircuitBoardBackground(
        modifier: Modifier = Modifier,
        backgroundColor: Color = Color(0xFF0D1117),
        circuitColor: Color = Color(0xFF1F6FEB),
        nodeColor: Color = Color(0xFF58A6FF),
        dataFlowColor: Color = Color(0xFF7C3AED),
        warningColor: Color = Color(0xFFFF6B35)
    ) {
        var animationPhase by remember { mutableFloatStateOf(0f) }

        LaunchedEffect(Unit) {
            while (true) {
                animationPhase += 0.02f
                kotlinx.coroutines.delay(16)
            }
        }

        Canvas(modifier = modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Dark tech background
            drawRect(color = backgroundColor, size = size)

            // Circuit traces
            drawCircuitTraces(width, height, circuitColor, animationPhase)

            // Connection nodes
            drawConnectionNodes(width, height, nodeColor, animationPhase)

            // Data flow particles
            drawDataFlowParticles(width, height, dataFlowColor, warningColor, animationPhase)

            // Corner command indicators
            drawCornerIndicators(width, height, nodeColor, animationPhase)
        }
    }

    // Helper functions
    private fun DrawScope.drawTacticalGrid(width: Float, height: Float, color: Color) {
        val spacing = 50f
        for (i in 0..(width / spacing).toInt()) {
            drawLine(
                color = color,
                start = Offset(i * spacing, 0f),
                end = Offset(i * spacing, height),
                strokeWidth = 0.5f
            )
        }
        for (i in 0..(height / spacing).toInt()) {
            drawLine(
                color = color,
                start = Offset(0f, i * spacing),
                end = Offset(width, i * spacing),
                strokeWidth = 0.5f
            )
        }
    }

    private fun DrawScope.drawHexagon(
        center: Offset,
        size: Float,
        color: Color,
        strokeWidth: Float = 2f,
        filled: Boolean = false
    ) {
        val path = Path()
        val radius = size / 2f

        for (i in 0..5) {
            val angle = i * PI / 3.0
            val x = center.x + radius * cos(angle).toFloat()
            val y = center.y + radius * sin(angle).toFloat()

            if (i == 0) path.moveTo(x, y)
            else path.lineTo(x, y)
        }
        path.close()

        if (filled) {
            drawPath(path, color)
        } else {
            drawPath(path, color, style = androidx.compose.ui.graphics.drawscope.Stroke(strokeWidth))
        }
    }

    private fun DrawScope.drawCircuitTraces(width: Float, height: Float, color: Color, phase: Float) {
        val gridSize = 80f
        val random = Random(123)

        // Horizontal traces
        for (i in 0..(height / gridSize).toInt()) {
            val y = i * gridSize
            var x = 0f
            while (x < width) {
                val segmentLength = random.nextFloat() * 120f + 60f
                val endX = minOf(x + segmentLength, width)

                val alpha = (sin(phase * 3f + x * 0.01f) * 0.3f + 0.7f).coerceIn(0.2f, 1f)
                drawLine(
                    color = color.copy(alpha = alpha),
                    start = Offset(x, y),
                    end = Offset(endX, y),
                    strokeWidth = 2f
                )
                x = endX + random.nextFloat() * 40f + 20f
            }
        }

        // Vertical traces
        for (i in 0..(width / gridSize).toInt()) {
            val x = i * gridSize
            var y = 0f
            while (y < height) {
                val segmentLength = random.nextFloat() * 100f + 50f
                val endY = minOf(y + segmentLength, height)

                val alpha = (sin(phase * 2f + y * 0.01f) * 0.3f + 0.7f).coerceIn(0.2f, 1f)
                drawLine(
                    color = color.copy(alpha = alpha),
                    start = Offset(x, y),
                    end = Offset(x, endY),
                    strokeWidth = 2f
                )
                y = endY + random.nextFloat() * 50f + 25f
            }
        }
    }

    private fun DrawScope.drawConnectionNodes(width: Float, height: Float, color: Color, phase: Float) {
        val random = Random(456)
        repeat(20) {
            val x = random.nextFloat() * width
            val y = random.nextFloat() * height
            val pulse = sin(phase * 4f + (x + y) * 0.01f) * 0.5f + 0.5f
            val radius = 4f + pulse * 3f

            drawCircle(
                color = color.copy(alpha = 0.8f),
                radius = radius,
                center = Offset(x, y)
            )
            drawCircle(
                color = color.copy(alpha = 0.3f),
                radius = radius * 2f,
                center = Offset(x, y),
                style = androidx.compose.ui.graphics.drawscope.Stroke(1f)
            )
        }
    }

    private fun DrawScope.drawDataFlowParticles(
        width: Float,
        height: Float,
        flowColor: Color,
        warningColor: Color,
        phase: Float
    ) {
        val random = Random(789)
        repeat(15) {
            val baseX = random.nextFloat() * width
            val baseY = random.nextFloat() * height
            val x = baseX + sin(phase * 3f + baseX * 0.01f) * 50f
            val y = baseY + cos(phase * 2f + baseY * 0.01f) * 30f

            val color = if (random.nextFloat() > 0.8f) warningColor else flowColor
            val alpha = (sin(phase * 5f + (baseX + baseY) * 0.02f) * 0.5f + 0.5f).coerceIn(0.3f, 1f)

            drawCircle(
                color = color.copy(alpha = alpha),
                radius = 2f,
                center = Offset(x, y)
            )
        }
    }

    private fun DrawScope.drawCornerIndicators(width: Float, height: Float, color: Color, phase: Float) {
        val cornerSize = 30f
        val alpha = (sin(phase * 2f) * 0.3f + 0.7f).coerceIn(0.4f, 1f)

        // Top-left corner
        drawLine(
            color = color.copy(alpha = alpha),
            start = Offset(20f, 20f),
            end = Offset(20f + cornerSize, 20f),
            strokeWidth = 3f
        )
        drawLine(
            color = color.copy(alpha = alpha),
            start = Offset(20f, 20f),
            end = Offset(20f, 20f + cornerSize),
            strokeWidth = 3f
        )

        // Top-right corner
        drawLine(
            color = color.copy(alpha = alpha),
            start = Offset(width - 20f, 20f),
            end = Offset(width - 20f - cornerSize, 20f),
            strokeWidth = 3f
        )
        drawLine(
            color = color.copy(alpha = alpha),
            start = Offset(width - 20f, 20f),
            end = Offset(width - 20f, 20f + cornerSize),
            strokeWidth = 3f
        )

        // Bottom corners
        drawLine(
            color = color.copy(alpha = alpha),
            start = Offset(20f, height - 20f),
            end = Offset(20f + cornerSize, height - 20f),
            strokeWidth = 3f
        )
        drawLine(
            color = color.copy(alpha = alpha),
            start = Offset(20f, height - 20f),
            end = Offset(20f, height - 20f - cornerSize),
            strokeWidth = 3f
        )

        drawLine(
            color = color.copy(alpha = alpha),
            start = Offset(width - 20f, height - 20f),
            end = Offset(width - 20f - cornerSize, height - 20f),
            strokeWidth = 3f
        )
        drawLine(
            color = color.copy(alpha = alpha),
            start = Offset(width - 20f, height - 20f),
            end = Offset(width - 20f, height - 20f - cornerSize),
            strokeWidth = 3f
        )
    }
}
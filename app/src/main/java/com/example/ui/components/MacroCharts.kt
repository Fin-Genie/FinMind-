package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.macro.CountryMacroProfile
import com.example.domain.macro.MacroDataPoint
import com.example.domain.macro.MacroIndicatorType
import com.example.domain.macro.MacroRegime
import com.example.domain.macro.TimeHorizon
import com.example.ui.theme.ChartGridLine
import com.example.ui.theme.CrimsonRisk
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.LilacAccent
import com.example.ui.theme.LilacDeep
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.SlateSurfaceVariant
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.theme.TextWhite
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Interactive Macro Time-Series Canvas Chart with touch/scrubbing inspector
 */
@Composable
fun InteractiveMacroLineChart(
    dataPoints: List<MacroDataPoint>,
    indicatorType: MacroIndicatorType,
    lineColor: Color = LilacAccent,
    targetBenchmark: Double? = null,
    modifier: Modifier = Modifier
) {
    if (dataPoints.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(SlateSurface, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("No series data available", color = TextTertiary, fontSize = 12.sp)
        }
        return
    }

    var selectedIndex by remember(dataPoints) { mutableIntStateOf(dataPoints.size - 1) }
    val activePoint = dataPoints.getOrNull(selectedIndex) ?: dataPoints.last()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(SlateSurface, RoundedCornerShape(20.dp))
            .border(1.dp, SlateBorder, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        // Scrubbing Header display
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = indicatorType.title.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = lineColor
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = String.format("%.2f", activePoint.value),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Text(
                        text = indicatorType.unit,
                        style = MaterialTheme.typography.titleSmall,
                        color = lineColor,
                        modifier = Modifier.padding(bottom = 3.dp, start = 2.dp)
                    )
                }
            }

            // Period badge & relative delta
            Column(horizontalAlignment = Alignment.End) {
                Box(
                    modifier = Modifier
                        .background(SlateSurfaceVariant, RoundedCornerShape(12.dp))
                        .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = activePoint.periodLabel,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        fontSize = 11.sp
                    )
                }

                if (selectedIndex > 0) {
                    val prevVal = dataPoints[selectedIndex - 1].value
                    val delta = activePoint.value - prevVal
                    val deltaSign = if (delta >= 0) "+" else ""
                    val deltaColor = if (delta >= 0) EmeraldAccent else CrimsonRisk
                    Text(
                        text = "$deltaSign${String.format("%.2f", delta)}% MoM/QoQ",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = deltaColor,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Interactive Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(dataPoints) {
                        detectTapGestures { offset ->
                            val n = dataPoints.size
                            if (n > 1) {
                                val idx = ((offset.x / size.width) * (n - 1)).roundToInt().coerceIn(0, n - 1)
                                selectedIndex = idx
                            }
                        }
                    }
                    .pointerInput(dataPoints) {
                        detectDragGestures { change, _ ->
                            val n = dataPoints.size
                            if (n > 1) {
                                val idx = ((change.position.x / size.width) * (n - 1)).roundToInt().coerceIn(0, n - 1)
                                selectedIndex = idx
                            }
                        }
                    }
            ) {
                val w = size.width
                val h = size.height
                val n = dataPoints.size
                if (n < 2) return@Canvas

                val minRaw = dataPoints.minOf { it.value }
                val maxRaw = dataPoints.maxOf { it.value }
                val padding = max(0.4, (maxRaw - minRaw) * 0.15)
                val minY = min(minRaw - padding, if (minRaw >= 0) 0.0 else minRaw - padding)
                val maxY = maxRaw + padding

                fun mapX(i: Int): Float = (i.toFloat() / (n - 1).toFloat()) * w
                fun mapY(v: Double): Float = h - (((v - minY) / (maxY - minY).coerceAtLeast(0.01)).toFloat() * h)

                // Draw subtle horizontal grid lines (4 lines)
                for (step in 0..4) {
                    val gy = h * (step / 4f)
                    drawLine(
                        color = ChartGridLine,
                        start = Offset(0f, gy),
                        end = Offset(w, gy),
                        strokeWidth = 1f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f))
                    )
                }

                // Target Benchmark Line (if available, e.g. 2.0% inflation target)
                if (targetBenchmark != null && targetBenchmark in minY..maxY) {
                    val targetY = mapY(targetBenchmark)
                    drawLine(
                        color = GoldAccent.copy(alpha = 0.5f),
                        start = Offset(0f, targetY),
                        end = Offset(w, targetY),
                        strokeWidth = 1.5f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f))
                    )
                }

                // Construct area fill and stroke path
                val strokePath = Path()
                val fillPath = Path()

                fillPath.moveTo(mapX(0), h)

                dataPoints.forEachIndexed { i, pt ->
                    val x = mapX(i)
                    val y = mapY(pt.value)
                    if (i == 0) {
                        strokePath.moveTo(x, y)
                        fillPath.lineTo(x, y)
                    } else {
                        strokePath.lineTo(x, y)
                        fillPath.lineTo(x, y)
                    }
                }

                fillPath.lineTo(mapX(n - 1), h)
                fillPath.close()

                // Draw gradient area
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(lineColor.copy(alpha = 0.35f), Color.Transparent),
                        startY = 0f,
                        endY = h
                    )
                )

                // Draw smooth line stroke
                drawPath(
                    path = strokePath,
                    color = lineColor,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )

                // Draw selected scrub line and active point
                val activeX = mapX(selectedIndex)
                val activeY = mapY(activePoint.value)

                drawLine(
                    color = lineColor.copy(alpha = 0.7f),
                    start = Offset(activeX, 0f),
                    end = Offset(activeX, h),
                    strokeWidth = 1.5f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f))
                )

                // Outer halo
                drawCircle(
                    color = lineColor.copy(alpha = 0.3f),
                    radius = 9.dp.toPx(),
                    center = Offset(activeX, activeY)
                )
                // Solid center
                drawCircle(
                    color = lineColor,
                    radius = 5.dp.toPx(),
                    center = Offset(activeX, activeY)
                )
                drawCircle(
                    color = SlateSurface,
                    radius = 2.5.dp.toPx(),
                    center = Offset(activeX, activeY)
                )
            }
        }

        // Timeline range footer
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(dataPoints.first().periodLabel, color = TextTertiary, fontSize = 10.sp)
            Text("Drag on chart to inspect points", color = TextTertiary.copy(alpha = 0.7f), fontSize = 9.sp)
            Text(dataPoints.last().periodLabel, color = TextTertiary, fontSize = 10.sp)
        }
    }
}

/**
 * Dual Indicator Overlay Chart: E.g., Inflation vs Policy Interest Rate to visualize Real Rates
 */
@Composable
fun DualMacroOverlayChart(
    series1: List<MacroDataPoint>,
    series2: List<MacroDataPoint>,
    name1: String,
    name2: String,
    color1: Color = LilacAccent,
    color2: Color = CyanAccent,
    modifier: Modifier = Modifier
) {
    if (series1.isEmpty() || series2.isEmpty()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(SlateSurface, RoundedCornerShape(20.dp))
            .border(1.dp, SlateBorder, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        // Legend & Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "MONETARY TRANSMISSION OVERLAY",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = TextSecondary
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.size(8.dp).background(color1, CircleShape))
                    Text(name1, fontSize = 10.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.size(8.dp).background(color2, CircleShape))
                    Text(name2, fontSize = 10.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                val n = min(series1.size, series2.size)
                if (n < 2) return@Canvas

                val minVal = min(series1.take(n).minOf { it.value }, series2.take(n).minOf { it.value }) - 0.5
                val maxVal = max(series1.take(n).maxOf { it.value }, series2.take(n).maxOf { it.value }) + 0.5

                fun mapX(i: Int): Float = (i.toFloat() / (n - 1).toFloat()) * w
                fun mapY(v: Double): Float = h - (((v - minVal) / (maxVal - minVal).coerceAtLeast(0.01)).toFloat() * h)

                // Grid lines
                for (step in 0..4) {
                    val gy = h * (step / 4f)
                    drawLine(
                        color = ChartGridLine,
                        start = Offset(0f, gy),
                        end = Offset(w, gy),
                        strokeWidth = 1f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f))
                    )
                }

                // Zero line if relevant
                if (0.0 in minVal..maxVal) {
                    val zeroY = mapY(0.0)
                    drawLine(
                        color = TextTertiary.copy(alpha = 0.4f),
                        start = Offset(0f, zeroY),
                        end = Offset(w, zeroY),
                        strokeWidth = 1f
                    )
                }

                // Draw Series 1
                val path1 = Path()
                series1.take(n).forEachIndexed { i, pt ->
                    val x = mapX(i)
                    val y = mapY(pt.value)
                    if (i == 0) path1.moveTo(x, y) else path1.lineTo(x, y)
                }
                drawPath(path = path1, color = color1, style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round))

                // Draw Series 2
                val path2 = Path()
                series2.take(n).forEachIndexed { i, pt ->
                    val x = mapX(i)
                    val y = mapY(pt.value)
                    if (i == 0) path2.moveTo(x, y) else path2.lineTo(x, y)
                }
                drawPath(
                    path = path2,
                    color = color2,
                    style = Stroke(
                        width = 2.5.dp.toPx(),
                        cap = StrokeCap.Round,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f))
                    )
                )

                // Draw end markers
                val last1X = mapX(n - 1)
                val last1Y = mapY(series1[n - 1].value)
                drawCircle(color = color1, radius = 5.dp.toPx(), center = Offset(last1X, last1Y))

                val last2X = mapX(n - 1)
                val last2Y = mapY(series2[n - 1].value)
                drawCircle(color = color2, radius = 5.dp.toPx(), center = Offset(last2X, last2Y))
            }
        }

        // Subtitle interpretation
        val latest1 = series1.lastOrNull()?.value ?: 0.0
        val latest2 = series2.lastOrNull()?.value ?: 0.0
        val spread = latest2 - latest1

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$name1: ${String.format("%.2f", latest1)}% • $name2: ${String.format("%.2f", latest2)}%",
                color = TextSecondary,
                fontSize = 11.sp
            )
            Text(
                text = "Real Rate Spread: ${if (spread >= 0) "+" else ""}${String.format("%.2f", spread)}%",
                color = if (spread >= 0) EmeraldAccent else CrimsonRisk,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * 2D Macro Cycle Quadrant Radar:
 * X-axis: Real GDP Growth, Y-axis: Inflation YoY
 * Shows Goldilocks, Reflation, Stagflation, and Slowdown quadrants with active position pulse.
 */
@Composable
fun MacroCycleQuadrantRadar(
    profile: CountryMacroProfile,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "macro_pulse")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 7f,
        targetValue = 18f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "radarPulse"
    )

    val currentInflation = profile.getLatestValue(MacroIndicatorType.INFLATION)
    val currentGdp = profile.getLatestValue(MacroIndicatorType.GDP_GROWTH)
    val targetInflation = profile.targetInflation

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(SlateSurface, RoundedCornerShape(20.dp))
            .border(1.dp, SlateBorder, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "MACROECONOMIC REGIME QUADRANT",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = LilacAccent
                )
                Text(
                    text = "${profile.flag} ${profile.name} Position",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextWhite
                )
            }

            Box(
                modifier = Modifier
                    .background(LilacDeep.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .border(1.dp, LilacAccent.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "GDP ${currentGdp}% • CPI ${currentInflation}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = LilacAccent,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Axis coordinates (middle splits)
                val midX = w * 0.5f
                val midY = h * 0.5f

                // Draw 4 Quadrants background tints
                // Top-Left: STAGFLATION (Low Growth, High Inflation)
                drawRect(
                    color = CrimsonRisk.copy(alpha = 0.08f),
                    topLeft = Offset(0f, 0f),
                    size = androidx.compose.ui.geometry.Size(midX, midY)
                )
                // Top-Right: REFLATION (High Growth, High Inflation)
                drawRect(
                    color = GoldAccent.copy(alpha = 0.08f),
                    topLeft = Offset(midX, 0f),
                    size = androidx.compose.ui.geometry.Size(midX, midY)
                )
                // Bottom-Left: SLOWDOWN (Low Growth, Low Inflation)
                drawRect(
                    color = CyanAccent.copy(alpha = 0.08f),
                    topLeft = Offset(0f, midY),
                    size = androidx.compose.ui.geometry.Size(midX, midY)
                )
                // Bottom-Right: GOLDILOCKS (High Growth, Low Inflation)
                drawRect(
                    color = EmeraldAccent.copy(alpha = 0.08f),
                    topLeft = Offset(midX, midY),
                    size = androidx.compose.ui.geometry.Size(midX, midY)
                )

                // Divider axes
                drawLine(
                    color = SlateBorder,
                    start = Offset(0f, midY),
                    end = Offset(w, midY),
                    strokeWidth = 1.5f
                )
                drawLine(
                    color = SlateBorder,
                    start = Offset(midX, 0f),
                    end = Offset(midX, h),
                    strokeWidth = 1.5f
                )

                // Map GDP (-2% to +8%) and Inflation (0% to 10%)
                val minGdp = -1.0
                val maxGdp = 8.0
                val minInf = 0.0
                val maxInf = 8.0

                val plotX = (((currentGdp - minGdp) / (maxGdp - minGdp)).toFloat() * w).coerceIn(12f, w - 12f)
                val plotY = (h - (((currentInflation - minInf) / (maxInf - minInf)).toFloat() * h)).coerceIn(12f, h - 12f)

                // Animated glowing pulse for country location
                drawCircle(
                    color = LilacAccent.copy(alpha = 0.25f),
                    radius = pulseRadius.dp.toPx(),
                    center = Offset(plotX, plotY)
                )
                drawCircle(
                    color = LilacAccent,
                    radius = 6.dp.toPx(),
                    center = Offset(plotX, plotY)
                )
                drawCircle(
                    color = TextWhite,
                    radius = 2.5.dp.toPx(),
                    center = Offset(plotX, plotY)
                )
            }

            // Quadrant labels overlay
            Box(modifier = Modifier.fillMaxSize().padding(10.dp)) {
                // Top-Left: Stagflation
                Text(
                    text = "STAGFLATION\n(High CPI, Weak GDP)",
                    color = CrimsonRisk.copy(alpha = 0.75f),
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.TopStart)
                )
                // Top-Right: Reflation
                Text(
                    text = "REFLATION\n(High CPI, Strong GDP)",
                    color = GoldAccent.copy(alpha = 0.75f),
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.TopEnd),
                    textAlign = androidx.compose.ui.text.style.TextAlign.End
                )
                // Bottom-Left: Slowdown
                Text(
                    text = "DISINFLATION SLOWDOWN\n(Low CPI, Weak GDP)",
                    color = CyanAccent.copy(alpha = 0.75f),
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.BottomStart)
                )
                // Bottom-Right: Goldilocks
                Text(
                    text = "GOLDILOCKS\n(Target CPI, Resilient GDP)",
                    color = EmeraldAccent.copy(alpha = 0.85f),
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.BottomEnd),
                    textAlign = androidx.compose.ui.text.style.TextAlign.End
                )
            }
        }
    }
}

/**
 * Cross-Country Peer Ranking Chart: Compares all 10 economies on the active indicator
 */
@Composable
fun GlobalPeerComparisonBarChart(
    allCountries: List<CountryMacroProfile>,
    activeIndicator: MacroIndicatorType,
    selectedCountryId: String,
    onSelectCountry: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val sortedCountries = remember(allCountries, activeIndicator) {
        allCountries.sortedByDescending { it.getLatestValue(activeIndicator) }
    }

    val maxVal = max(0.1, sortedCountries.maxOfOrNull { it.getLatestValue(activeIndicator) } ?: 5.0)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(SlateSurface, RoundedCornerShape(20.dp))
            .border(1.dp, SlateBorder, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "GLOBAL SOVEREIGN PEER MATRIX",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = LilacAccent
                )
                Text(
                    text = "Ranked by ${activeIndicator.shortLabel}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextWhite
                )
            }
            Text(
                text = "Tap to switch",
                color = TextTertiary,
                fontSize = 10.sp
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            sortedCountries.forEach { country ->
                val isSelected = country.id == selectedCountryId
                val value = country.getLatestValue(activeIndicator)
                val ratio = (value / maxVal).toFloat().coerceIn(0.04f, 1f)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (isSelected) LilacDeep.copy(alpha = 0.45f) else Color.Transparent,
                            RoundedCornerShape(8.dp)
                        )
                        .border(
                            1.dp,
                            if (isSelected) LilacAccent.copy(alpha = 0.5f) else Color.Transparent,
                            RoundedCornerShape(8.dp)
                        )
                        .pointerInput(country.id) {
                            detectTapGestures { onSelectCountry(country.id) }
                        }
                        .padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(country.flag, fontSize = 16.sp, modifier = Modifier.width(26.dp))
                    Text(
                        text = country.name,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) TextWhite else TextSecondary,
                        modifier = Modifier.width(95.dp),
                        maxLines = 1
                    )

                    // Bar container
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(12.dp)
                            .background(SlateSurfaceVariant, RoundedCornerShape(6.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(ratio)
                                .height(12.dp)
                                .background(
                                    if (isSelected) {
                                        Brush.horizontalGradient(listOf(LilacAccent, EmeraldAccent))
                                    } else {
                                        Brush.horizontalGradient(listOf(CyanAccent.copy(alpha = 0.7f), CyanAccent))
                                    },
                                    RoundedCornerShape(6.dp)
                                )
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "${String.format("%.1f", value)}${activeIndicator.unit}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) LilacAccent else TextPrimary,
                        modifier = Modifier.width(44.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.End
                    )
                }
            }
        }
    }
}

/**
 * KPI Stat Card for Macro Overview
 */
@Composable
fun MacroKpiCard(
    indicatorType: MacroIndicatorType,
    currentValue: Double,
    delta: Double,
    benchmarkLabel: String,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val deltaSign = if (delta >= 0) "+" else ""
    val deltaColor = if (delta >= 0) EmeraldAccent else CrimsonRisk

    Column(
        modifier = modifier
            .background(SlateSurface, RoundedCornerShape(16.dp))
            .border(1.dp, SlateBorder, RoundedCornerShape(16.dp))
            .pointerInput(indicatorType) {
                detectTapGestures { onClick() }
            }
            .padding(14.dp)
    ) {
        Text(
            text = indicatorType.shortLabel.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = accentColor
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = String.format("%.2f", currentValue),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
            Text(
                text = indicatorType.unit,
                style = MaterialTheme.typography.labelSmall,
                color = accentColor,
                modifier = Modifier.padding(bottom = 2.dp, start = 2.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$deltaSign${String.format("%.2f", delta)}%",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = deltaColor
            )
            Text(
                text = benchmarkLabel,
                fontSize = 9.sp,
                color = TextTertiary
            )
        }
    }
}

package com.example.ui.components

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
import androidx.compose.ui.geometry.Size
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
import com.example.domain.math.DistributionBin
import com.example.domain.math.TrajectoryStep
import com.example.ui.theme.ChartGridLine
import com.example.ui.theme.CrimsonRisk
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.LilacAccent
import com.example.ui.theme.LilacDeep
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.PurpleGreeks
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
 * Interactive Monte Carlo Trajectory Fan Chart with touch/scrub inspector
 */
@Composable
fun MonteCarloTrajectoryFanChart(
    trajectorySteps: List<TrajectoryStep>,
    samplePaths: List<List<Double>>,
    targetGoal: Double,
    totalInvested: Double,
    modifier: Modifier = Modifier
) {
    if (trajectorySteps.isEmpty()) return

    var selectedIndex by remember(trajectorySteps) {
        mutableIntStateOf(trajectorySteps.size - 1)
    }

    val activeStep = trajectorySteps.getOrNull(selectedIndex) ?: trajectorySteps.last()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(SlateSurface, RoundedCornerShape(20.dp))
            .border(1.dp, SlateBorder, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        // Scrub Inspection Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "PROJECTED PROBABILITY FAN",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = LilacAccent
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "\$${String.format("%,.0f", activeStep.p50)}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Text(
                        text = " (Median P50)",
                        style = MaterialTheme.typography.labelSmall,
                        color = LilacAccent,
                        modifier = Modifier.padding(bottom = 2.dp, start = 4.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .background(SlateSurfaceVariant, RoundedCornerShape(12.dp))
                    .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Year ${String.format("%.1f", activeStep.year)}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            }
        }

        // Percentile Strip: P5 vs P95
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "5th % Floor: \$${String.format("%,.0f", activeStep.p5)}",
                fontSize = 10.sp,
                color = CrimsonRisk,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "95th % Bull: \$${String.format("%,.0f", activeStep.p95)}",
                fontSize = 10.sp,
                color = EmeraldAccent,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Interactive Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(trajectorySteps) {
                        detectTapGestures { offset ->
                            val n = trajectorySteps.size
                            if (n > 1) {
                                val idx = ((offset.x / size.width) * (n - 1)).roundToInt().coerceIn(0, n - 1)
                                selectedIndex = idx
                            }
                        }
                    }
                    .pointerInput(trajectorySteps) {
                        detectDragGestures { change, _ ->
                            val n = trajectorySteps.size
                            if (n > 1) {
                                val idx = ((change.position.x / size.width) * (n - 1)).roundToInt().coerceIn(0, n - 1)
                                selectedIndex = idx
                            }
                        }
                    }
            ) {
                val w = size.width
                val h = size.height
                val n = trajectorySteps.size
                if (n < 2) return@Canvas

                val maxWealth = max(targetGoal * 1.1, trajectorySteps.maxOf { it.p95 } * 1.05)
                val minWealth = 0.0

                fun mapX(i: Int): Float = (i.toFloat() / (n - 1).toFloat()) * w
                fun mapY(v: Double): Float = h - (((v - minWealth) / (maxWealth - minWealth).coerceAtLeast(0.01)).toFloat() * h)

                // Grid lines (4 horizontal)
                for (step in 1..4) {
                    val gy = h * (step / 5f)
                    drawLine(
                        color = ChartGridLine,
                        start = Offset(0f, gy),
                        end = Offset(w, gy),
                        strokeWidth = 1f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f))
                    )
                }

                // Shaded 5th to 95th Percentile Confidence Corridor
                val path90 = Path()
                // Top curve (P95)
                trajectorySteps.forEachIndexed { i, step ->
                    val x = mapX(i)
                    val y = mapY(step.p95)
                    if (i == 0) path90.moveTo(x, y) else path90.lineTo(x, y)
                }
                // Bottom curve back (P5)
                for (i in (n - 1) downTo 0) {
                    val step = trajectorySteps[i]
                    val x = mapX(i)
                    val y = mapY(step.p5)
                    path90.lineTo(x, y)
                }
                path90.close()
                drawPath(
                    path = path90,
                    brush = Brush.verticalGradient(
                        colors = listOf(PurpleGreeks.copy(alpha = 0.20f), LilacDeep.copy(alpha = 0.10f)),
                        startY = 0f,
                        endY = h
                    )
                )

                // Shaded 25th to 75th Percentile Interquartile Range
                val path50 = Path()
                trajectorySteps.forEachIndexed { i, step ->
                    val x = mapX(i)
                    val y = mapY(step.p75)
                    if (i == 0) path50.moveTo(x, y) else path50.lineTo(x, y)
                }
                for (i in (n - 1) downTo 0) {
                    val step = trajectorySteps[i]
                    val x = mapX(i)
                    val y = mapY(step.p25)
                    path50.lineTo(x, y)
                }
                path50.close()
                drawPath(
                    path = path50,
                    brush = Brush.verticalGradient(
                        colors = listOf(LilacAccent.copy(alpha = 0.35f), LilacDeep.copy(alpha = 0.25f)),
                        startY = 0f,
                        endY = h
                    )
                )

                // Draw background thin stochastic sample paths
                samplePaths.forEach { path ->
                    val pathObj = Path()
                    val pLen = min(n, path.size)
                    for (i in 0 until pLen) {
                        val x = mapX(i)
                        val y = mapY(path[i])
                        if (i == 0) pathObj.moveTo(x, y) else pathObj.lineTo(x, y)
                    }
                    drawPath(
                        path = pathObj,
                        color = Color.White.copy(alpha = 0.08f),
                        style = Stroke(width = 1.dp.toPx())
                    )
                }

                // Target Goal Horizontal Threshold Line
                if (targetGoal in minWealth..maxWealth) {
                    val goalY = mapY(targetGoal)
                    drawLine(
                        color = GoldAccent,
                        start = Offset(0f, goalY),
                        end = Offset(w, goalY),
                        strokeWidth = 1.5f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f))
                    )
                }

                // Total Invested Baseline Line
                if (totalInvested in minWealth..maxWealth) {
                    val invY = mapY(totalInvested)
                    drawLine(
                        color = TextTertiary.copy(alpha = 0.6f),
                        start = Offset(0f, invY),
                        end = Offset(w, invY),
                        strokeWidth = 1f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f))
                    )
                }

                // Median P50 Line (Solid Lilac)
                val medianPath = Path()
                trajectorySteps.forEachIndexed { i, step ->
                    val x = mapX(i)
                    val y = mapY(step.p50)
                    if (i == 0) medianPath.moveTo(x, y) else medianPath.lineTo(x, y)
                }
                drawPath(
                    path = medianPath,
                    color = LilacAccent,
                    style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                )

                // Active Scrub Cursor Indicator
                val activeX = mapX(selectedIndex)
                val activeP50Y = mapY(activeStep.p50)

                drawLine(
                    color = LilacAccent.copy(alpha = 0.8f),
                    start = Offset(activeX, 0f),
                    end = Offset(activeX, h),
                    strokeWidth = 1.5f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f))
                )

                // Indicator Dot
                drawCircle(color = LilacAccent.copy(alpha = 0.3f), radius = 9.dp.toPx(), center = Offset(activeX, activeP50Y))
                drawCircle(color = LilacAccent, radius = 5.dp.toPx(), center = Offset(activeX, activeP50Y))
                drawCircle(color = TextWhite, radius = 2.dp.toPx(), center = Offset(activeX, activeP50Y))
            }
        }

        // Legend
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                LegendBadge(color = LilacAccent, label = "Median (50%)")
                LegendBadge(color = PurpleGreeks.copy(alpha = 0.6f), label = "90% Corridor")
                LegendBadge(color = GoldAccent, label = "Goal")
            }
            Text("Drag to inspect timeline", color = TextTertiary, fontSize = 9.sp)
        }
    }
}

/**
 * Interactive Terminal Return Probability Distribution Histogram & PDF curve
 */
@Composable
fun ReturnDistributionHistogramChart(
    distributionBins: List<DistributionBin>,
    targetGoal: Double,
    totalInvested: Double,
    medianWealth: Double,
    modifier: Modifier = Modifier
) {
    if (distributionBins.isEmpty()) return

    var selectedBinIndex by remember(distributionBins) {
        mutableIntStateOf(distributionBins.size / 2)
    }

    val activeBin = distributionBins.getOrNull(selectedBinIndex) ?: distributionBins.first()

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
                    text = "TERMINAL RETURN PROBABILITY DISTRIBUTION",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = LilacAccent
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "${String.format("%.1f", activeBin.probability)}%",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Text(
                        text = " Probability Mass",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        modifier = Modifier.padding(bottom = 2.dp, start = 4.dp)
                    )
                }
            }

            // Cumulative percentile badge
            Box(
                modifier = Modifier
                    .background(SlateSurfaceVariant, RoundedCornerShape(12.dp))
                    .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${String.format("%.1f", activeBin.cumulativeProbability)}th Percentile",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = LilacAccent
                )
            }
        }

        // Active Bin Value range
        Text(
            text = "Interval: \$${String.format("%,.0f", activeBin.minVal)} to \$${String.format("%,.0f", activeBin.maxVal)} (${if (activeBin.returnPct >= 0) "+" else ""}${String.format("%.1f", activeBin.returnPct)}% Return)",
            fontSize = 11.sp,
            color = TextSecondary,
            modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
        )

        // Histogram Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(distributionBins) {
                        detectTapGestures { offset ->
                            val n = distributionBins.size
                            if (n > 0) {
                                val idx = ((offset.x / size.width) * n).toInt().coerceIn(0, n - 1)
                                selectedBinIndex = idx
                            }
                        }
                    }
                    .pointerInput(distributionBins) {
                        detectDragGestures { change, _ ->
                            val n = distributionBins.size
                            if (n > 0) {
                                val idx = ((change.position.x / size.width) * n).toInt().coerceIn(0, n - 1)
                                selectedBinIndex = idx
                            }
                        }
                    }
            ) {
                val w = size.width
                val h = size.height
                val n = distributionBins.size
                if (n < 2) return@Canvas

                val maxProb = max(1.0, distributionBins.maxOf { it.probability } * 1.15)
                val barWidth = (w / n.toFloat()) * 0.85f
                val barSpacing = (w / n.toFloat()) * 0.15f

                // Draw horizontal probability grid
                for (step in 1..3) {
                    val gy = h * (step / 4f)
                    drawLine(
                        color = ChartGridLine,
                        start = Offset(0f, gy),
                        end = Offset(w, gy),
                        strokeWidth = 1f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f))
                    )
                }

                // Draw Histogram Bars
                distributionBins.forEachIndexed { i, bin ->
                    val isSelected = i == selectedBinIndex
                    val barH = (bin.probability / maxProb).toFloat() * h
                    val x = i * (barWidth + barSpacing) + (barSpacing / 2f)
                    val y = h - barH

                    // Semantic bar color based on outcome
                    val barColor = when {
                        bin.centerVal < totalInvested -> CrimsonRisk
                        bin.centerVal >= targetGoal -> EmeraldAccent
                        else -> CyanAccent
                    }

                    // Bar fill
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                if (isSelected) barColor else barColor.copy(alpha = 0.75f),
                                if (isSelected) barColor.copy(alpha = 0.4f) else barColor.copy(alpha = 0.2f)
                            ),
                            startY = y,
                            endY = h
                        ),
                        topLeft = Offset(x, y),
                        size = Size(barWidth, barH)
                    )

                    // Top cap on bar
                    drawLine(
                        color = if (isSelected) Color.White else barColor,
                        start = Offset(x, y),
                        end = Offset(x + barWidth, y),
                        strokeWidth = if (isSelected) 2.5f else 1.5f
                    )
                }

                // Smooth PDF curve approximation
                val pdfPath = Path()
                distributionBins.forEachIndexed { i, bin ->
                    val barH = (bin.probability / maxProb).toFloat() * h
                    val x = i * (barWidth + barSpacing) + (barWidth / 2f)
                    val y = h - barH
                    if (i == 0) pdfPath.moveTo(x, y) else pdfPath.lineTo(x, y)
                }
                drawPath(
                    path = pdfPath,
                    color = LilacAccent,
                    style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                )

                // Median Marker Line
                val medBin = distributionBins.minByOrNull { kotlin.math.abs(it.centerVal - medianWealth) }
                medBin?.let {
                    val idx = distributionBins.indexOf(it)
                    val medX = idx * (barWidth + barSpacing) + (barWidth / 2f)
                    drawLine(
                        color = LilacAccent,
                        start = Offset(medX, 0f),
                        end = Offset(medX, h),
                        strokeWidth = 1.5f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 4f))
                    )
                }
            }
        }

        // Legend & Color classification
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                LegendBadge(color = CrimsonRisk, label = "Loss (<0%)")
                LegendBadge(color = CyanAccent, label = "Positive")
                LegendBadge(color = EmeraldAccent, label = "Goal Met")
            }
            Text("Tap bars to inspect distribution", color = TextTertiary, fontSize = 9.sp)
        }
    }
}

@Composable
private fun LegendBadge(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            color = TextSecondary,
            fontWeight = FontWeight.Medium
        )
    }
}

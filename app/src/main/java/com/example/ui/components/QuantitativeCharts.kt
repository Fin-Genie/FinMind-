package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.math.PortfolioPoint
import com.example.domain.math.SensitivityCell
import com.example.ui.theme.ChartGridLine
import com.example.ui.theme.CrimsonLight
import com.example.ui.theme.CrimsonRisk
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.LilacAccent
import com.example.ui.theme.LilacDeep
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.PurpleGreeks
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Markowitz Efficient Frontier Canvas Chart
 */
@Composable
fun EfficientFrontierChart(
    simulatedPoints: List<PortfolioPoint>,
    frontierCurve: List<PortfolioPoint>,
    currentPoint: PortfolioPoint,
    tangencyPoint: PortfolioPoint,
    minVariancePoint: PortfolioPoint,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(230.dp)
            .background(SlateSurface, RoundedCornerShape(16.dp))
            .border(1.dp, SlateBorder, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            if (simulatedPoints.isEmpty()) return@Canvas

            val minVol = (simulatedPoints.minOfOrNull { it.volatility } ?: 0.05) * 0.85
            val maxVol = (simulatedPoints.maxOfOrNull { it.volatility } ?: 0.40) * 1.10
            val minRet = (simulatedPoints.minOfOrNull { it.expectedReturn } ?: 0.02) * 0.85
            val maxRet = (simulatedPoints.maxOfOrNull { it.expectedReturn } ?: 0.35) * 1.10

            fun mapX(vol: Double): Float = ((vol - minVol) / (maxVol - minVol).coerceAtLeast(0.01)).toFloat() * w
            fun mapY(ret: Double): Float = h - (((ret - minRet) / (maxRet - minRet).coerceAtLeast(0.01)).toFloat() * h)

            // Draw grid lines
            for (i in 1..4) {
                val gridY = (h / 5f) * i
                drawLine(
                    color = ChartGridLine,
                    start = Offset(0f, gridY),
                    end = Offset(w, gridY),
                    strokeWidth = 1f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f))
                )
                val gridX = (w / 5f) * i
                drawLine(
                    color = ChartGridLine,
                    start = Offset(gridX, 0f),
                    end = Offset(gridX, h),
                    strokeWidth = 1f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f))
                )
            }

            // Draw Monte Carlo cloud points
            val step = max(1, simulatedPoints.size / 350)
            for (i in simulatedPoints.indices step step) {
                val pt = simulatedPoints[i]
                val px = mapX(pt.volatility)
                val py = mapY(pt.expectedReturn)
                val sharpeRatioNorm = ((pt.sharpeRatio - 0.2) / 1.2).coerceIn(0.0, 1.0).toFloat()
                val pointColor = Color(
                    red = (0.5f + 0.3f * sharpeRatioNorm).coerceIn(0f, 1f),
                    green = (0.4f + 0.3f * sharpeRatioNorm).coerceIn(0f, 1f),
                    blue = (0.9f + 0.1f * sharpeRatioNorm).coerceIn(0f, 1f),
                    alpha = 0.35f
                )
                drawCircle(color = pointColor, radius = 3.5f, center = Offset(px, py))
            }

            // Draw Efficient Frontier Curve
            if (frontierCurve.size >= 2) {
                val curvePath = Path()
                curvePath.moveTo(mapX(frontierCurve[0].volatility), mapY(frontierCurve[0].expectedReturn))
                for (i in 1 until frontierCurve.size) {
                    val p = frontierCurve[i]
                    curvePath.lineTo(mapX(p.volatility), mapY(p.expectedReturn))
                }
                drawPath(
                    path = curvePath,
                    color = LilacAccent,
                    style = Stroke(width = 3.5f, cap = StrokeCap.Round)
                )
            }

            // Draw Capital Allocation Line (CAL)
            val rfY = mapY(0.042)
            val tanX = mapX(tangencyPoint.volatility)
            val tanY = mapY(tangencyPoint.expectedReturn)
            drawLine(
                color = LilacAccent.copy(alpha = 0.6f),
                start = Offset(mapX(minVol), rfY),
                end = Offset(tanX + (tanX - mapX(minVol)) * 0.4f, tanY - (rfY - tanY) * 0.4f),
                strokeWidth = 2f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f))
            )

            // Tangency Point (Max Sharpe) - Lilac Ring
            drawCircle(color = LilacAccent.copy(alpha = 0.3f), radius = 14f, center = Offset(tanX, tanY))
            drawCircle(color = LilacAccent, radius = 7f, center = Offset(tanX, tanY))
            drawCircle(color = ObsidianBg, radius = 3.5f, center = Offset(tanX, tanY))

            // Minimum Variance Point - Emerald Ring
            val minX = mapX(minVariancePoint.volatility)
            val minY = mapY(minVariancePoint.expectedReturn)
            drawCircle(color = EmeraldAccent.copy(alpha = 0.3f), radius = 12f, center = Offset(minX, minY))
            drawCircle(color = EmeraldAccent, radius = 6f, center = Offset(minX, minY))

            // Current Portfolio Point - Crimson Pulse Ring
            val curX = mapX(currentPoint.volatility)
            val curY = mapY(currentPoint.expectedReturn)
            drawCircle(color = CrimsonRisk.copy(alpha = 0.35f), radius = 13f, center = Offset(curX, curY))
            drawCircle(color = CrimsonRisk, radius = 6.5f, center = Offset(curX, curY))
        }

        // Floating Legend
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .background(ObsidianBg.copy(alpha = 0.9f), RoundedCornerShape(8.dp))
                .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LegendItem(color = LilacAccent, label = "Max Sharpe (${String.format("%.2f", tangencyPoint.sharpeRatio)})")
            LegendItem(color = EmeraldAccent, label = "Min Vol (${String.format("%.1f", minVariancePoint.volatility * 100)}%)")
            LegendItem(color = CrimsonRisk, label = "Current (${String.format("%.1f", currentPoint.expectedReturn * 100)}%)")
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary
        )
    }
}

/**
 * GARCH(1,1) Volatility Cone & Forecast Chart
 */
@Composable
fun GarchVolatilityChart(
    historicalVolatilities: List<Double>,
    forecastedVolatilities: List<Double>,
    unconditionalVol: Double,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(SlateSurface, RoundedCornerShape(16.dp))
            .border(1.dp, SlateBorder, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            val allVols = historicalVolatilities + forecastedVolatilities
            if (allVols.isEmpty()) return@Canvas

            val minV = (allVols.minOrNull() ?: 0.10) * 0.85
            val maxV = (allVols.maxOrNull() ?: 0.50) * 1.15

            fun mapY(vol: Double): Float = h - (((vol - minV) / (maxV - minV).coerceAtLeast(0.01)).toFloat() * h)

            // Draw grid
            for (i in 1..3) {
                val gridY = (h / 4f) * i
                drawLine(color = ChartGridLine, start = Offset(0f, gridY), end = Offset(w, gridY), strokeWidth = 1f)
            }

            // Draw Unconditional Long-Term Volatility Asymptote
            val uncondY = mapY(unconditionalVol)
            drawLine(
                color = LilacAccent.copy(alpha = 0.6f),
                start = Offset(0f, uncondY),
                end = Offset(w, uncondY),
                strokeWidth = 2f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f))
            )

            val totalPoints = historicalVolatilities.size + forecastedVolatilities.size
            val xStep = w / (totalPoints - 1).coerceAtLeast(1)

            // Historical Path
            val histPath = Path()
            if (historicalVolatilities.isNotEmpty()) {
                histPath.moveTo(0f, mapY(historicalVolatilities[0]))
                for (i in 1 until historicalVolatilities.size) {
                    val x = i * xStep
                    val y = mapY(historicalVolatilities[i])
                    histPath.lineTo(x, y)
                }
                drawPath(
                    path = histPath,
                    color = CyanAccent,
                    style = Stroke(width = 3f, cap = StrokeCap.Round)
                )
            }

            // Forecast Cone & Trajectory
            if (forecastedVolatilities.isNotEmpty() && historicalVolatilities.isNotEmpty()) {
                val startIdx = historicalVolatilities.size - 1
                val startX = startIdx * xStep
                val startY = mapY(historicalVolatilities.last())

                val coneUpperPath = Path()
                val coneLowerPath = Path()
                coneUpperPath.moveTo(startX, startY)
                coneLowerPath.moveTo(startX, startY)

                val forecastPath = Path()
                forecastPath.moveTo(startX, startY)

                for (i in forecastedVolatilities.indices) {
                    val idx = startIdx + 1 + i
                    val x = idx * xStep
                    val fVol = forecastedVolatilities[i]
                    val y = mapY(fVol)
                    forecastPath.lineTo(x, y)

                    val coneSpread = (i + 1) * 0.012
                    coneUpperPath.lineTo(x, mapY(fVol + coneSpread))
                    coneLowerPath.lineTo(x, mapY(fVol - coneSpread))
                }

                drawPath(
                    path = forecastPath,
                    color = EmeraldAccent,
                    style = Stroke(
                        width = 3.5f,
                        cap = StrokeCap.Round,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f))
                    )
                )

                drawLine(
                    color = TextTertiary.copy(alpha = 0.5f),
                    start = Offset(startX, 0f),
                    end = Offset(startX, h),
                    strokeWidth = 1.5f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f))
                )
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .background(ObsidianBg.copy(alpha = 0.9f), RoundedCornerShape(8.dp))
                .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LegendItem(color = CyanAccent, label = "Historical Vol")
            LegendItem(color = EmeraldAccent, label = "GARCH(1,1) Forecast")
            LegendItem(color = LilacAccent, label = "Asymptote (${String.format("%.1f", unconditionalVol * 100)}%)")
        }
    }
}

/**
 * Cointegration Spread & Z-Score Mean Reversion Chart
 */
@Composable
fun CointegrationSpreadChart(
    zScores: List<Double>,
    currentZScore: Double,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(SlateSurface, RoundedCornerShape(16.dp))
            .border(1.dp, SlateBorder, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            if (zScores.isEmpty()) return@Canvas

            val minZ = min(-3.0, (zScores.minOrNull() ?: -2.5) * 1.1)
            val maxZ = max(3.0, (zScores.maxOrNull() ?: 2.5) * 1.1)

            fun mapY(z: Double): Float = h - (((z - minZ) / (maxZ - minZ)).toFloat() * h)

            // Zero Mean line
            val zeroY = mapY(0.0)
            drawLine(color = TextSecondary.copy(alpha = 0.6f), start = Offset(0f, zeroY), end = Offset(w, zeroY), strokeWidth = 1.5f)

            // +2.0 Upper Threshold
            val upperY = mapY(2.0)
            drawLine(
                color = CrimsonRisk.copy(alpha = 0.7f),
                start = Offset(0f, upperY),
                end = Offset(w, upperY),
                strokeWidth = 1.5f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 4f))
            )

            // -2.0 Lower Threshold
            val lowerY = mapY(-2.0)
            drawLine(
                color = EmeraldAccent.copy(alpha = 0.7f),
                start = Offset(0f, lowerY),
                end = Offset(w, lowerY),
                strokeWidth = 1.5f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 4f))
            )

            // Z-Score line
            val xStep = w / (zScores.size - 1).coerceAtLeast(1)
            val zPath = Path()
            zPath.moveTo(0f, mapY(zScores[0]))
            for (i in 1 until zScores.size) {
                zPath.lineTo(i * xStep, mapY(zScores[i]))
            }

            drawPath(
                path = zPath,
                brush = Brush.verticalGradient(
                    colors = listOf(CrimsonRisk, LilacAccent, EmeraldAccent),
                    startY = upperY,
                    endY = lowerY
                ),
                style = Stroke(width = 3f, cap = StrokeCap.Round)
            )

            val lastX = (zScores.size - 1) * xStep
            val lastY = mapY(currentZScore)
            val curColor = if (currentZScore < -1.8) EmeraldAccent else if (currentZScore > 1.8) CrimsonRisk else LilacAccent
            drawCircle(color = curColor, radius = 6f, center = Offset(lastX, lastY))
        }

        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .background(ObsidianBg.copy(alpha = 0.9f), RoundedCornerShape(8.dp))
                .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LegendItem(color = CrimsonRisk, label = "+2.0σ Sell Zone")
            LegendItem(color = LilacAccent, label = "Z-Score (${String.format("%.2f", currentZScore)})")
            LegendItem(color = EmeraldAccent, label = "-2.0σ Buy Zone")
        }
    }
}

/**
 * DCF Valuation 2D Sensitivity Heatmap
 */
@Composable
fun DcfSensitivityHeatmap(
    matrix: List<List<SensitivityCell>>,
    currentPrice: Double,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(SlateSurface, RoundedCornerShape(16.dp))
            .border(1.dp, SlateBorder, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "WACC vs Perpetual Growth Sensitivity Grid",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Text(
                text = "Spot: $${String.format("%.2f", currentPrice)}",
                style = MaterialTheme.typography.labelSmall,
                color = LilacAccent,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (matrix.isNotEmpty() && matrix[0].isNotEmpty()) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.weight(1.1f), contentAlignment = Alignment.Center) {
                    Text(text = "WACC \\ g", style = MaterialTheme.typography.labelSmall, color = TextTertiary, fontSize = 9.sp)
                }
                for (cell in matrix[0]) {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Text(
                            text = "${String.format("%.1f", cell.perpetualGrowthPct)}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            for (row in matrix) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1.1f), contentAlignment = Alignment.CenterStart) {
                        Text(
                            text = "${String.format("%.2f", row[0].discountRatePct)}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }

                    for (cell in row) {
                        val margin = cell.marginOfSafetyPct
                        val cellBg = when {
                            margin > 15.0 -> EmeraldAccent.copy(alpha = 0.25f)
                            margin > 0.0 -> EmeraldAccent.copy(alpha = 0.12f)
                            margin > -10.0 -> LilacAccent.copy(alpha = 0.18f)
                            else -> CrimsonRisk.copy(alpha = 0.20f)
                        }
                        val textColor = when {
                            margin > 15.0 -> EmeraldLight
                            margin > 0.0 -> EmeraldAccent
                            margin > -10.0 -> LilacAccent
                            else -> CrimsonLight
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(32.dp)
                                .padding(horizontal = 2.dp)
                                .background(cellBg, RoundedCornerShape(6.dp))
                                .border(0.5.dp, SlateBorder.copy(alpha = 0.6f), RoundedCornerShape(6.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$${String.format("%.0f", cell.intrinsicValuePerShare)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = textColor
                                )
                                Text(
                                    text = "${if (margin >= 0) "+" else ""}${String.format("%.0f", margin)}%",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 8.sp,
                                    color = textColor.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Option Expiration Payoff & Greeks Diagram
 */
@Composable
fun OptionPayoffChart(
    spotPoints: List<Double>,
    callPayoffs: List<Double>,
    putPayoffs: List<Double>,
    strikeK: Double,
    isCallSelected: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(190.dp)
            .background(SlateSurface, RoundedCornerShape(16.dp))
            .border(1.dp, SlateBorder, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            val activePayoffs = if (isCallSelected) callPayoffs else putPayoffs
            if (activePayoffs.isEmpty() || spotPoints.isEmpty()) return@Canvas

            val minP = min(activePayoffs.minOrNull() ?: -10.0, -5.0) * 1.15
            val maxP = max(activePayoffs.maxOrNull() ?: 20.0, 10.0) * 1.15

            val minSpot = spotPoints.first()
            val maxSpot = spotPoints.last()

            fun mapX(spot: Double): Float = (((spot - minSpot) / (maxSpot - minSpot)).toFloat() * w)
            fun mapY(pnl: Double): Float = h - (((pnl - minP) / (maxP - minP)).toFloat() * h)

            val zeroY = mapY(0.0)
            drawLine(color = TextSecondary.copy(alpha = 0.5f), start = Offset(0f, zeroY), end = Offset(w, zeroY), strokeWidth = 1.5f)

            val strikeX = mapX(strikeK)
            drawLine(
                color = LilacAccent.copy(alpha = 0.7f),
                start = Offset(strikeX, 0f),
                end = Offset(strikeX, h),
                strokeWidth = 1.5f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f))
            )

            val path = Path()
            path.moveTo(mapX(spotPoints[0]), mapY(activePayoffs[0]))
            for (i in 1 until spotPoints.size) {
                path.lineTo(mapX(spotPoints[i]), mapY(activePayoffs[i]))
            }

            val payoffColor = if (isCallSelected) EmeraldAccent else LilacAccent
            drawPath(
                path = path,
                color = payoffColor,
                style = Stroke(width = 3.5f, cap = StrokeCap.Round)
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .background(ObsidianBg.copy(alpha = 0.9f), RoundedCornerShape(8.dp))
                .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LegendItem(
                color = if (isCallSelected) EmeraldAccent else LilacAccent,
                label = "${if (isCallSelected) "Long Call" else "Long Put"} Expiration Payoff"
            )
            LegendItem(color = LilacAccent, label = "Strike K = $$strikeK")
        }
    }
}

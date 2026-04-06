import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.uikit.compose.components.composites.button.Button
import com.uikit.compose.components.primitives.text.TextBlock
import com.uikit.compose.theme.parseColor
import com.uikit.components.primitives.text.TextBlockVariant
import com.uikit.foundation.VisualVariant
import com.uikit.tokens.DesignTokens

@Composable
internal fun MotionShowcase(tokens: DesignTokens) {
	var animate by remember { mutableStateOf(false) }
	val durations =
		listOf(
			"Instant" to tokens.motion.durationInstant,
			"Fast" to tokens.motion.durationFast,
			"Normal" to tokens.motion.durationNormal,
			"Slow" to tokens.motion.durationSlow,
			"Slower" to tokens.motion.durationSlower,
		)

	ShowcaseSection("Motion", tokens) {
		Button(
			text = if (animate) "Reset" else "Play Animation",
			variant = VisualVariant.Soft,
			onClick = { animate = !animate },
		)
		Spacer(Modifier.height(tokens.spacing.lg.dp))
		Column(verticalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp)) {
			durations.forEach { (label, ms) ->
				val offsetX by animateDpAsState(
					targetValue = if (animate) 200.dp else 0.dp,
					animationSpec = tween(durationMillis = ms),
				)
				Row(
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.spacedBy(tokens.spacing.md.dp),
				) {
					TextBlock(
						text = "$label (${ms}ms)",
						variant = TextBlockVariant.LabelMedium,
						modifier = Modifier.width(100.dp),
					)
					Box(
						modifier =
							Modifier
								.offset(x = offsetX)
								.size(40.dp)
								.clip(RoundedCornerShape(tokens.radius.md.dp))
								.background(parseColor(tokens.color.primary)),
					)
				}
			}
		}
	}
}

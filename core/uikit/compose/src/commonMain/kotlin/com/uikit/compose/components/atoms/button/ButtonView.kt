package com.uikit.compose.components.atoms.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uikit.components.atoms.button.ButtonConfig
import com.uikit.components.atoms.button.ButtonStyleResolver
import com.uikit.compose.theme.LocalDesignTokens
import com.uikit.compose.theme.parseColor
import com.uikit.foundation.Visibility

@Composable
fun ButtonView(
	config: ButtonConfig,
	onAction: (String) -> Unit = {},
	onClick: (() -> Unit)? = null,
	modifier: Modifier = Modifier,
) {
	if (config.visibility == Visibility.Gone) return

	val tokens = LocalDesignTokens.current
	val style = remember(config, tokens) { ButtonStyleResolver.resolve(config, tokens) }

	Button(
		onClick = {
			onClick?.invoke()
			config.actionRoute?.let(onAction)
		},
		enabled = config.isInteractive,
		colors =
			ButtonDefaults.buttonColors(
				containerColor = parseColor(style.colors.bg),
				contentColor = parseColor(style.colors.text),
				disabledContainerColor = parseColor(style.colors.bg),
				disabledContentColor = parseColor(style.colors.text),
			),
		border =
			if (style.colors.border != "transparent") {
				BorderStroke(1.dp, parseColor(style.colors.border))
			} else {
				null
			},
		shape = RoundedCornerShape(style.radius.dp),
		modifier =
			modifier
				.defaultMinSize(minHeight = style.sizes.height.dp)
				.then(if (config.visibility == Visibility.Invisible) Modifier.alpha(0f) else Modifier)
				.testTag(config.testTag ?: config.id),
	) {
		if (config.loading) {
			CircularProgressIndicator(
				modifier = Modifier.size(style.sizes.iconSize.dp),
				color = parseColor(style.colors.text),
				strokeWidth = 2.dp,
			)
		} else {
			Text(
				text = config.text,
				fontSize = style.sizes.fontSize.sp,
				letterSpacing = style.sizes.letterSpacing.sp,
			)
		}
	}
}

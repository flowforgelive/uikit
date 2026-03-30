package com.uikit.components.atoms.segmentedcontrol

import com.uikit.tokens.DesignTokens
import kotlin.js.JsExport
import kotlinx.serialization.Serializable

@JsExport
@Serializable
data class SegmentedControlColors(
    val trackBg: String,
    val thumbBg: String,
    val textActive: String,
    val textInactive: String,
    val border: String,
)

@JsExport
@Serializable
data class SegmentedControlSizes(
    val height: Int,
    val paddingH: Int,
    val fontSize: Int,
    val radius: Int,
    val thumbRadius: Int,
    val trackPadding: Int,
)

@JsExport
@Serializable
data class ResolvedSegmentedControlStyle(
    val colors: SegmentedControlColors,
    val sizes: SegmentedControlSizes,
)

@JsExport
object SegmentedControlStyleResolver {

    fun resolve(tokens: DesignTokens): ResolvedSegmentedControlStyle {
        return ResolvedSegmentedControlStyle(
            colors = SegmentedControlColors(
                trackBg = tokens.color.surfaceHover,
                thumbBg = tokens.color.surface,
                textActive = tokens.color.textPrimary,
                textInactive = tokens.color.secondary,
                border = tokens.color.border,
            ),
            sizes = SegmentedControlSizes(
                height = tokens.sizing.buttonSm,
                paddingH = tokens.spacing.sm,
                fontSize = tokens.typography.captionSize,
                radius = tokens.radius.md,
                thumbRadius = tokens.radius.sm + 2,
                trackPadding = 2,
            ),
        )
    }
}

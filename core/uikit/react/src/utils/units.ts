/**
 * Converts abstract dp value to rem string.
 * Base: 1rem = 16px (browser default). dp/16 = rem.
 */
export function toRem(dp: number): string {
	return `${dp / 16}rem`;
}

/**
 * Converts dp value to em string relative to a base font size.
 * em = dp / baseFontSizeDp. Matches Compose's sp-based scaling.
 */
export function toEm(dp: number, baseFontSizeDp: number): string {
	if (baseFontSizeDp === 0) return "0em";
	return `${dp / baseFontSizeDp}em`;
}

/**
 * Returns a unitless line-height ratio: lineHeightDp / fontSizeDp.
 * CSS unitless line-height scales with font-size, matching Compose's sp behavior.
 */
export function toLineHeightRatio(lineHeightDp: number, fontSizeDp: number): number {
	if (fontSizeDp === 0) return 1;
	return lineHeightDp / fontSizeDp;
}

const SYSTEM_FONT_STACK = '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif';

/**
 * Builds a CSS font-family stack from a logical font name.
 * Appends a system fallback stack for cross-browser/cross-platform compatibility.
 */
export function buildFontStack(fontFamilyName: string): string {
	if (!fontFamilyName) return SYSTEM_FONT_STACK;
	return `${fontFamilyName}, ${SYSTEM_FONT_STACK}`;
}

/**
 * Returns CSS properties for a typography TextStyle from design tokens.
 * Applies correct units: rem for fontSize, unitless ratio for lineHeight,
 * em for letterSpacing, and fontVariationSettings from tokens.
 */
export function textStyle(
	style: { fontSize: number; fontWeight: number; lineHeight: number; letterSpacing: number },
	tokens: { fontVariationSettings: string },
): Record<string, string | number> {
	return {
		fontSize: toRem(style.fontSize),
		fontWeight: style.fontWeight,
		lineHeight: toLineHeightRatio(style.lineHeight, style.fontSize),
		letterSpacing: toEm(style.letterSpacing, style.fontSize),
		fontVariationSettings: tokens.fontVariationSettings,
	};
}

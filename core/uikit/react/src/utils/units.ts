/**
 * Converts abstract dp value to rem string.
 * Base: 1rem = 16px (browser default). dp/16 = rem.
 */
export function toRem(dp: number): string {
	return `${dp / 16}rem`;
}

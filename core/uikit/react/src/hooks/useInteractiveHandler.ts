import { useCallback } from "react";

/**
 * Shared click handler for interactive components (Button, Chip).
 * Guards with isInteractive check and routes actionRoute to onAction callback.
 */
export function useInteractiveHandler(
	isInteractive: boolean,
	actionRoute: string | undefined | null,
	onClick?: () => void,
	onAction?: (route: string) => void,
): () => void {
	return useCallback(() => {
		if (isInteractive) {
			onClick?.();
			if (actionRoute) {
				onAction?.(actionRoute);
			}
		}
	}, [isInteractive, actionRoute, onAction, onClick]);
}

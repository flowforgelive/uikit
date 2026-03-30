import React from "react";

/**
 * Inline script для предотвращения flash of wrong theme при SSR.
 * Вставлять в <head> или в начало <body> в layout.tsx (Server Component).
 *
 * Работает синхронно ДО первого paint:
 * 1. Читает cookie "uikit-theme"
 * 2. Если нет — определяет по prefers-color-scheme
 * 3. Ставит data-theme на <html> до hydration
 */
export function UIKitThemeScript() {
	const script = `(function(){var d=document.documentElement;var c=(document.cookie.match(/(?:^|;\\s*)uikit-theme=([^;]*)/)||[])[1];var t=(c==='light'||c==='dark')?c:matchMedia('(prefers-color-scheme:dark)').matches?'dark':'light';d.setAttribute('data-theme',t)})()`;

	return (
		<script
			dangerouslySetInnerHTML={{ __html: script }}
			suppressHydrationWarning
		/>
	);
}

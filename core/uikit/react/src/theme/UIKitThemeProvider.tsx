"use client";

import React, {
	createContext,
	useContext,
	useState,
	useEffect,
	useCallback,
	useMemo,
	type ReactNode,
} from "react";
import {
	DesignTokens,
	ThemeMode,
	type DesignTokens as DesignTokensType,
} from "uikit-common";
import { DesignTokensContext } from "./useDesignTokens";

// ─── Types ──────────────────────────────────────────────

type ThemeModeValue = "light" | "dark" | "system";
type LayoutDirectionValue = "ltr" | "rtl";

interface UIKitThemeContextValue {
	/** Текущий режим темы (light | dark | system) */
	mode: ThemeModeValue;
	/** Резолвленный режим (light | dark) — System уже раскрыт */
	resolvedMode: "light" | "dark";
	/** Токены дизайн-системы для текущей темы */
	tokens: DesignTokensType;
	/** Направление текста (ltr | rtl) */
	dir: LayoutDirectionValue;
	/** Установить режим темы */
	setMode: (mode: ThemeModeValue) => void;
	/** Установить направление текста */
	setDir: (dir: LayoutDirectionValue) => void;
}

const UIKitThemeContext = createContext<UIKitThemeContextValue | null>(null);

// ─── Hooks ──────────────────────────────────────────────

export function useUIKitTheme(): UIKitThemeContextValue {
	const ctx = useContext(UIKitThemeContext);
	if (!ctx) {
		throw new Error("useUIKitTheme must be used within <UIKitThemeProvider>");
	}
	return ctx;
}

// ─── Cookie helpers (SSR-compatible) ────────────────────

const COOKIE_NAME = "uikit-theme";
const COOKIE_MAX_AGE = 365 * 24 * 60 * 60; // 1 year in seconds

function getCookie(name: string): string | null {
	if (typeof document === "undefined") return null;
	const match = document.cookie.match(
		new RegExp("(?:^|;\\s*)" + name + "=([^;]*)"),
	);
	return match ? decodeURIComponent(match[1]) : null;
}

function setCookie(name: string, value: string): void {
	if (typeof document === "undefined") return;
	document.cookie = `${name}=${encodeURIComponent(value)};path=/;max-age=${COOKIE_MAX_AGE};SameSite=Lax`;
}

function deleteCookie(name: string): void {
	if (typeof document === "undefined") return;
	document.cookie = `${name}=;path=/;max-age=0`;
}

// ─── System theme detection ─────────────────────────────

function useSystemDark(): boolean {
	const [isDark, setIsDark] = useState(() => {
		if (typeof window === "undefined") return true; // SSR default
		return window.matchMedia("(prefers-color-scheme: dark)").matches;
	});

	useEffect(() => {
		const mq = window.matchMedia("(prefers-color-scheme: dark)");
		const handler = (e: MediaQueryListEvent) => setIsDark(e.matches);
		mq.addEventListener("change", handler);
		return () => mq.removeEventListener("change", handler);
	}, []);

	return isDark;
}

// ─── Utils ──────────────────────────────────────────────

function parseMode(value: string | null | undefined): ThemeModeValue {
	if (value === "light" || value === "dark") return value;
	return "system";
}

function tokensForResolvedMode(resolved: "light" | "dark"): DesignTokensType {
	return resolved === "dark"
		? DesignTokens.Companion.DefaultDark
		: DesignTokens.Companion.DefaultLight;
}

// ─── Provider ───────────────────────────────────────────

interface UIKitThemeProviderProps {
	/**
	 * Initial theme from server (read from cookie in Server Component).
	 * If not provided, defaults to "system".
	 */
	initialTheme?: ThemeModeValue;
	/** Layout direction (ltr/rtl). Defaults to "ltr". */
	initialDir?: LayoutDirectionValue;
	children: ReactNode;
}

export function UIKitThemeProvider({
	initialTheme,
	initialDir = "ltr",
	children,
}: UIKitThemeProviderProps) {
	const [mode, setModeState] = useState<ThemeModeValue>(() => {
		// Priority: prop → cookie → "system"
		if (initialTheme) return initialTheme;
		return parseMode(getCookie(COOKIE_NAME));
	});

	const [dir, setDir] = useState<LayoutDirectionValue>(initialDir);

	const systemDark = useSystemDark();

	const resolvedMode: "light" | "dark" = useMemo(() => {
		if (mode === "system") return systemDark ? "dark" : "light";
		return mode;
	}, [mode, systemDark]);

	const tokens = useMemo(
		() => tokensForResolvedMode(resolvedMode),
		[resolvedMode],
	);

	const setMode = useCallback((newMode: ThemeModeValue) => {
		setModeState(newMode);
		if (newMode === "system") {
			deleteCookie(COOKIE_NAME);
		} else {
			setCookie(COOKIE_NAME, newMode);
		}
	}, []);

	// Sync data-theme and dir attributes on <html>
	useEffect(() => {
		document.documentElement.setAttribute("data-theme", resolvedMode);
	}, [resolvedMode]);

	useEffect(() => {
		document.documentElement.setAttribute("dir", dir);
	}, [dir]);

	const value = useMemo(
		() => ({ mode, resolvedMode, tokens, dir, setMode, setDir }),
		[mode, resolvedMode, tokens, dir, setMode, setDir],
	);

	return (
		<UIKitThemeContext.Provider value={value}>
			<DesignTokensContext.Provider value={tokens}>
				{children}
			</DesignTokensContext.Provider>
		</UIKitThemeContext.Provider>
	);
}

package com.uikit.foundation

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * In-memory ThemeProvider — переключение темы в рантайме без персистенции.
 * Подходит для каталогов, демо, тестов.
 */
class InMemoryThemeProvider(
	initial: ThemeMode = ThemeMode.System,
) : ThemeProvider {
	private val _themeMode = MutableStateFlow(initial)

	override fun observeThemeMode(): Flow<ThemeMode> = _themeMode

	override suspend fun getTheme(): ThemeMode = _themeMode.value

	override suspend fun setTheme(mode: ThemeMode) {
		_themeMode.value = mode
	}
}

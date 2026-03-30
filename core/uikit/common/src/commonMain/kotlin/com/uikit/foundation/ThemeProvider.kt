package com.uikit.foundation

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Контракт для провайдера темы с поддержкой чтения и записи.
 * UIKit не знает откуда берётся тема — только потребляет.
 *
 * Реализации могут:
 * - Читать/писать в DataStore (persistence)
 * - Использовать cookies (SSR)
 * - Возвращать константу (тестирование)
 * - Использовать системные настройки
 */
interface ThemeProvider {
	/** Наблюдает за текущим режимом темы. */
	fun observeThemeMode(): Flow<ThemeMode>

	/** Получает текущий режим темы. */
	suspend fun getTheme(): ThemeMode

	/** Устанавливает режим темы. */
	suspend fun setTheme(mode: ThemeMode)
}

/**
 * Default-провайдер — всегда System.
 * Используется когда нет внешней конфигурации.
 */
object DefaultThemeProvider : ThemeProvider {
	override fun observeThemeMode(): Flow<ThemeMode> = flowOf(ThemeMode.System)

	override suspend fun getTheme(): ThemeMode = ThemeMode.System

	override suspend fun setTheme(mode: ThemeMode) { /* no-op */ }
}

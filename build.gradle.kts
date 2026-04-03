plugins {
	alias(libs.plugins.kotlin.multiplatform) apply false
	alias(libs.plugins.kotlin.serialization) apply false
	alias(libs.plugins.compose.multiplatform) apply false
	alias(libs.plugins.kotlin.compose) apply false
}

allprojects {
	repositories {
		google()
		mavenCentral()
		maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
	}
}

/**
 * Verification task: scans View-layer files for hardcoded dimension values.
 *
 * Compose: forbids literal <number>.dp / <number>.sp (except 0.dp, 0.sp, 0f)
 * React CSS: forbids literal <number>px outside var() fallbacks (except within var())
 *
 * Run: ./gradlew verifyNoHardcodedValues
 */
tasks.register("verifyNoHardcodedValues") {
	group = "verification"
	description = "Checks for hardcoded dp/sp/px values in View-layer files"

	doLast {
		val violations = mutableListOf<String>()

		// --- Compose View files: forbid literal N.dp / N.sp ---
		val composeDir = file("core/uikit/compose/src")
		if (composeDir.exists()) {
			val dpSpPattern = Regex("""\b([1-9]\d*(?:\.\d+)?)\.(dp|sp)\b""")
			composeDir.walkTopDown()
				.filter { it.name.endsWith("View.kt") }
				.forEach { file ->
					file.readLines().forEachIndexed { index, line ->
						val trimmed = line.trim()
						if (trimmed.startsWith("//") || trimmed.startsWith("*")) return@forEachIndexed
						dpSpPattern.findAll(line).forEach { match ->
							violations.add("COMPOSE  ${file.relativeTo(projectDir)}:${index + 1}  →  ${match.value}")
						}
					}
				}
		}

		// --- React CSS Modules: forbid literal Npx outside var() fallbacks ---
		val reactDir = file("core/uikit/react/src")
		if (reactDir.exists()) {
			// Matches "Npx" that is NOT inside var(--..., Npx)
			val pxPattern = Regex("""\b(\d+)px\b""")
			val varFallbackPattern = Regex("""var\([^)]*\b\d+px\b[^)]*\)""")
			val allowedProperties = setOf("content", "opacity", "z-index")
			// 1px borders are standard CSS — allow them
			val allowedPxValues = setOf("0", "1")

			reactDir.walkTopDown()
				.filter { it.name.endsWith(".module.css") }
				.forEach { file ->
					file.readLines().forEachIndexed { index, line ->
						val trimmed = line.trim()
						if (trimmed.startsWith("/*") || trimmed.startsWith("*") || trimmed.startsWith("//")) return@forEachIndexed
						// Skip if the whole px match is inside a var() fallback
						if (pxPattern.containsMatchIn(line) && !varFallbackPattern.containsMatchIn(line)) {
							pxPattern.findAll(line).filter { it.groupValues[1] !in allowedPxValues }.forEach { match ->
								// Skip allowed CSS properties
								val propName = trimmed.substringBefore(":").trim()
								if (propName !in allowedProperties) {
									violations.add("CSS      ${file.relativeTo(projectDir)}:${index + 1}  →  ${match.value}  in: ${trimmed.take(80)}")
								}
							}
						}
					}
				}

			// --- React CSS: forbid literal numeric font-weight (not via var()) ---
			val fontWeightPattern = Regex("""font-weight:\s*(\d+)\s*;""")
			reactDir.walkTopDown()
				.filter { it.name.endsWith(".module.css") }
				.forEach { file ->
					file.readLines().forEachIndexed { index, line ->
						fontWeightPattern.find(line)?.let { match ->
							violations.add("CSS      ${file.relativeTo(projectDir)}:${index + 1}  →  font-weight: ${match.groupValues[1]}  (must use var())")
						}
					}
				}
		}

		if (violations.isNotEmpty()) {
			val message = buildString {
				appendLine()
				appendLine("╔══════════════════════════════════════════════════════════════╗")
				appendLine("║  HARDCODED VALUES DETECTED IN VIEW LAYER                    ║")
				appendLine("╠══════════════════════════════════════════════════════════════╣")
				violations.forEach { appendLine("║  $it") }
				appendLine("╠══════════════════════════════════════════════════════════════╣")
				appendLine("║  All dimensions must come from StyleResolver / DesignTokens  ║")
				appendLine("║  CSS: use var(--prefix-prop). Compose: use style.sizes.*.dp  ║")
				appendLine("╚══════════════════════════════════════════════════════════════╝")
			}
			throw GradleException(message)
		} else {
			logger.lifecycle("✅ No hardcoded values found in View-layer files.")
		}
	}
}

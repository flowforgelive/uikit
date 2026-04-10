@AGENTS.md

## Claude Code

Отвечай на русском языке. Технические термины оставляй в оригинале.
Эталонный компонент для изучения паттернов: Button (common + react + compose).
При создании нового компонента следуй чек-листу из AGENTS.md.

## Тестирование Compose Desktop через computer-use MCP

### Быстрый старт
```bash
# Сборка (только createDistributable — не run, не runDistributable)
./gradlew :apps:catalog-ui:compose:createDistributable

# Перезапуск
pkill -f "UIKit Catalog" 2>/dev/null; sleep 1
open "/Users/konstantin/projects/uikit/apps/catalog-ui/compose/build/compose/binaries/main/app/UIKit Catalog.app"

# Затем в computer-use:
# request_access(apps=["com.uikit.catalog"])
```

### Почему НЕ `run`
`./gradlew run` запускает raw Java процесс (`com.jetbrains.jbr.java`) — computer-use MCP его не видит.
`createDistributable` создаёт настоящий macOS `.app` с bundleID `com.uikit.catalog`.

### Важные паттерны
- Всегда `screenshot()` перед кликом — координаты берутся из последнего скриншота
- `zoom(region=[...])` — для детального осмотра мелких элементов (glass, shadow, border)
- `computer_batch([...])` — группировать несколько действий в один вызов
- Compose рендерит на canvas — accessibility tree отсутствует, только координаты

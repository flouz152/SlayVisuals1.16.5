# SlayClient standalone build

Этот каталог содержит упрощённый Gradle-проект, который позволяет собрать
ядро SlayClient и все задействованные утилиты из исходного клиента Expensive.
Сборка использует Java 19 (через toolchain) и подключает все jar-файлы из
`../libraries` как compileOnly-зависимости, чтобы код корректно компилировался
вне среды MCP.

## Быстрый старт

```bash
cd slayclient
gradle dist
```

Итоговый архив `build/libs/SlayClient-1.0.0-SNAPSHOT-slayclient.jar` содержит
классы SlayClient, GUI и все утилиты, необходимые для интеграции в кастомную
версию Minecraft 1.16.5. Добавляйте свои аддоны (скомпилированные под Java 19)
через API `im.slayclient.addon.SlayAddon`.

## Структура исходников
- `../src/im/slayclient` — код клиента, GUI и API для аддонов.
- `../src/im/expensive/utils` — полный набор утилит Expensive (рендер, математика,
  курсоры, шрифты и т.д.), которые переиспользуются в интерфейсе SlayClient.
- `../src/assets` и `../src/data` — ресурсы (шрифты, шейдеры), подключенные как ресурсы Gradle.

## Примечание
Gradle-проект использует существующие исходники, поэтому на GitHub остаётся
оригинальное дерево `src` c Expensive, а также отдельная папка `slayclient`
для сборки версии SlayClient.

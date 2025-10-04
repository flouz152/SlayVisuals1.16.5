# SlayClient standalone build

Этот каталог содержит полноценный Gradle-проект, который позволяет собрать
ядро SlayClient вместе с **полным** набором утилит Expensive (включая dropdown
GUI, рендер-помощники, курсоры и шрифты). Сборка использует Java 19 (через
toolchain) и подключает все jar-файлы из `../libraries` как compileOnly-зависимости,
чтобы код корректно компилировался вне среды MCP.

## Быстрый старт

```bash
cd slayclient
gradle dist
```

Итоговый архив `build/libs/SlayClient-1.0.0-SNAPSHOT-slayclient.jar` содержит
классы SlayClient, GUI, полный пакет утилит Expensive и ресурсы (шрифты,
шейдеры), необходимые для интеграции в кастомную версию Minecraft 1.16.5.
Добавляйте свои аддоны (скомпилированные под Java 19) через API
`im.slayclient.addon.SlayAddon`.

## Создание кастомной версии для лаунчера
1. Соберите jar командой `gradle dist` (см. выше).
2. В директории `.minecraft/versions` создайте папку `SlayClient`.
3. Скопируйте jar `SlayClient-1.0.0-SNAPSHOT-slayclient.jar` в эту папку и
   переименуйте его в `SlayClient.jar`.
4. Создайте файл `SlayClient.json` со следующим содержимым и положите рядом с jar:

```json
{
  "id": "SlayClient",
  "inheritsFrom": "1.16.5",
  "mainClass": "net.minecraft.launchwrapper.Launch",
  "minecraftArguments": "${auth_player_name} ${auth_uuid}",
  "libraries": [],
  "javaVersion": {
    "component": "jre-19",
    "majorVersion": 19
  }
}
```

5. Перезапустите лаунчер и выберите профиль `SlayClient`. Убедитесь, что Java 19
   установлен и выбран для запуска.

## Структура исходников
- `src/main/java/im/slayclient` — код клиента, GUI, API для аддонов и новые фичи
  (табы Addons/Performance/Settings/Client/Innovation, сохранение настроек).
- `src/main/java/im/expensive` — полный набор исходников Expensive (utils, dropdown,
  функции, события), скопированный для автономной сборки SlayClient.
- `src/main/resources` — ресурсы SlayClient. Дополнительно Gradle подмешивает
  оригинальные `../src/assets`, `../src/data`, `../src/pack.mcmeta` и `../src/pack.png`,
  чтобы шрифты и шейдеры совпадали с основным клиентом.

## Примечание
Gradle-проект использует существующие исходники, поэтому на GitHub остаётся
оригинальное дерево `src` c Expensive, а также отдельная папка `slayclient`
для сборки версии SlayClient.

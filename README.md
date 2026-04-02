# ⌨️ FastTyping — JavaFX Typing Trainer

**FastTyping** — це десктопний тренажер сліпого друку, розроблений на Java. Застосунок допомагає користувачам покращити швидкість і точність набору тексту, відстежувати прогрес через детальну статистику та отримувати ігрові досягнення.

---

## 🚀 Основний функціонал

* **Чотири режими тренування:**
    * [`Easy`](https://github.com/matganzovka90-bit/FastTyping/blob/main/src/main/java/speed/fasttyping/strategy/EasyStrategy.java): Базовий режим з однією цитатою.
    * [`Time Attack`](https://github.com/matganzovka90-bit/FastTyping/blob/main/src/main/java/speed/fasttyping/strategy/TimeAttackStrategy.java): Інтенсивний набір на 60 секунд.
    * [`Marathon`](https://github.com/matganzovka90-bit/FastTyping/blob/main/src/main/java/speed/fasttyping/strategy/MarathonStrategy.java): Режим на витривалість (180 секунд).
    * [`Vanishing`](https://github.com/matganzovka90-bit/FastTyping/blob/main/src/main/java/speed/fasttyping/strategy/VanishingStrategy.java): Ускладнений режим, де текст поступово зникає.
* **Аналітика в реальному часі**: [`Підрахунок WPM`](https://github.com/matganzovka90-bit/FastTyping/blob/main/src/main/java/speed/fasttyping/strategy/TypingSession.java#L51-L65) (слів за хвилину), [точності](https://github.com/matganzovka90-bit/FastTyping/blob/main/src/main/java/speed/fasttyping/strategy/TypingSession.java#L69-L83) (%) та [помилок](https://github.com/matganzovka90-bit/FastTyping/blob/main/src/main/java/speed/fasttyping/strategy/TypingSession.java#L85-L98) без затримок.
* **Гнучкість контенту**: Отримання [`текстів`](https://github.com/matganzovka90-bit/FastTyping/blob/main/src/main/java/speed/fasttyping/util/TextProvider.java) через зовнішні API (англійська та українська мови).
* **Локальне сховище**: [`Збереження результатів`](https://github.com/matganzovka90-bit/FastTyping/blob/main/src/main/java/speed/fasttyping/dao/AchievementDao.java) у вбудованій базі даних **H2**.
* **Візуалізація**: [`Графіки`](https://github.com/matganzovka90-bit/FastTyping/blob/main/src/main/java/speed/fasttyping/util/ChartBuilder.java) прогресу та таблиці статистики з фільтрацією.
* **Система досягнень**: [`Розблокування ачівок`](https://github.com/matganzovka90-bit/FastTyping/blob/main/src/main/java/speed/fasttyping/util/AchievementManager.java#L126-L144) за досягнуті рекорди.

---

## 🛠 Технологічний стек

* **Мова:** Java 21+
* **UI:** JavaFX (FXML, CSS)
* **Збірка:** Maven
* **БД:** H2 (JDBC, DAO Pattern)
* **Безпека:** SHA-256 хешування паролів

---

## 🏗 Programming Principles (Принципи програмування)

Проєкт розроблений з дотриманням ключових стандартів чистого коду:

1.  **Single Responsibility Principle (SRP)**: Кожен клас виконує одну задачу. Наприклад, [`PasswordHasher`](https://github.com/matganzovka90-bit/FastTyping/blob/main/src/main/java/speed/fasttyping/util/PasswordHasher.java) відповідає лише за криптографію, а [UserDao](https://github.com/matganzovka90-bit/FastTyping/blob/main/src/main/java/speed/fasttyping/dao/UserDao.java) — за роботу з БД.
2.  **Separation of Concerns**: Чіткий поділ на рівні: UI (FXML), бізнес-логіка (Controllers), та доступ до даних (DAO).
3.  **Encapsulation**: Стан об'єктів (наприклад, `User` або `TypingResult`) прихований і доступний лише через геттери/сеттери.
4.  **Validation at Boundaries**: Всі дані користувача валідуються класом [`InputValidator`](https://github.com/matganzovka90-bit/FastTyping/blob/main/src/main/java/speed/fasttyping/util/InputValidator.java) перед обробкою сервісами.
5.  **Don't Repeat Yourself (DRY)**: Спільна логіка (перемикання сцен, побудова графіків) винесена в утиліти [`SceneNavigator`](https://github.com/matganzovka90-bit/FastTyping/blob/main/src/main/java/speed/fasttyping/util/SceneNavigator.java) та [ChartBuilder](https://github.com/matganzovka90-bit/FastTyping/blob/main/src/main/java/speed/fasttyping/util/ChartBuilder.java).

---

## 🧩 Design Patterns (Патерни проєктування)

1.  **Strategy Pattern**: Реалізовано через інтерфейс [`TypingStrategy`](https://github.com/matganzovka90-bit/FastTyping/blob/main/src/main/java/speed/fasttyping/strategy/TypingStrategy.java). Це дозволяє легко перемикати логіку різних режимів друку під час виконання програми.
2.  **Observer Pattern**: Використовується для оновлення UI. [`TypingSession`](https://github.com/matganzovka90-bit/FastTyping/blob/main/src/main/java/speed/fasttyping/strategy/TypingStrategy.java) повідомляє підписників (`WpmObserver`, `AccuracyObserver`), коли змінюються показники.
3.  **Singleton Pattern**: Застосовано в [`SessionManager`](https://github.com/matganzovka90-bit/FastTyping/blob/main/src/main/java/speed/fasttyping/util/SessionManager.java) та [`DatabaseConnection`](https://github.com/matganzovka90-bit/FastTyping/blob/main/src/main/java/speed/fasttyping/dao/DatabaseConnection.java) для забезпечення єдиної точки доступу до ресурсів.
4.  **Factory Pattern**: [`DaoFactory`](https://github.com/matganzovka90-bit/FastTyping/blob/main/src/main/java/speed/fasttyping/dao/DaoFactory.java) централізовано створює об'єкти доступу до даних, приховуючи логіку їх ініціалізації.

---

## ♻️ Refactoring Techniques (Техніки рефакторингу)

* **Extract Method**: Розбиття великих методів у контролерах на дрібніші (`renderText`, `updateSummary`, `loadAchievements`).
* **Extract Module**: Поділ коду на логічні пакети (`strategy`, `observer`, `dao`, `util`) для зменшення зв'язаності.
* **Extract Validation Logic**: Винесення логіки перевірки логінів/паролів з контролера в окремий клас.
* **Replace Magic Numbers with Named Constants**: Використання іменованих констант (наприклад, `SALT_LENGTH`, `MIN_PASSWORD_LENGTH`) замість незрозумілих чисел у коді.
* **Encapsulate Reusable Logic**: Створення допоміжних класів-утиліт для повторного використання коду.

---
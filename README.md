# Rezepte-App

## Installation

## Voraussetzungen

* Java (z. B. Version 17 oder 21)
* Eine eigene laufende MySQL-Datenbank (lokal oder extern), auf die man Zugriff hat

---

## Setup

### Projekt vorbereiten

1. Projektordner herunterladen
2. Den Ordner entpacken/extrahieren

Hinweis: Es ist wichtig, dass das pom.xml und der src Ordner im selben Ordner liegen

### 1. Datenbank importieren

1. Terminal / Konsole als Administrator öffnen

2. In den Ordner wechseln, in dem sich `database.sql` befindet:

```bash
cd PFAD_ZUM_ORDNER
```

3. Import ausführen:

```bash
mysql -u USERNAME -p < database.sql
```

Hier USERNAME durch den Namen des Verwendeten Users in der Datenbank ersetzen.\
Der Befehl verbindet sich standardmäßig mit einer lokalen MySQL-Datenbank (localhost).
Sollte die Datenbank nicht lokal gespeichert sein, dann noch das Argument -h HOST vorne hinzufügen (und HOST mit der ip des Hosts ersetzen).

Falls der Befehl nicht funktioniert, sicherstellen, dass MySQL installiert ist und der Befehl `mysql` im Terminal verfügbar ist.

4. Passwort eingeben und warten, bis der Import abgeschlossen ist

---

### 2. Environment-Datei erstellen

1. Datei `.env.example` zu `.env` kopieren
2. Die Datei muss im selben Ordner wie das pom.xml und der src Ordner liegen
3. Werte im .env anpassen

Beispiel:

```env
DB_URL=jdbc:mysql://localhost:3306/rezeptedb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
DB_USER=example_user
DB_PASSWORD=example_password
```

Beispiel für externe Datenbank:

```env
DB_URL=jdbc:mysql://123.123.123.123:3306/rezeptedb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
```

---

## Start

### Variante 1 (IntelliJ)

1. Projekt in IntelliJ öffnen
2. Warten, bis alle Maven-Dependencies geladen wurden
3. Datei öffnen:

```
src/main/java/Main.java
```

4. Die Klasse `Main` ausführen

Diese Variante ist am einfachsten, da IntelliJ Maven bereits integriert hat und alle Abhängigkeiten automatisch verwaltet.

---

### Variante 2 (Terminal mit Maven)

1. Terminal öffnen
2. In den Projektordner wechseln (dort, wo sich die `pom.xml` befindet):

```bash
cd PFAD_ZUM_PROJEKTORDNER
```

3. Projekt starten:

```bash
mvn javafx:run
```

Voraussetzung: Maven muss installiert sein.

## Weitere Hinweise/Erklärungen zum Projekt

Für das Projekt haben wir Maven benutzt, um unsere externen
Libraries zu managen.\
Das `pom.xml`-File gehört zu Maven und sorgt dafür, dass wir beide immer
die gleichen Libraries und Versionen installiert haben.

Zu den Libraries:\
Anstatt bei jedem Import alles zu erklären, haben wir im `pom.xml`
erklärt, wofür wir externe Libraries benutzen.

Außerdem noch kurz zu unserer Verwendung von `java.util` (da das
keine externe Library im `pom.xml` ist):

Wir haben hauptsächlich `List<>`-Objekte benutzt und diese überall
verwendet, wo wir mehrere Objekte anzeigen oder benutzen, zum
Beispiel:

-   Meals, die wir aus der Datenbank bekommen
-   Ingredients, die in einer `ListView` angezeigt werden sollen

Wir haben außerdem noch **`java.util.stream.Collectors`**,
**`java.util.function.Function`**, **`java.util.Set`** und **`java.util.Objects`** benutzt.\
Diese werden jeweils in den entsprechenden Klassen erklärt.

------------------------------------------------------------------------

## Projektstruktur

Wir haben unseren Code wie folgt strukturiert:

``` text
project
├── pom.xml
│
├── src
│   └── main
│       ├── java
│       │   ├── Main.java
│       │   ├── App.java
│       │   │
│       │   ├── dao
│       │   │   ├── AreaDAO.java
│       │   │   ├── CategoryDAO.java
│       │   │   ├── IngredientDAO.java
│       │   │   ├── MealDAO.java
│       │   │   ├── MealIngredientDAO.java
│       │   │   ├── UserDAO.java
│       │   │   └── UserFavoriteDAO.java
│       │   │
│       │   ├── db
│       │   │   ├── Database.java
│       │   │   └── DbInit.java
│       │   │
│       │   ├── model
│       │   │   ├── Area.java
│       │   │   ├── Category.java
│       │   │   ├── Ingredient.java
│       │   │   ├── Meal.java
│       │   │   ├── MealIngredient.java
│       │   │   ├── User.java
│       │   │   └── UserFavorite.java
│       │   │
│       │   ├── services
│       │   │   ├── IngredientCalculationService.java
│       │   │   └── MealFilterService.java
│       │   │
│       │   └── view
│       │       ├── CreateMealController.java
│       │       ├── EditMealController.java
│       │       ├── LoginController.java
│       │       ├── MainController.java
│       │       ├── ManageMealsController.java
│       │       ├── MealDetailController.java
│       │       ├── RegisterController.java
│       │       └── SettingsController.java
│       │
│       └── resources
│           ├── css
│           │   └── style.css
│           │
│           ├── fxml
│           │   ├── create_meal.fxml
│           │   ├── edit_meal.fxml
│           │   ├── login.fxml
│           │   ├── main_view.fxml
│           │   ├── manage_meals.fxml
│           │   ├── meal_detail.fxml
│           │   ├── register.fxml
│           │   └── settings.fxml
│           │
│           ├── images
│           │   └── placeholder.png
│           │
│           └── sql
│               └── schema.sql
```

------------------------------------------------------------------------

## Packages

### model

Enthält Klassen die rein zur Erstellung von Objekten mit Feldern existieren und daher nur zur
Datenspeicherung genutzt werden.\
Da wir 7 Tabellen in unserer Datenbank haben, gibt es hier 7
korrespondierende Klassen.

### dao

Das Package `dao` ist das Backend unseres Projekts und ist daher für alle Data Access Objects zuständig, also
für alle Methoden und Objekte, die mit der Datenbank interagieren.\
Da wir 7 Tabellen und 7 Modelle in unserer Datenbank haben, gibt es
auch hier 7 korrespondierende Klassen.

### db

Ist für die Verbindungen mit der Datenbank zuständig.

### view

Enthält die JavaFX Controller.\
Diese Klassen sind für alles zuständig, was im Frontend angezeigt
wird und passiert.

### services

Enthält Logik für das Frontend, die wir aus Gründen der Übersicht in
eigene Klassen ausgelagert haben.

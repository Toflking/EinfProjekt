# Rezepte-App

Wir haben momentan noch keine Version zur eigenen Ausführung. Diese
werden wir jedoch für Milestone 2 bereit haben.

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
**`java.util.function.Function`** und **`java.util.Objects`** benutzt.\
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
│       │   │   └── MealIngredientDAO.java
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
│       │   │   └── MealIngredient.java
│       │   │
│       │   ├── services
│       │   │   ├── IngredientCalculationService.java
│       │   │   └── MealFilterService.java
│       │   │
│       │   └── view
│       │       ├── MainController.java
│       │       └── MealDetailController.java
│       │
│       └── resources
│           ├── fxml
│           │   ├── main_view.fxml
│           │   └── meal_detail.fxml
│           │
│           ├── css
│           │   └── style.css
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

Enthält Klassen zur Erstellung von Objekten und damit zur
Datenspeicherung.\
Da wir 5 Tabellen in unserer Datenbank haben, gibt es hier 5
korrespondierende Klassen.

### dao

Das Package `dao` ist für alle Data Access Objects zuständig, also
für alle Methoden und Objekte, die mit der Datenbank interagieren.\
Da wir 5 Tabellen und 5 Modelle in unserer Datenbank haben, gibt es
auch hier 5 korrespondierende Klassen.

### db

Ist für die Verbindungen mit der Datenbank zuständig.

### view

Enthält die JavaFX Controller.\
Diese Klassen sind für alles zuständig, was im Frontend angezeigt
wird.

### services

Enthält Logik für das Frontend, die wir aus Gründen der Übersicht in
eigene Klassen ausgelagert haben.

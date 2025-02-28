# HeartsPiek
HeartsPiek ist eine **kostenfreie** Fitness-App, mit der die Nutzer ihre aufgezeichneten Strecken per GPS genau verfolgen und ihr Gewicht und ihre Grösse überwachen können. Setzen Sie einen Alarm, um tägliche Morning Routinen zu tun. 🗺️🏃‍♂️

**Installieren Sie die App heute noch!**

### Infos
- Sie brauchen **keinen Account** für meine App
- Stellen Sie sicher, dass Sie über **genügend Speicherplatz** verfügen, da die Datenbank auf Ihrem Handy gespeichert wird.
- Für die Google Maps darstellung brauchen Sie **Internet**.

### Änderungen
- Keine Änderungen wurden vorgenommen

### Wie starte ich Linter? (Manuell)
Gebe in der Android Studio Kommandozeile welche man unten links auf "Terminal" folgender Befehl ein:
```sh
./gradlew lint
```
Die Ergebnisse findest du dann unter:
```bash
app/build/reports/lint-results.html
```

### Wie starte ich Linter? (Automate)
Wenn Sie Android Studio builden, sollte eine Task aufgerufen werden welches den Linter ausführt.
```kts
tasks.named("check").configure {
        dependsOn("lint")
    }
```

### Logs
- [Logs.txt (28-02-2025)](/Logs/Logs-28-02-2025.txt)

### Author & Entwickler
- Came

# Publish Page
![HeartsPiek Publish Page](/Concept/Images/Publish.png)

# Benutzte Quellen
- [Andorid Studio Dokumentation](https://developer.android.com/studio?hl=de)
- [Kotlin Dokumentation](https://www.w3schools.com/KOTLIN/index.php)
- [StackOverflow Fehlersuche](https://stackoverflow.com/questions)
- [Figma - Designplatform](http://figma.com/)
- [Icon Generator Romannurik](https://romannurik.github.io/AndroidAssetStudio/icons-launcher.html)
- [Google Cloud Console](https://console.cloud.google.com/google/maps-apis/overview?pli=1)
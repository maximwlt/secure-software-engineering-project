# Dokumentation
[[_TOC_]]
## Beschreibung
#### Inhalt der Anwendung
Diese Anwendung ermöglicht das Erstellen von Notizen
mittels Markdown-Syntax. Diese Notizen werden in HTML gerendert und angezeigt.
Ein Benutzer kann sich mit einer E-Mail Adresse und Passwort registrieren und ebenso anmelden
mit den gleichen Angaben. 
Dabei haben wir sichergestellt, dass unsere Anwendung sicher ist vor Angriffen wie
XSS, CSRF, SQL Injection, DoS-Attacken, User Enumeration und haben uns bei der Implementierung an die Vorgaben von OWASP gerichtet.

#### Gruppenmitglieder: 
  - Kaylin Althoff
  - Maxim Walter

### Verwendete Technologien

#### Backend:
- **Spring Boot:** Wir haben Spring Boot verwendet, um ein stabiles Backend zu haben, was
  durch Spring Security viele Sicherheitsfeatures mitbringt. Dabei war der SecurityContextFilter
  nützlich, um die Authentifizierung und Autorisierung zu handhaben. Dazu haben wir JPA benutzt,
  um ORM zu nutzen bzw. die Datenbankzugriffe zu erleichtern. 
- **Maven:** Als Build-Tool haben wir Maven verwendet, um die Abhängigkeiten zu verwalten und das Projekt zu bauen.
- **Java 21** Wir haben Java als Sprache verwendet, da Java eine sichere Programmiersprache ist und diese in Spring Boot gebraucht wird.

#### Frontend:
- **React:**  Wir haben React verwendet, um einfache Komponenten zu erstellen und eine Single-Page-Application (SPA) zu bauen.
- **Vite:** Als Build-Tool haben wir Vite verwendet, um das Frontend zu bauen und die Abhängigkeiten zu verwalten, da
    Vite schneller als andere Build-Tools ist und es veraltet ist, Create-React-App für neue Projekte zu verwenden.
- **TypeScript:** Wir haben statt JavaScript als Programmiersprache TypeScript verwendet, um typsicheren Code im Frontend zu schreiben.
- **Nginx:** Wurde gewählt um das Frontend zu hosten, da es gleichzeitig auch als Reverse-Proxy für das Backend konfiguriert werden kann.
    Außerdem wurde es auch mit einem *Rate-Limit* und einer *Content Security Policy* versehen.

#### Datenbank:
- **PostgreSQL:** Wir haben PostgreSQL als relationale Datenbank verwendet, um die Benutzerdaten und Notizen zu speichern.
Die Wahl fiel auf PostgreSQL, da es uns beiden vertraut ist.

#### Testing {#tests}
- **Vitest:** Um Frontend Unit Tests zu schreiben, haben wir Vitest verwendet, da es gut mit Vite zusammenarbeitet und schnelle Tests ermöglicht.
- **Spring Boot Test:** Um Backend Tests zu schreiben, haben wir Spring Boot Test verwendet, damit wir die mitgelieferten Testfunktionen von Spring Boot nutzen können. Hierbei haben wir keine Integrationstest
verwendet, sondern simple Unit Tests geschrieben.

#### Infrastruktur
- **GitLab:** ...
- **Docker:** Ist durch die Projektbeschreibung vorgegeben. Wird zum builden und ausführen der Anwendung und ihrer Komponenten verwendet.

### Dependencies
#### Backend

(TODO: MUSS ANGEPASST WERDEN)

| Name                                | Version                      | Nutzen                            |
|-------------------------------------|------------------------------|-----------------------------------|
| spring-boot-starter-data-jpa        | 4.0.0 (parent)               |                                   |
| spring-boot-starter-security        | 4.0.0 (parent)               |                                   |
| spring-security-crypto              | 7.0.2                        |                                   |
| zxcvbn                              |                              | Passwortvalidierung               |
| bcprov-jdk18on                      | 1.83                         | Nutzung von Argon2PasswordEncoder |
| spring-boot-starter-validation      | 4.0.0 (parent)               |                                   |
| spring-boot-starter-webmvc          | 4.0.0 (parent)               |                                   |
| jjwt-api                            | 0.13.0                       |                                   |
| jjwt-impl                           | 0.13.0                       |                                   |
| jjwt-jackson                        | 0.13.0                       |                                   |
| postgresql                          | managed by Spring Boot 4.0.0 |                                   |
| spring-boot-starter-mail            | 4.0.0                        |                                   |
| spring-boot-starter-data-jpa-test   | 4.0.0 (parent, test)         |                                   |
| spring-boot-starter-security-test   | 4.0.0 (parent, test)         |                                   |
| spring-boot-starter-validation-test | 4.0.0 (parent, test)         |                                   |
| spring-boot-starter-webmvc-test     | 4.0.0 (parent, test)         |                                   |

<br>

#### Frontend

**Production** <br>

| Name           | Version | Nutzen |
|----------------|---------|--------|
|@zxcvbn-ts/core | 3.0.4   |        |
|@zxcvbn-ts/language-common | 3.0.4   |        |
|@zxcvbn-ts/language-de | 3.0.2   |        |
|dompurify       | 3.3.1   |        |
|marked          | 17.0.1  |        |
|react           | 19.2.0  |        |
|react-dom       | 19.2.0  |        |
|react-router    | 7.11.0  |        |

**Development** <br>

| Name               | Version | Nutzen |
|--------------------|---------|--------|
|@vitejs/plugin-react | 5.1.1   |        |
|vite                | 7.2.4   |        |
|typescript          | ~5.9.3  |        |
|typescript-eslint  | 8.46.4  |        |
|eslint              | 9.39.1  |        |
|eslint/js              | 9.39.1  |        |
|eslint-plugin-react-hooks | 7.0.1   |        |
|eslint-plugin-react-refresh | 0.4.24  |        |
|@types/node         | 24.10.1|        |
|@types/react        | 19.2.5 |        |
|@types/react-dom    | 19.2.3 |        |
|globals            | 16.5.0 |        |




## Infrastruktur

### CI/CD
...

### Verwendete IDE
Wir haben ein Monorepository erstellt und IntelliJ IDEA Ultimate für unser gesamtes Projekt (Backend, Frontend, etc.) verwendet,
da IntelliJ sowohl Java/Spring Boot als auch TypeScript/React sehr gut unterstützt und viele nützliche Features
für die Entwicklung mitbringt. 

### Struktur des Entwicklungsprozesses
Wir haben GitLab verwendet und Issues erstellt,
die wir jeweils in einem eigenen Branch bearbeitet haben. 
Den Main-Branch haben wir geschützt, damit wir nur über die 
Merge-Request die Änderungen aus dem Feature-branch übernehmen
und ein direkter Push auf dem main-branch nicht möglich ist.
Außerdem haben wir uns zusammen wöchentlich über Discord getroffen, um
gemeinsam die Merge Requests durchzugehen, Probleme zu besprechen
und unsere Aufgaben/TODOs zu kommunizieren.
Für unser Projekt haben wir KI benutzt. Hauptsächlich für das
Debugging bzw. die Fehlersuche. Wenn wir sicherheitsrelevante Features implementieren
mussten und dabei KI verwendet haben, haben wir immer geschaut, ob der Vorschlag der KI
mit den OWASP Vorgehen übereinstimmt und eigenständig überprüft, ob vorgeschlagene Libraries gepflegt und nicht veraltet sind.
Für das Layout im Frontend habe ich mir fast alle CSS Dateien generieren lassen, um Zeit
zu sparen und weil hier kein Sicherheitsrisiko bestand.




## Funktionen

### Anmeldung

### Registrierung
Um einen Nutzer registrieren zu können, muss dieser eine gültige E-Mail Adresse
besitzen und ein Passwort, welches der Passwordvalidation (nach zxcvbn-Score)
entspricht, übergeben. Nach der Registrierung wird eine Bestätigungslink an die angegebene E-Mail Adresse
gesendet, um die E-Mail Adresse zu verifizieren und um sicherzugehen, dass die vom Nutzer angegebene E-Mail Adresse in seinem Besitz ist.
Dabei werden zunächst die Daten in einer temporären Tabelle (Registration_Request) gespeichert, bis der Nutzer
seine E-Mail Adresse bestätigt hat. Ist dies passiert, dann werden die Daten
in die eigentliche Nutzertabelle (User) übertragen und der Eintrag in der temporären Tabelle gelöscht.
Der Nutzer muss auf den Link in der E-Mail klicken, um die Registrierung abzuschließen. Dies muss innerhalb
von 3 Stunden geschehen, da der Link sonst ungültig wird bzw. der Eintrag in der temporären Tabelle gelöscht wird.
Der Token für den Bestätigungslink muss sicher sein
und zufällig generiert werden, um sicherzustellen, dass nur der Besitzer der E-Mail Adresse
die Registrierung abschließen kann. Dabei sind für unseren Fall *Cryptographically Secure Pseudo-Random Number Generators* [CSPRNG](https://cheatsheetseries.owasp.org/cheatsheets/Cryptographic_Storage_Cheat_Sheet.html#secure-random-number-generation)
wichtig. In Java nutzt man daher die `java.security.SecureRandom` Library, um kryptografisch sichere Zufallszahlen zu generieren.

Folgender Code zeigt die Generierung eines sicheren, zufälligen Tokens mit 256 Bit (32 Bytes) Länge
```java
public String generateOpaqueToken() {
    byte[] randomBytes = new byte[32];
    secRandom.nextBytes(randomBytes);
    return base64Encoder.encodeToString(randomBytes);
}
```
Anmerkung: Die Sicherheit des Tokens ist nicht abhängig von der Länge des Tokens, sondern von der Entropie,
also wie die Bytes zufällig generiert werden. Ein 32 Byte langer Token ist daher ausreichend sicher.

Bei der Speicherung des URL-Tokens haben wir




Verbesserung: Das gleiche Secret haben wir beim HMAC für die Refresh Tokens verwendet.
Es wäre jedoch besser einen weiteren eigenen Secret zu erstellen, damit die
_Schlüsseltrennung_ eingehalten wird.

Das Secret haben wir in einer .env Datei gespeichert, die nicht ins Repository gepusht wird, welche
in application.properties referenziert wird.

Um Datenschutz einzuhalten, könnte man, wenn
innerhalb von 3 Stunden die E-Mail Adresse nicht bestätigt wird, die Daten
löschen, indem man einen CronJob einrichtet, der täglich die unbestätigten
Nutzer aus der temporären Tabelle löscht, was wir jedoch nicht implementiert haben.
Ist die E-Mail Adresse bereits registriert **und** verifiziert und es wird erneut eine Registrierung mit dieser
E-Mail Adresse versucht, dann bekommt der Angreifer die gleiche Rückmeldung. Somit wird nicht verraten, dass
die E-Mail Adresse bereits registriert ist. Dem Besitzer der E-Mail Adresse wird jedoch eine Warn-E-Mail gesendet
Dieser Vorgang dient, um User Enumeration zu verhindern.

Ist nun eine Registrierung mit gültigen Daten erfolgt, wird das Passwort
mit einem sicheren Algorithmus argon2id gehashed und in der Datenbank gespeichert. Dieser wird
von OWASP als erste Wahl empfohlen. Bei argon2id ist das Salting bereits integriert.
Es müssen lediglich die richtigen Parameter gesetzt werden, worauf wir uns an die [OWASP Empfehlung](https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html#argon2id) gehalten haben (siehe unten). <br>
**Argon2id Parameter:**
- Saltlänge: 16 Bytes
- Hashlänge: 32 Bytes
- Speicher: 12288 MiB
- Iterationen: 3
- Parellelismus: 1


**Benutzte Dependencies:**
- zxcvbn: Für die Passwortstärke Validierung im Backend
- zxcvbn-ts: Für die Passwortstärke Validierung im Frontend
- ... (TODO)


### Autorisierung

### Funktionen der Anwendung
#### Notiz

#### Social-Plugin

#### Suche

#### Datenschutz


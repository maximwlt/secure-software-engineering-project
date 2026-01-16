# Dokumentation

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

#### Datenbank:
- **PostgreSQL:** Wir haben PostgreSQL als relationale Datenbank verwendet, um die Benutzerdaten und Notizen zu speichern.
Die Wahl fiel auf PostgreSQL, da es uns beiden vertraut ist.

#### Reverse Proxy
- **Nginx:** ...

#### Testing
- **Vitest:** Um Frontend Unit Tests zu schreiben, haben wir Vitest verwendet, da es gut mit Vite zusammenarbeitet und schnelle Tests ermöglicht.
- **Spring Boot Test:** Um Backend Tests zu schreiben, haben wir Spring Boot Test verwendet, damit wir die mitgelieferten Testfunktionen von Spring Boot nutzen können. Hierbei haben wir keine Integrationstest
verwendet, sondern simple Unit Tests geschrieben.

#### Infrastruktur
- **GitLab:** ...
- **Docker:** ...


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
Der registrierte **und** verifizierte Nutzer kann sich mit seiner E-Mail Adresse
und seinem Passwort anmelden. 
Als Authentifizierungsmechanismus haben wir JSON Web Tokens (JWT) gewählt,
um eine zustandslose Authentifizierung zu ermöglichen.
Der JWT Token besitzt eine Gültigkeit von 10 Minuten und besitzt
als sub-claim die Nutzer-UUID, um den Nutzer zu identifizieren.
Hierbei werden **keine** persönlichen Daten im Token gespeichert, um Datenschutz einzuhalten.
Als Signieralgorithmus haben wir `HS256` (HMAC mit SHA-256) gewählt, 
Damit der Nutzer nach Ablauf
des Tokens nicht erneut seine Anmeldedaten eingeben muss, haben wir Refresh Tokens
implementiert, die eine Gültigkeit von 7 Tagen besitzen.




Der JWT-Token  

Der Client 


 

### Registrierung
Um einen Nutzer registrieren zu können, muss dieser eine gültige E-Mail Adresse
besitzen und ein Passwort, welches der Passwordvalidation (nach zxcvbn-Score)
entspricht, übergeben. Nach der Registrierung wird eine Bestätigungslink an die angegebene E-Mail Adresse
gesendet, um die E-Mail Adresse zu verifizieren und um sicherzugehen, dass die vom Nutzer angegebene E-Mail Adresse in seinem Besitz ist.
Dabei werden zunächst die Daten in einer temporären Tabelle (Registration_Request) gespeichert, bis der Nutzer
seine E-Mail Adresse bestätigt hat. Ist dies passiert, dann werden die Daten
in die eigentliche Nutzertabelle (User) übertragen und der Eintrag in der temporären Tabelle gelöscht.
Der Nutzer muss auf den Link in der E-Mail klicken, um die Registrierung abzuschließen. Dies muss innerhalb
von 3 Stunden geschehen, da der Link sonst ungültig wird.
Außerdem ist der Link nur einmalig verwendbar.
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

Bei der Speicherung des URL-Tokens haben wir uns an die OWASP Vorgaben aus dem ["Passwort vergessen" Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Forgot_Password_Cheat_Sheet.html#implementing-password-reset-tokens) gehalten,
obwohl es hier nicht um ein Passwort zurücksetzen geht, sondern um die Verifizierung der E-Mail Adresse.
Als Angabe steht gibt OWASP _"stored securely"_ an und verweist auf das [Password Storage Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html),
jedoch ist langsame Hashen von Tokens nicht sinnvoll, da diese nur kurzlebig sind und die Performance
beeinträchtigen würden. Daher haben wir uns entschieden, den Token mit SHA3-256 zu hashen.
Die Kombination aus einem kryptografisch sicheren, zufällig generierten Token, dem 
SHA3-256 Hash, der kurzlebigkeit des Tokens (3 Stunden) und der Einmaligkeit ist ausreichend sicher,
um die E-Mail Verifizierung via URL Tokens umzusetzen.
Speziell haben wir den SHA3-256 Algorithmus gewählt, da dieser aus der SHA3 Familie stammt,
welche als langfristig sicherer gilt als die SHA2 Familie.
Dafür haben wir folgenden Code implementiert:
```java
public String hashToken(String token) {
    try {
        MessageDigest digest = MessageDigest.getInstance("SHA3-256");
        byte[] hashedBytes = digest.digest(token.getBytes(StandardCharsets.UTF_8));
        return base64Encoder.encodeToString(hashedBytes);
    } catch (NoSuchAlgorithmException e) {
        throw new RuntimeException("Error hashing token", e);
    }
}
```
Die MessageDigest Klasse ist Teil der Java Standardbibliothek und bietet eine einfache Möglichkeit,
um Hashes mit verschiedenen Algorithmen zu generieren, weshalb keine externe Library benötigt wird.

Um Datenschutz einzuhalten, könnte man, wenn
innerhalb von 3 Stunden die E-Mail Adresse nicht bestätigt wird, die Daten
löschen, indem man einen CronJob einrichtet, der täglich die unbestätigten
Nutzer aus der temporären Tabelle löscht, was wir jedoch nicht implementiert haben.
Ist die E-Mail Adresse bereits registriert **und** verifiziert bzw. der Account erfolgreich erstellt wurde, und es wird erneut eine Registrierung mit dieser
E-Mail Adresse versucht, dann bekommt der Angreifer die gleiche Rückmeldung. Somit wird nicht verraten, dass
die E-Mail Adresse bereits registriert ist. Dem Besitzer der E-Mail Adresse wird jedoch eine Warn-E-Mail gesendet
Dieser Vorgang dient, um User Enumeration zu verhindern.

Damit die Nutzer sichere Passwörter verwenden und vor Brute-Force Attacken geschützt sind,
haben wir uns die zxcvbn Library integriert, sowohl im Backend als auch im Frontend und nur Passwörter mit einem Score von 4 (sehr stark) erlaubt.
Passwörter mit einem Score von 0-3 werden abgelehnt und der Nutzer bekommt eine entsprechende Fehlermeldung.
Dabei gelten Passwörter mit einer Stärke von 3 als erratbar in weniger als 10^10 Versuchen laut diesem [Artikel](https://dev.to/tooleroid/password-strength-testing-with-zxcvbn-a-deep-dive-into-modern-password-security-2hl8).
Passwörter mit einem Score von 4 gelten als sehr stark und benötigen erhebliche Rechenressourcen, um diese zu erraten.
Im Frontend werden dem Nutzer zusätzlich noch Tipps während der Passworteingabe angezeigt,
wie er sein Passwort verbessern kann, um einen höheren Score zu erreichen.
Anstatt die Passwortregeln (Mindestlänge, Sonderzeichen, etc.) selbst zu definieren,
haben wir uns für zxcvbn entschieden, da diese Library eine gängige Muster, Passwortlisten/Wörterbücher und
andere Faktoren berücksichtigt, um die Passwortstärke zu bewerten, die ansonsten de
Hier ist jedoch anzumerken, dass die zxcvbn Library im Backend 
weniger aktuell (letzter Release war 2017) ist und gepflegt wird als die TypeScript Version im Frontend (`zxcvbn-ts`),
jedoch wollten wir die Passwortvalidierung im Backend und Frontend von den Libraries her konsistent halten.

Ist nun eine Registrierung mit gültigen Daten erfolgt, wird das Passwort
mit einem sicheren Algorithmus `argon2id` gehasht und in der Datenbank gespeichert. Dieser wird
von OWASP als erste Wahl empfohlen ([Password Storage Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html)).
Bei argon2id ist das Salting bereits integriert, denn
es müssen lediglich die richtigen Parameter gesetzt werden, worauf wir uns an die [OWASP Empfehlung](https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html#argon2id) gehalten haben <br>

**Argon2id Parameter:**
- Saltlänge: 16 Bytes
- Hashlänge: 32 Bytes
- Speicher: 12288 MiB
- Iterationen: 3
- Parellelismus: 1

Hierbei liefert Spring Security Crypto eine eingebaute Unterstützung für Argon2id
über den `Argon2PasswordEncoder`, welcher die oben genannten Parameter standardmäßig verwendet, jedoch 
wird intern noch die Bouncy Castle Library benötigt, weshalb wir die `bcprov-jdk18on` Dependency
hinzugefügt haben. Das Repository von Bouncy Castle wird regelmäßig gepflegt und ist nicht veraltet
Hier ein Einblick aus dem Repository: [Bouncy Castle GitHub](https://github.com/bcgit/bc-java/graphs/commit-activity)

Im Backend haben wir durch Bean Validation (@Min, @Max, @NotBlank, @StrongPassword (zxcvbn)) Min- und Max-Längen für die E-Mail Adresse und das Passwort festgelegt,
und eine Grundabsicherung gegen DoS-Attacken implementiert, indem wir in der Reverse Proxy 
maximal 1 Request pro Sekunde erlauben.
Die Eingabefelder sind vor SQL Injections sicher, da wir JPA als Object-Relational-Mapper verwenden,
welches Prepared Statements verwendet, anstatt String-Konkatenation für SQL Queries.
Vor Stored-XSS sind die Eingabefelder geschützt, da unsere REST-API nur JSON-Daten akzeptiert
und auch nur JSON-Daten zurückgibt. 


### Autorisierung

### Funktionen der Anwendung
#### Notiz

- JPA eingehen, dass es SQL Injection verhindert durch vorgefertigte Methodennamen
und bei Custom SQL-Queries via @Query Annotation Prepared Statementsverwendet werden,
anstatt String Konkatenation.

#### Social-Plugin

#### Suche

#### Datenschutz

### CI/CD Pipeline



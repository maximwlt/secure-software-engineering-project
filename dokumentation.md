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
  nützlich, um die Authentifizierung und Autorisierung zu handhaben. Dazu haben wir JPA benutzt
  um ORM zu implementieren und die Datenbankzugriffe zu erleichtern.
- **Maven:** Als Build-Tool haben wir Maven verwendet, um die Abhängigkeiten zu verwalten und das Projekt zu bauen.
- **Java 21** (Wir haben Java als Sprache verwendet, da Java eine sichere Programmiersprache ist und Spring Boot sehr gut unterstützt.)
#### Frontend:
- React
- TypeScript

#### Datenbank:
- PostgreSQL

#### Reverse Proxy 
- Nginx

#### Testing
- Vitest (Frontend Unit Tests)
- Spring Boot Test (Backend Unit Tests)

#### Infrastruktur
- GitLab
- Docker



### Dependencies

#### Backend

JPA

| Name                                | Version                      | Nutzen |
|-------------------------------------|------------------------------|--------|
| spring-boot-starter-data-jpa        | 4.0.0 (parent)               |        |
| spring-boot-starter-security        | 4.0.0 (parent)               |        |
| spring-security-crypto              | 7.0.2                        |        |
| zxcvbn       valiedierin            |                              |        |
| bcprov-jdk18on                      | 1.83                         |        |
| spring-boot-starter-validation      | 4.0.0 (parent)               |        |
| spring-boot-starter-webmvc          | 4.0.0 (parent)               |        |
| jjwt-api                            | 0.13.0                       |        |
| jjwt-impl                           | 0.13.0                       |        |
| jjwt-jackson                        | 0.13.0                       |        |
| postgresql                          | managed by Spring Boot 4.0.0 |        |
| spring-boot-starter-mail            | 4.0.0                        |        |
| spring-boot-starter-data-jpa-test   | 4.0.0 (parent, test)         |        |
| spring-boot-starter-security-test   | 4.0.0 (parent, test)         |        |
| spring-boot-starter-validation-test | 4.0.0 (parent, test)         |        |
| spring-boot-starter-webmvc-test     | 4.0.0 (parent, test)         |        |

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
und unsere Aufgaben zu kommunizieren. 

Für unser Projekt haben wir KI benutzt. Hauptsächlich für das
Debugging bzw. die Fehlersuche. Wenn wir sicherheitsrelevante Features implementieren
mussten und dabei KI verwendet haben, haben wir immer geschaut, ob der Vorschlag der KI
mit den OWASP Vorgehen übereinstimmt und eigenständig überprüft, ob vorgeschlagene Libraries sicher und nicht veraltet sind.
Für das Layout im Frontend habe ich mir die CSS Dateien generieren lassen, um Zeit
zu sparen und weil hier kein Sicherheitsrisiko bestand.   




## Funktionen

### Anmeldung

### Registrierung

### Autorisierung

### Notizen

### CI/CD Pipeline



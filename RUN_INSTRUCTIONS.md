# How to Run the Arabic Text Editor

## Prerequisites
- Java 8 (JDK 1.8) installed
- MariaDB installed and running
- Database `realeditor` created with tables

## Database Setup
1. Start MariaDB service
2. Create database and tables:
```sql
-- Use the SQL script in resource/Database/EditorDBQuery.sql
-- Or manually create database: CREATE DATABASE realeditor;
```

3. Update `config.properties` with your database credentials:
```properties
db.url = jdbc:mariadb://localhost:3306/realeditor
db.username = root
db.password = YOUR_PASSWORD
db.type = dal.MariaDBDAOFactory
```

## Compilation
From project root directory:
```powershell
javac -encoding UTF-8 -cp "bin\mariadb-java-client-3.4.1.jar;bin\log4j-api-2.20.0.jar;bin\log4j-core-2.20.0.jar;bin\AlKhalilMorphoSys2.jar;bin\AlKhalilDiacritizer.jar" -d bin -sourcepath src src\dto\*.java src\dal\*.java src\bll\*.java src\pl\*.java src\Driver.java
```

## Running the Application
From project root directory:
```powershell
java -cp "bin;bin\mariadb-java-client-3.4.1.jar;bin\log4j-api-2.20.0.jar;bin\log4j-core-2.20.0.jar;bin\AlKhalilMorphoSys2.jar;bin\AlKhalilDiacritizer.jar" Driver
```

## Verified Configuration
- Java Version: 1.8.0_202
- MariaDB: Running on localhost:3306
- Database: realeditor (11 tables created successfully)
- Application: GUI launches without errors

## Notes
- Ensure you run from the project root directory (where `config.properties` is located)
- JAR file `ADAT-Stemmer.v1.20180101.jar` is corrupted and excluded from classpath
- Log4j configuration is in `bin/log4j2.xml`

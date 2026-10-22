# Career Skill Gap Analyzer (Console + Java + MySQL)

## Requirements
- JDK 21+
- MySQL Server
- Maven

## 1. Create the database
Open MySQL Workbench, open `database/schema.sql`, and execute the full script. It creates the `career_skill_gap` database and sample job roles/skills.

## 2. Configure MySQL credentials
The application uses MySQL user `root` by default. Set the environment variable `MYSQL_PASSWORD` to your MySQL password before running. If your MySQL username is different, update `USER` in `DBConnection.java`.

Windows Command Prompt (only for the current CMD window):
```bat
set MYSQL_PASSWORD=your_mysql_password
```

PowerShell:
```powershell
$env:MYSQL_PASSWORD="your_mysql_password"
```

## 3. Run the project
Open a terminal in the folder containing `pom.xml` and run:
```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.career.app.Main"
```
If `mvn exec:java` says the exec plugin is unavailable, use your IDE's Maven reload and run `Main.java`, or add the Maven Exec Plugin to `pom.xml`.

## Menu
1. Register Candidate
2. View Candidates
3. Add or Update Candidate Skill
4. View Job Roles and Required Skills
5. Analyze Skill Gap
6. Exit

## Matching rule
The match percentage is the number of required skills the candidate has divided by the total configured required skills, multiplied by 100. Proficiency is stored, but this first version treats a skill as matched if it is present. Weighted matching can be added as an enhancement.

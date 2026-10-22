CREATE DATABASE IF NOT EXISTS career_skill_gap;
USE career_skill_gap;

CREATE TABLE IF NOT EXISTS candidates (
  candidate_id INT PRIMARY KEY AUTO_INCREMENT,
  full_name VARCHAR(100) NOT NULL,
  email VARCHAR(150) NOT NULL UNIQUE,
  education VARCHAR(120) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS job_roles (
  role_id INT PRIMARY KEY AUTO_INCREMENT,
  role_name VARCHAR(100) NOT NULL UNIQUE,
  description VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS skills (
  skill_id INT PRIMARY KEY AUTO_INCREMENT,
  skill_name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS role_skills (
  role_id INT NOT NULL,
  skill_id INT NOT NULL,
  importance ENUM('REQUIRED','PREFERRED') NOT NULL DEFAULT 'REQUIRED',
  PRIMARY KEY (role_id, skill_id),
  FOREIGN KEY (role_id) REFERENCES job_roles(role_id) ON DELETE CASCADE,
  FOREIGN KEY (skill_id) REFERENCES skills(skill_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS candidate_skills (
  candidate_id INT NOT NULL,
  skill_id INT NOT NULL,
  proficiency ENUM('BEGINNER','INTERMEDIATE','ADVANCED') NOT NULL DEFAULT 'BEGINNER',
  PRIMARY KEY (candidate_id, skill_id),
  FOREIGN KEY (candidate_id) REFERENCES candidates(candidate_id) ON DELETE CASCADE,
  FOREIGN KEY (skill_id) REFERENCES skills(skill_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS analysis_reports (
  report_id INT PRIMARY KEY AUTO_INCREMENT,
  candidate_id INT NOT NULL,
  role_id INT NOT NULL,
  match_percentage DECIMAL(5,2) NOT NULL,
  matched_skills TEXT,
  missing_skills TEXT,
  analyzed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (candidate_id) REFERENCES candidates(candidate_id) ON DELETE CASCADE,
  FOREIGN KEY (role_id) REFERENCES job_roles(role_id) ON DELETE CASCADE
);

-- Starter job roles and required skills. Safe to run more than once.
INSERT IGNORE INTO job_roles(role_name, description) VALUES
('Java Developer', 'Develop Java applications and backend services'),
('Web Developer', 'Build and maintain web applications'),
('Data Analyst', 'Analyze data and create reports'),
('Network Engineer', 'Configure, maintain and troubleshoot networks');

INSERT IGNORE INTO skills(skill_name) VALUES
('Java'),('OOP'),('Collections'),('JDBC'),('MySQL'),('Spring Boot'),('REST APIs'),('Git'),('HTML'),('CSS'),('JavaScript'),('React'),('SQL'),('Excel'),('Python'),('Statistics'),('Power BI'),('Networking'),('TCP/IP'),('Subnetting'),('Linux'),('Troubleshooting');

INSERT IGNORE INTO role_skills(role_id, skill_id, importance)
SELECT r.role_id, s.skill_id, 'REQUIRED' FROM job_roles r JOIN skills s
WHERE (r.role_name='Java Developer' AND s.skill_name IN ('Java','OOP','Collections','JDBC','MySQL','Spring Boot','REST APIs','Git'))
   OR (r.role_name='Web Developer' AND s.skill_name IN ('HTML','CSS','JavaScript','React','REST APIs','Git','MySQL'))
   OR (r.role_name='Data Analyst' AND s.skill_name IN ('SQL','Excel','Python','Statistics','Power BI'))
   OR (r.role_name='Network Engineer' AND s.skill_name IN ('Networking','TCP/IP','Subnetting','Linux','Troubleshooting'));

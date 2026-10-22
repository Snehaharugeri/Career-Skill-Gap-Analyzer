package com.career.app;

import com.career.dao.CandidateDAO;
import com.career.dao.JobRoleDAO;
import com.career.model.Candidate;
import com.career.model.JobRole;
import com.career.service.SkillGapService;
import com.career.util.DBConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner sc = new Scanner(System.in);
    private static final CandidateDAO candidateDAO = new CandidateDAO();
    private static final JobRoleDAO jobRoleDAO = new JobRoleDAO();
    private static final SkillGapService service = new SkillGapService();

    public static void main(String[] args) {
        try (Connection ignored = DBConnection.getConnection()) {
            System.out.println("MySQL connection successful.");
        } catch (SQLException e) {
            System.out.println("Cannot connect to MySQL. Check MySQL is running, schema is imported, and MYSQL_PASSWORD is set.");
            System.out.println("Details: " + e.getMessage());
            return;
        }

        int choice;
        do {
            printMenu();
            choice = readInt("Enter choice: ");
            try {
                switch (choice) {
                    case 1 -> registerCandidate();
                    case 2 -> viewCandidates();
                    case 3 -> addSkill();
                    case 4 -> viewJobRoles();
                    case 5 -> analyzeSkills();
                    case 6 -> System.out.println("Thank you for using Career Skill Gap Analyzer!");
                    default -> System.out.println("Invalid choice. Enter a number from 1 to 6.");
                }
            } catch (SQLException e) {
                if (e.getErrorCode() == 1062) System.out.println("That email is already registered.");
                else System.out.println("Database error: " + e.getMessage());
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } while (choice != 6);
    }

    private static void printMenu() {
        System.out.println("\n========================================");
        System.out.println("       CAREER SKILL GAP ANALYZER");
        System.out.println("========================================");
        System.out.println("1. Register Candidate");
        System.out.println("2. View Candidates");
        System.out.println("3. Add or Update Candidate Skill");
        System.out.println("4. View Job Roles and Required Skills");
        System.out.println("5. Analyze Skill Gap");
        System.out.println("6. Exit");
        System.out.println("========================================");
    }

    private static void registerCandidate() throws SQLException {
        String name = readNonEmpty("Full name: ");
        String email = readNonEmpty("Email: ");
        String education = readNonEmpty("Education (e.g., BE CSE): ");
        int id = candidateDAO.add(new Candidate(name, email, education));
        System.out.println("Candidate registered successfully. Your Candidate ID is: " + id);
    }

    private static void viewCandidates() throws SQLException {
        List<Candidate> candidates = candidateDAO.findAll();
        if (candidates.isEmpty()) { System.out.println("No candidates registered yet."); return; }
        System.out.println("\nID | Name | Email | Education");
        System.out.println("------------------------------------------------------------");
        for (Candidate c : candidates)
            System.out.printf("%d | %s | %s | %s%n", c.getId(), c.getFullName(), c.getEmail(), c.getEducation());
    }

    private static void addSkill() throws SQLException {
        int id = readInt("Candidate ID: ");
        String skill = readNonEmpty("Skill name (e.g., Java): ");
        System.out.println("1. Beginner  2. Intermediate  3. Advanced");
        int level = readInt("Choose proficiency: ");
        String proficiency = switch (level) {
            case 1 -> "BEGINNER"; case 2 -> "INTERMEDIATE"; case 3 -> "ADVANCED";
            default -> throw new IllegalArgumentException("Choose proficiency 1, 2, or 3.");
        };
        service.addSkill(id, skill, proficiency);
        System.out.println("Skill saved successfully.");
    }

    private static void viewJobRoles() throws SQLException {
        List<JobRole> roles = jobRoleDAO.findAll();
        System.out.println("\nAvailable job roles:");
        for (JobRole role : roles) {
            System.out.println(role);
        }
        System.out.println("Required skills:");
        for (JobRole role : roles) {
            System.out.println("\n" + role.getId() + ". " + role.getName());
            // Reuse the skill comparison workflow through a simple query helper.
            try (Connection con = DBConnection.getConnection();
                 var ps = con.prepareStatement("SELECT s.skill_name FROM role_skills rs JOIN skills s ON s.skill_id=rs.skill_id WHERE rs.role_id=? ORDER BY s.skill_name")) {
                ps.setInt(1, role.getId());
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) System.out.println("   - " + rs.getString(1));
                }
            }
        }
    }

    private static void analyzeSkills() throws SQLException {
        int candidateId = readInt("Candidate ID: ");
        viewJobRoles();
        int roleId = readInt("Select target job role ID: ");
        SkillGapService.Report report = service.analyze(candidateId, roleId);
        System.out.println("\n========== SKILL GAP REPORT ==========");
        System.out.println("Target role: " + report.roleName());
        System.out.printf("Skill match: %.2f%%%n", report.percentage());
        System.out.println("Matched skills: " + (report.matched().isEmpty() ? "None" : String.join(", ", report.matched())));
        System.out.println("Missing skills: " + (report.missing().isEmpty() ? "None" : String.join(", ", report.missing())));
        System.out.println("\nLearning recommendations:");
        if (report.missing().isEmpty()) System.out.println("You have all configured required skills. Practice projects and interview questions to validate your proficiency.");
        else for (int i = 0; i < report.missing().size(); i++)
            System.out.println((i + 1) + ". Learn and practice " + report.missing().get(i) + ". Build a small project using this skill.");
        System.out.println("Report saved to MySQL.");
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = sc.nextLine().trim();
            try { return Integer.parseInt(value); }
            catch (NumberFormatException e) { System.out.println("Please enter a valid whole number."); }
        }
    }

    private static String readNonEmpty(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = sc.nextLine().trim();
            if (!value.isEmpty()) return value;
            System.out.println("This field cannot be empty.");
        }
    }
}

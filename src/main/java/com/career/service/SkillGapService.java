package com.career.service;

import com.career.dao.AnalysisDAO;
import com.career.dao.CandidateDAO;
import com.career.dao.JobRoleDAO;
import com.career.dao.SkillDAO;
import com.career.model.JobRole;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SkillGapService {
    private final CandidateDAO candidateDAO = new CandidateDAO();
    private final JobRoleDAO jobRoleDAO = new JobRoleDAO();
    private final SkillDAO skillDAO = new SkillDAO();
    private final AnalysisDAO analysisDAO = new AnalysisDAO();

    public record Report(String roleName, double percentage, List<String> matched, List<String> missing, Map<String,String> candidateSkills) {}

    public void addSkill(int candidateId, String skill, String proficiency) throws SQLException {
        if (!candidateDAO.exists(candidateId)) throw new IllegalArgumentException("Candidate ID not found.");
        skillDAO.addOrUpdateCandidateSkill(candidateId, skill, proficiency);
    }

    public Report analyze(int candidateId, int roleId) throws SQLException {
        if (!candidateDAO.exists(candidateId)) throw new IllegalArgumentException("Candidate ID not found.");
        JobRole role = jobRoleDAO.findById(roleId);
        if (role == null) throw new IllegalArgumentException("Job role ID not found.");
        List<String> required = skillDAO.getRequiredSkills(roleId);
        if (required.isEmpty()) throw new IllegalStateException("No required skills configured for this role.");
        Map<String,String> candidateSkills = skillDAO.getCandidateSkills(candidateId);
        List<String> matched = new ArrayList<>(), missing = new ArrayList<>();
        for (String skill : required) {
            if (candidateSkills.containsKey(skill)) matched.add(skill);
            else missing.add(skill);
        }
        double percentage = Math.round((matched.size() * 10000.0) / required.size()) / 100.0;
        analysisDAO.save(candidateId, roleId, percentage, matched, missing);
        return new Report(role.getName(), percentage, matched, missing, candidateSkills);
    }
}

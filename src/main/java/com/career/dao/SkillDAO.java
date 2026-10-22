package com.career.dao;

import com.career.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SkillDAO {
    public void addOrUpdateCandidateSkill(int candidateId, String skillName, String proficiency) throws SQLException {
        int skillId;
        try (Connection con = DBConnection.getConnection()) {
            try (PreparedStatement add = con.prepareStatement("INSERT IGNORE INTO skills(skill_name) VALUES(?)")) {
                add.setString(1, skillName.trim()); add.executeUpdate();
            }
            try (PreparedStatement find = con.prepareStatement("SELECT skill_id FROM skills WHERE LOWER(skill_name)=LOWER(?)")) {
                find.setString(1, skillName.trim());
                try (ResultSet rs = find.executeQuery()) { if (!rs.next()) throw new SQLException("Could not find skill."); skillId = rs.getInt(1); }
            }
            String sql = "INSERT INTO candidate_skills(candidate_id,skill_id,proficiency) VALUES(?,?,?) ON DUPLICATE KEY UPDATE proficiency=VALUES(proficiency)";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, candidateId); ps.setInt(2, skillId); ps.setString(3, proficiency); ps.executeUpdate();
            }
        }
    }

    public Map<String, String> getCandidateSkills(int candidateId) throws SQLException {
        Map<String, String> skills = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        String sql = "SELECT s.skill_name,cs.proficiency FROM candidate_skills cs JOIN skills s ON s.skill_id=cs.skill_id WHERE cs.candidate_id=? ORDER BY s.skill_name";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, candidateId); try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) skills.put(rs.getString("skill_name"), rs.getString("proficiency"));
            }
        }
        return skills;
    }

    public List<String> getRequiredSkills(int roleId) throws SQLException {
        List<String> skills = new ArrayList<>();
        String sql = "SELECT s.skill_name FROM role_skills rs JOIN skills s ON s.skill_id=rs.skill_id WHERE rs.role_id=? AND rs.importance='REQUIRED' ORDER BY s.skill_name";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, roleId); try (ResultSet rs = ps.executeQuery()) { while (rs.next()) skills.add(rs.getString(1)); }
        }
        return skills;
    }
}

package com.career.dao;

import com.career.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class AnalysisDAO {
    public void save(int candidateId, int roleId, double percentage, List<String> matched, List<String> missing) throws SQLException {
        String sql = "INSERT INTO analysis_reports(candidate_id,role_id,match_percentage,matched_skills,missing_skills) VALUES(?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, candidateId); ps.setInt(2, roleId); ps.setDouble(3, percentage);
            ps.setString(4, String.join(", ", matched)); ps.setString(5, String.join(", ", missing)); ps.executeUpdate();
        }
    }
}

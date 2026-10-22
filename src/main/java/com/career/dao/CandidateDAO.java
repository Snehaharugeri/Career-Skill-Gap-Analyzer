package com.career.dao;

import com.career.model.Candidate;
import com.career.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CandidateDAO {
    public int add(Candidate candidate) throws SQLException {
        String sql = "INSERT INTO candidates(full_name,email,education) VALUES(?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, candidate.getFullName()); ps.setString(2, candidate.getEmail()); ps.setString(3, candidate.getEducation());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) { if (keys.next()) return keys.getInt(1); }
        }
        throw new SQLException("Candidate was added but no ID was returned.");
    }

    public List<Candidate> findAll() throws SQLException {
        List<Candidate> result = new ArrayList<>();
        String sql = "SELECT candidate_id,full_name,email,education FROM candidates ORDER BY candidate_id";
        try (Connection con = DBConnection.getConnection(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) result.add(new Candidate(rs.getInt("candidate_id"), rs.getString("full_name"), rs.getString("email"), rs.getString("education")));
        }
        return result;
    }

    public boolean exists(int id) throws SQLException {
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement("SELECT 1 FROM candidates WHERE candidate_id=?")) {
            ps.setInt(1, id); try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }
}

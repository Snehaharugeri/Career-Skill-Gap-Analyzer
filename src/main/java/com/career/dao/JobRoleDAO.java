package com.career.dao;

import com.career.model.JobRole;
import com.career.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JobRoleDAO {
    public List<JobRole> findAll() throws SQLException {
        List<JobRole> roles = new ArrayList<>();
        String sql = "SELECT role_id,role_name,description FROM job_roles ORDER BY role_id";
        try (Connection con = DBConnection.getConnection(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) roles.add(new JobRole(rs.getInt("role_id"), rs.getString("role_name"), rs.getString("description")));
        }
        return roles;
    }

    public JobRole findById(int id) throws SQLException {
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement("SELECT role_id,role_name,description FROM job_roles WHERE role_id=?")) {
            ps.setInt(1, id); try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new JobRole(rs.getInt("role_id"), rs.getString("role_name"), rs.getString("description"));
            }
        }
        return null;
    }
}

package com.example;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;

public class UserServlet extends HttpServlet {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/vamsi?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String JDBC_USERNAME = "root";
    private static final String JDBC_PASSWORD = "Durga$10";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Load MySQL driver
            try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM users")) {

                out.println("<html><body><h1>Users List</h1><table>");
                out.println("<tr><th>ID</th><th>Name</th><th>Email</th></tr>");
                while (rs.next()) {
                    out.println("<tr><td>" + rs.getInt("id") + "</td><td>" + rs.getString("name") + "</td><td>" + rs.getString("email") + "</td></tr>");
                }
                out.println("</table>");
                out.println("<h2>Add New User</h2>");
                out.println("<form action='users' method='POST'>");
                out.println("Name: <input type='text' name='name'><br>");
                out.println("Email: <input type='email' name='email'><br>");
                out.println("<input type='submit' value='Add User'>");
                out.println("</form>");
                out.println("</body></html>");
            }
        } catch (ClassNotFoundException e) {
            out.println("<p>Error: MySQL Driver not found.</p>");
            e.printStackTrace();
        } catch (SQLException e) {
            out.println("<p>Error: " + e.getMessage() + "</p>");
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");

        if (name != null && !name.isEmpty() && email != null && !email.isEmpty()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver"); // Load MySQL driver
                try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD);
                     PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (name, email) VALUES (?, ?)")) {

                    stmt.setString(1, name);
                    stmt.setString(2, email);
                    int rowsAffected = stmt.executeUpdate();

                    if (rowsAffected > 0) {
                        response.sendRedirect("users");
                    } else {
                        response.getWriter().println("<p>Error adding user.</p>");
                    }
                }
            } catch (ClassNotFoundException e) {
                response.getWriter().println("<p>Error: MySQL Driver not found.</p>");
                e.printStackTrace();
            } catch (SQLException e) {
                response.getWriter().println("<p>Error: " + e.getMessage() + "</p>");
                e.printStackTrace();
            }
        } else {
            response.getWriter().println("<p>Both name and email are required.</p>");
        }
    }
}


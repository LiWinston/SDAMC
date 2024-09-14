package org.sdamc.Controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.sdamc.DTO.LoginResponse;
import org.sdamc.DTO.Result;
import org.sdamc.DTO.UserDTO;
import org.sdamc.DomainObject.Clubs;
import org.sdamc.DomainObject.Students;
import org.sdamc.Mapper.ClubMembershipsMapper;
import org.sdamc.Mapper.StudentsMapper;
import org.sdamc.UnitofWork;
import org.sdamc.Utils.Constants;

import java.io.IOException;
import java.util.List;

@Slf4j
@WebServlet(name = "UserController", urlPatterns = { "/user/*" })
public class UserController extends HttpServlet {

    ClubMembershipsMapper clubMembershipsMapper;

    private StudentsMapper studentsMapper;

    @Override
    public void init() throws ServletException {
        studentsMapper = new StudentsMapper();
        clubMembershipsMapper = new ClubMembershipsMapper();
    }

    @Override
    // 获取用户管辖的club /user/{stuid}/clubs
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String requestURI = req.getRequestURI();
        if (requestURI.endsWith("/clubs")) {
            handleGetClubsAdminedByUser(req, resp);
        }
        else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void handleGetClubsAdminedByUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo(); // "/123/clubs"
        String[] parts = pathInfo.split("/");

        UnitofWork.newCurrent();
        if (parts.length >= 2) {
            int stuid = Integer.parseInt(parts[1]); // parts[1] is "123"
            List<Clubs> clubs = clubMembershipsMapper.findClubsAdminedByStudent(stuid);
            resp.setContentType("application/json");
            new ObjectMapper().writeValue(resp.getOutputStream(), clubs);
        }
        else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid path");
        }
        UnitofWork.getCurrent().commit();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestURI = req.getRequestURI();

        if (requestURI.endsWith("/login")) {
            handleLogin(req, resp);
        }
        else if (requestURI.endsWith("/register")) {
            handleRegister(req, resp);
        }
        else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        UserDTO userDTO = mapper.readValue(req.getInputStream(), UserDTO.class);

        String email = userDTO.getEmail();
        String name = userDTO.getUsername();
        String password = userDTO.getPassword();

        if (name == null || name.isEmpty()) {
            name = email;
        }

        try {
            UnitofWork.newCurrent();

            if (studentsMapper.findByEmail(email) != null) {
                // 返回 Result<T> 错误信息
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                resp.setContentType("application/json");
                mapper.writeValue(resp.getOutputStream(), Result.error("Email already registered"));
                return;
            }

            // 插入新用户
            Students student = new Students(studentsMapper.getNewId());
            student.setEmail(email);
            student.setName(name);
            student.setPassword(password);

            studentsMapper.insert(student);
            UnitofWork.getCurrent().commit();

            // 注册成功时返回 Result<T> 成功消息
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.setContentType("application/json");
            mapper.writeValue(resp.getOutputStream(), Result.success(null, "User registered successfully"));
        }
        catch (Exception e) {
            // 返回 Result<T> 错误信息
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json");
            mapper.writeValue(resp.getOutputStream(), Result.error("Database error" + e.getMessage().substring(0, 50)));
            e.printStackTrace();
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        UserDTO userDTO = mapper.readValue(req.getInputStream(), UserDTO.class);

        String email = userDTO.getEmail();
        String password = userDTO.getPassword();

        try {
            UnitofWork.newCurrent();
            Students student = studentsMapper.findByEmail(email);

            if (student != null && student.getPassword().equals(password)) {
                String token = generateJwtToken(student.getId());
                // 登录成功时返回 Result<LoginResponse>
                resp.setContentType("application/json");
                mapper.writeValue(resp.getOutputStream(),
                        Result.success(new LoginResponse(token, student.getId()), "Login successful"));
            }
            else {
                // 返回 Result<T> 错误信息
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.setContentType("application/json");
                mapper.writeValue(resp.getOutputStream(), Result.error("Invalid email or password"));
            }

            UnitofWork.getCurrent().commit();
        }
        catch (Exception e) {
            // 返回 Result<T> 错误信息
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json");
            mapper.writeValue(resp.getOutputStream(), Result.error("Database error"));
            e.printStackTrace();
        }
    }

    // JWT 生成逻辑
    private String generateJwtToken(int studentId) {
        return JWT.create().withClaim("id", studentId).sign(Algorithm.HMAC256(Constants.JWT_SECRET));
    }

}

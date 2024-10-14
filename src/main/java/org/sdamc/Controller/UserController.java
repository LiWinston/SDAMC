package org.sdamc.Controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
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
import org.sdamc.Mapper.AdminMapper;
import org.sdamc.Mapper.ClubMembershipsMapper;
import org.sdamc.Mapper.StudentsMapper;
import org.sdamc.UnitofWork;
import org.sdamc.Utils.Constants;
import org.sdamc.Utils.IOWrapper;

import java.io.IOException;
import java.util.List;

@Slf4j
@WebServlet(name = "UserController", urlPatterns = { "/user/*" })
public class UserController extends HttpServlet {

    private ClubMembershipsMapper clubMembershipsMapper;

    private StudentsMapper studentsMapper;

    private AdminMapper adminMapper;

    @Override
    public void init() {
        studentsMapper = new StudentsMapper();
        clubMembershipsMapper = new ClubMembershipsMapper();
        adminMapper = new AdminMapper();
    }

    @Override
    // 获取用户管辖的club /user/{stuid}/clubs
    // 获取用户以超级管理员身份管辖的club /user/{stuid}/clubs_super
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            String requestURI = req.getRequestURI();
            if (requestURI.endsWith("/clubs")) {
                handleGetClubsAdminedByUser(req, resp);
            }
            else if (requestURI.endsWith("/clubs_super")) {
                handleGetClubsSuperAdminedByUser(req, resp);
            }
            else {
                IOWrapper.writeValue(resp, Result.error("Invalid path"));
            }
        }
        catch (IOException e) {
            log.error("搞毛啊，IOException");
        }
    }

    private void handleGetClubsAdminedByUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo(); // "/123/clubs"
        String[] parts = pathInfo.split("/");

        UnitofWork.newCurrent();
        if (parts.length >= 2) {
            int stuid = Integer.parseInt(parts[1]); // parts[1] is "123"
            List<Clubs> clubs = clubMembershipsMapper.findClubsAdminedByStudent(stuid);
            IOWrapper.writeValue(resp, clubs);
        }
        else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid path");
        }
        UnitofWork.getCurrent().commit();
    }

    private void handleGetClubsSuperAdminedByUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo(); // "/123/clubs_super"
        String[] parts = pathInfo.split("/");

        UnitofWork.newCurrent();
        if (parts.length >= 2) {
            int stuid = Integer.parseInt(parts[1]); // parts[1] is "123"
            List<Clubs> clubs = clubMembershipsMapper.findClubsSuperAdminedByStudent(stuid);
            IOWrapper.writeValue(resp, clubs);
        }
        else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid path");
        }
        UnitofWork.getCurrent().commit();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        String requestURI = req.getRequestURI();
        try {
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
        catch (IOException e) {
            log.error("搞毛啊，流都读不了");// in case IOException混淆视听
        }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        UserDTO userDTO = IOWrapper.readValue(req, UserDTO.class);

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
                // resp.setStatus(HttpServletResponse.SC_CONFLICT);
                resp.setContentType("application/json");
                IOWrapper.writeValue(resp, Result.error("Email already registered"), HttpServletResponse.SC_CONFLICT);
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
            // resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.setContentType("application/json");
            IOWrapper.writeValue(resp, Result.success("Registration successful"), HttpServletResponse.SC_CREATED);
        }
        catch (Exception e) {
            // 返回 Result<T> 错误信息
            // resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json");
            IOWrapper.writeValue(resp, Result.error("Database error"), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserDTO userDTO = IOWrapper.readValue(req, UserDTO.class);

        String email = userDTO.getEmail();
        String password = userDTO.getPassword();

        try {
            UnitofWork.newCurrent();
            Students student = studentsMapper.findByEmail(email);

            if (student != null && student.getPassword().equals(password)) {
                String token = generateJwtToken(student.getId());
                // 登录成功时返回 Result<LoginResponse>
                String isFacultyAdministrator = adminMapper.isAdmin(email, password) ? "FacultyAdministrator"
                        : "Student";
                IOWrapper.writeValue(resp, Result.success(new LoginResponse(token, student.getId(), student.getName()),
                        "Login successful as " + isFacultyAdministrator));
            }
            else {
                // 返回 Result<T> 错误信息
                // resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                IOWrapper.writeValue(resp, Result.error("Invalid email or password"),
                        HttpServletResponse.SC_UNAUTHORIZED);
            }

            UnitofWork.getCurrent().commit();
        }
        catch (Exception e) {
            // 返回 Result<T> 错误信息
            // resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json");
            IOWrapper.writeValue(resp, Result.error("Database error" + e),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    // JWT 生成逻辑
    private String generateJwtToken(int studentId) {
        return JWT.create().withClaim("id", studentId).sign(Algorithm.HMAC256(Constants.JWT_SECRET));
    }

}

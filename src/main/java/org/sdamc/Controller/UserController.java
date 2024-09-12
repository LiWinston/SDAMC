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
import org.sdamc.DomainObject.Students;
import org.sdamc.Mapper.StudentsMapper;
import org.sdamc.UnitofWork;

import java.io.IOException;

@Slf4j
@WebServlet(name = "UserController", urlPatterns = { "/user/*" })
public class UserController extends HttpServlet {

    private StudentsMapper studentsMapper;

    @Override
    public void init() throws ServletException {
        // 初始化Mapper，可以通过某种依赖注入机制，或者直接实例化
        studentsMapper = new StudentsMapper(); // 假设你有一个 StudentsMapper 类
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
            mapper.writeValue(resp.getOutputStream(), Result.success("User registered successfully"));
        }
        catch (Exception e) {
            // 返回 Result<T> 错误信息
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json");
            mapper.writeValue(resp.getOutputStream(), Result.error("Database error"));
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
        return JWT.create().withClaim("id", studentId).sign(Algorithm.HMAC256("secret_key")); // secret_key请换成你实际的密钥
    }

}

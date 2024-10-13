package org.sdamc.Controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.sdamc.DTO.Fundings;
import org.sdamc.DTO.Result;
import org.sdamc.DomainObject.Clubs;
import org.sdamc.DomainObject.FundingApplication;
import org.sdamc.DomainObject.Students;
import org.sdamc.Mapper.*;
import org.sdamc.UnitofWork;
import org.sdamc.Utils.Constants;
import org.sdamc.Utils.IOWrapper;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;
import static org.sdamc.Utils.JwtUtil.VerifyToken;

@Slf4j
@WebServlet(name = "FundingApplicationController", urlPatterns = { "/funding/*" })
public class FundingApplicationController extends HttpServlet {

    FundingApplicationMapper fundingApplicationMapper;

    private StudentsMapper studentsMapper;

    private ClubMembershipsMapper membershipsMapper;

    private ClubsMapper clubsMapper;

    private AdminMapper adminMapper;

    @Override
    public void init() {
        studentsMapper = new StudentsMapper();
        membershipsMapper = new ClubMembershipsMapper();
        fundingApplicationMapper = new FundingApplicationMapper();
        clubsMapper = new ClubsMapper();
        adminMapper = new AdminMapper();
    }

    @Override
    // get all fundings /funding/all
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            String requestURI = req.getRequestURI();
            if (requestURI.endsWith("/all")) {
                handleGetFundings(req, resp);
            }
            else if (requestURI.endsWith("/club")) {
                handleGetFundingsByClub(req, resp);
            }
            else {
                IOWrapper.writeValue(resp, Result.error("Invalid path"));
            }
        }
        catch (IOException e) {
            log.error("搞毛啊，IOException");
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleGetFundings(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        try {
            UnitofWork.newCurrent();
            List<FundingApplication> applications = fundingApplicationMapper.getAll();
            for (FundingApplication application : applications) {
                application.setStatus("in_review");
                fundingApplicationMapper.update(application);
            }
            List<Fundings> result = new ArrayList<Fundings>();
            for (FundingApplication application : applications) {
                int id = application.getId();
                Clubs club = (Clubs) clubsMapper.find(application.getClubId());
                String clubName = club.getName();
                Students student = (Students) studentsMapper.find(application.getStudentId());
                String applicant = student.getName();
                String description = application.getDescription();
                float amount = application.getAmount();
                String status = application.getStatus();
                Fundings funding = new Fundings(id, clubName, applicant, description, amount, status);
                result.add(funding);
            }
            IOWrapper.writeValue(resp, result);
            UnitofWork.getCurrent().commit();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleGetFundingsByClub(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, SQLException {
        String pathInfo = req.getPathInfo(); // "funding/1/club"
        String[] parts = pathInfo.split("/");

        UnitofWork.newCurrent();
        if (parts.length >= 2) {
            int clubId = parseInt(parts[1]); // parts[1] is "1"
            List<FundingApplication> applications = fundingApplicationMapper.findByClubId(clubId);
            IOWrapper.writeValue(resp, applications);
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
            if (requestURI.endsWith("/update")) {
                handleUpdate(req, resp);
            }
            else if (requestURI.endsWith("/submit")) {
                handleSubmit(req, resp);
            }
            else if (requestURI.endsWith("/cancel")) {
                handleCancel(req, resp);
            }
            else if (requestURI.endsWith("/approve")) {
                handleApprove(req, resp);
            }
            else if (requestURI.endsWith("/reject")) {
                handleReject(req, resp);
            }
            else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        }
        catch (IOException e) {
            log.error("搞毛啊，流都读不了");// in case IOException混淆视听
        }
    }

    private void handleUpdate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> requestBody = mapper.readValue(req.getInputStream(), Map.class);
        try {
            int fundingId = parseInt(requestBody.get("id"));
            UnitofWork.newCurrent();
            FundingApplication application = (FundingApplication) fundingApplicationMapper.find(fundingId);
            if (application != null) {
                // application.setRole("admin");
                String description = requestBody.get("description");
                Float amount = Float.parseFloat(requestBody.get("amount"));
                application.setDescription(description);
                application.setAmount(amount);
                fundingApplicationMapper.update(application);
                UnitofWork.getCurrent().commit();
                new ObjectMapper().writeValue(resp.getOutputStream(),
                        Result.success(null, "Funding application: " + application.getId() + " updated successfully"));
            }
            else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Funding not found");
            }
        }
        catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json");
            new ObjectMapper().writeValue(resp.getOutputStream(), Result.error("Database error"));
            e.printStackTrace();
        }
    }

    private void handleSubmit(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String token = req.getHeader("Authorization");
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> requestBody = mapper.readValue(req.getInputStream(), Map.class);
        int studentId = parseInt(requestBody.get("studentId"));
        log("studentId: " + studentId);

        // 验证 token 和 studentId
        if (!VerifyToken(req, resp, studentId)) {
            // resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
            return;
        }

        int clubId = parseInt(requestBody.get("clubId"));
        try {
            UnitofWork.newCurrent();
            // 检查用户是否为此社团的管理员
            if (!membershipsMapper.isAdmin(studentId, clubId)) {
                resp.setContentType("application/json");
                new ObjectMapper().writeValue(resp.getOutputStream(),
                        Result.error("You are not the admin of this club"));
                return;
            }

            // 获取事件信息
            String description = requestBody.get("description");
            Float amount = Float.parseFloat(requestBody.get("amount"));
            String status = "submitted";

            // 创建事件
            FundingApplication application = new FundingApplication(fundingApplicationMapper.getNewId());
            application.setDescription(description);
            application.setAmount(amount);
            application.setStudentId(studentId);
            application.setClubId(clubId);
            application.setStatus(status);

            fundingApplicationMapper.insert(application);
            UnitofWork.getCurrent().commit();

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.setContentType("application/json");
            new ObjectMapper().writeValue(resp.getOutputStream(),
                    Result.success(null, "Funding application: " + application.getId() + " submitted successfully"));
        }
        catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json");
            new ObjectMapper().writeValue(resp.getOutputStream(), Result.error("Database error"));
            e.printStackTrace();
        }
    }

    private void handleCancel(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo(); // "funding/1/cancel"
        String[] parts = pathInfo.split("/");

        UnitofWork.newCurrent();
        if (parts.length >= 2) {
            int id = parseInt(parts[1]); // parts[1] is "1"
            FundingApplication application = (FundingApplication) fundingApplicationMapper.find(id);
            fundingApplicationMapper.delete(application);
            new ObjectMapper().writeValue(resp.getOutputStream(),
                    Result.success(null, "Funding application: " + application.getId() + " canceled successfully"));
        }
        else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid path");
        }
        UnitofWork.getCurrent().commit();
    }

    private void handleApprove(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo(); // "funding/1/approve"
        String[] parts = pathInfo.split("/");
        String userId = new ObjectMapper().readTree(req.getInputStream()).get("userId").asText();

        UnitofWork.newCurrent();

        // 检查路径是否合法
        if (parts.length >= 2) {
            // 从 StudentsMapper 获取用户 email 和密码
            Students student = (Students) studentsMapper.find(Integer.parseInt(userId));

            if (student == null) {
                IOWrapper.writeValue(resp, Result.error("User not found"), HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            // 检查是否为管理员
            if (!adminMapper.isAdmin(student.getEmail(), student.getPassword())) {
                IOWrapper.writeValue(resp, Result.error("Operation not permitted, " + student.getEmail() + " "
                        + student.getPassword() + " is Not FundingAdmin"), HttpServletResponse.SC_OK);
                return;
            }

            int id = parseInt(parts[1]); // parts[1] is "1"
            FundingApplication application = (FundingApplication) fundingApplicationMapper.find(id);
            application.setStatus("approved");
            fundingApplicationMapper.update(application);

            IOWrapper.writeValue(resp,
                    Result.success(null, "Funding application: " + application.getId() + " approved"));
        }
        else {
            IOWrapper.writeValue(resp, Result.error("Invalid path"), HttpServletResponse.SC_BAD_REQUEST);
        }

        UnitofWork.getCurrent().commit();
    }

    private void handleReject(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo(); // "funding/1/reject"
        String[] parts = pathInfo.split("/");
        String userId = new ObjectMapper().readTree(req.getInputStream()).get("userId").asText();
        log.warn("userId: " + userId);
        UnitofWork.newCurrent();

        // 检查路径是否合法
        if (parts.length >= 2) {
            // 从 StudentsMapper 获取用户 email 和密码
            Students student = (Students) studentsMapper.find(parseInt(userId));
            log.warn("student: " + student);

            if (student == null) {
                IOWrapper.writeValue(resp, Result.error("User not found"), HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            // 检查是否为管理员
            if (!adminMapper.isAdmin(student.getEmail(), student.getPassword())) {
                IOWrapper.writeValue(resp, Result.error("Operation not permitted, " + student.getEmail() + " "
                        + student.getPassword() + " is Not FundingAdmin"), HttpServletResponse.SC_OK);
                return;
            }

            int id = parseInt(parts[1]); // parts[1] is "1"
            FundingApplication application = (FundingApplication) fundingApplicationMapper.find(id);
            application.setStatus("rejected");
            fundingApplicationMapper.update(application);

            IOWrapper.writeValue(resp,
                    Result.success(null, "Funding application: " + application.getId() + " rejected"));
        }
        else {
            IOWrapper.writeValue(resp, Result.error("Invalid path"), HttpServletResponse.SC_BAD_REQUEST);
        }

        UnitofWork.getCurrent().commit();
    }

    // JWT 生成逻辑
    private String generateJwtToken(int studentId) {
        return JWT.create().withClaim("id", studentId).sign(Algorithm.HMAC256(Constants.JWT_SECRET));
    }

}

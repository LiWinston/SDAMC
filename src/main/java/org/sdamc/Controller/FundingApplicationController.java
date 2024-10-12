package org.sdamc.Controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.sdamc.DTO.ClubMember;
import org.sdamc.DTO.Result;
import org.sdamc.DomainObject.Events;
import org.sdamc.DomainObject.FundingApplication;
import org.sdamc.Mapper.ClubMembershipsMapper;
import org.sdamc.Mapper.FundingApplicationMapper;
import org.sdamc.Mapper.StudentsMapper;
import org.sdamc.UnitofWork;
import org.sdamc.Utils.Constants;
import org.sdamc.Utils.IOWrapper;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import static org.sdamc.Utils.JwtUtil.VerifyToken;

@Slf4j
@WebServlet(name = "FundingApplicationController", urlPatterns = { "/funding/*" })
public class FundingApplicationController extends HttpServlet {

    FundingApplicationMapper fundingApplicationMapper;

    private StudentsMapper studentsMapper;

    private ClubMembershipsMapper membershipsMapper;

    @Override
    public void init() {
        studentsMapper = new StudentsMapper();
        membershipsMapper = new ClubMembershipsMapper();
        fundingApplicationMapper = new FundingApplicationMapper();
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
        UnitofWork.newCurrent();
        List<FundingApplication> applications = fundingApplicationMapper.getAll();
        IOWrapper.writeValue(resp, applications);
        UnitofWork.getCurrent().commit();
    }

    private void handleGetFundingsByClub(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, SQLException {
        String pathInfo = req.getPathInfo(); // "funding/1/club"
        String[] parts = pathInfo.split("/");

        UnitofWork.newCurrent();
        if (parts.length >= 2) {
            int clubId = Integer.parseInt(parts[1]); // parts[1] is "1"
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
            int fundingId = Integer.parseInt(requestBody.get("id"));
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
        int studentId = Integer.parseInt(requestBody.get("studentId"));
        log("studentId: " + studentId);

        // 验证 token 和 studentId
        if (!VerifyToken(req, resp, studentId)) {
            // resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
            return;
        }

        int clubId = Integer.parseInt(requestBody.get("clubId"));
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
            int id = Integer.parseInt(parts[1]); // parts[1] is "1"
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

    // JWT 生成逻辑
    private String generateJwtToken(int studentId) {
        return JWT.create().withClaim("id", studentId).sign(Algorithm.HMAC256(Constants.JWT_SECRET));
    }

}

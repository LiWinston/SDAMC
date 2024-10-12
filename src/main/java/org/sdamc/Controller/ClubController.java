package org.sdamc.Controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.sdamc.DTO.ClubMember;
import org.sdamc.DTO.Result;
import org.sdamc.DomainObject.ClubMemberships;
import org.sdamc.Mapper.ClubMembershipsMapper;
import org.sdamc.Mapper.StudentsMapper;
import org.sdamc.UnitofWork;
import org.sdamc.Utils.Constants;
import org.sdamc.Utils.IOWrapper;

import java.io.IOException;
import java.util.List;

@Slf4j
@WebServlet(name = "ClubController", urlPatterns = { "/club/*" })
public class ClubController extends HttpServlet {

    ClubMembershipsMapper clubMembershipsMapper;

    private StudentsMapper studentsMapper;

    @Override
    public void init() {
        studentsMapper = new StudentsMapper();
        clubMembershipsMapper = new ClubMembershipsMapper();
    }

    @Override
    // get all members in a club /club/{clubid}/all
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            String requestURI = req.getRequestURI();
            if (requestURI.endsWith("/all")) {
                handleGetClubMembers(req, resp);
            }
            else {
                IOWrapper.writeValue(resp, Result.error("Invalid path"));
            }
        }
        catch (IOException e) {
            log.error("搞毛啊，IOException");
        }
    }

    private void handleGetClubMembers(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo(); // "/1/all"
        String[] parts = pathInfo.split("/");

        UnitofWork.newCurrent();
        if (parts.length >= 2) {
            int clubid = Integer.parseInt(parts[1]); // parts[1] is "1"
            List<ClubMember> members = clubMembershipsMapper.getClubMembers(clubid);
            IOWrapper.writeValue(resp, members);
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
            if (requestURI.endsWith("/admin")) {
                handleSet(req, resp);
            }
            else if (requestURI.endsWith("/normal_member")) {
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

    private void handleSet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo(); // "club/1/admin"
        String[] parts = pathInfo.split("/");

        UnitofWork.newCurrent();
        if (parts.length >= 2) {
            int cmid = Integer.parseInt(parts[1]); // parts[1] is "1"
            ClubMemberships cm = (ClubMemberships) clubMembershipsMapper.find(cmid);

            if (cm != null) {
                cm.setRole("admin");

                clubMembershipsMapper.update(cm);
                resp.getWriter().write("Club Member updated successfully");
            }
            else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Club Member not found");
            }
        }
        else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid path");
        }
        UnitofWork.getCurrent().commit();
    }

    private void handleCancel(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo(); // "club/1/normal_member"
        String[] parts = pathInfo.split("/");

        UnitofWork.newCurrent();
        if (parts.length >= 2) {
            int cmid = Integer.parseInt(parts[1]); // parts[1] is "1"
            ClubMemberships cm = (ClubMemberships) clubMembershipsMapper.find(cmid);

            if (cm != null) {
                cm.setRole("normal_member");

                clubMembershipsMapper.update(cm);
                resp.getWriter().write("Club Member updated successfully");
            }
            else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Club Member not found");
            }
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

package org.sdamc.Utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.sdamc.DTO.Result;

import java.io.IOException;

@Slf4j
public class JwtUtil {

    public static void logWithOneTraceBack(String message) {
        // 获取当前调用栈
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        // 打印当前方法所在的类和方法
        log.info("Current method: {}.{}", stackTrace[1].getClassName(), stackTrace[1].getMethodName());

        // 获取调用者的信息（上一层的方法）
        if (stackTrace.length > 2) {
            String callerClass = stackTrace[2].getClassName();
            String callerMethod = stackTrace[2].getMethodName();
            System.out.println("Called by: " + callerClass + "." + callerMethod);
        }

        // 输出传递的日志信息
        log.info(message);
    }

    public static boolean VerifyToken(HttpServletRequest req, HttpServletResponse resp, int userId) throws IOException {
        // 在这里检查 token 和 userId 的匹配关系，确保身份验证
        String token = req.getHeader("Authorization");
        System.out.println("Token: " + token);
        if (token == null || !token.startsWith("Bearer ")) {
            new ObjectMapper().writeValue(resp.getOutputStream(), Result.error("Invalid token " + token));
            return false;
        }
        Boolean notMatch = true;
        // 去掉 "Bearer " 前缀
        token = token.substring(7);

        try {
            // 使用相同的算法和密钥进行验证
            Algorithm algorithm = Algorithm.HMAC256(Constants.JWT_SECRET);
            JWTVerifier verifier = JWT.require(algorithm)
                .withClaim("id", userId) // 验证 token 中的 id 和传入的 userId 一致
                .build();
            // 验证和解码 token
            DecodedJWT jwt = verifier.verify(token);
            String userIdFromToken = jwt.getClaim("id").asString();
            logWithOneTraceBack("Verified User ID: " + userIdFromToken);
            // 验证通过
            return true;
        }
        catch (JWTVerificationException exception) {
            // 验证失败
            new ObjectMapper().writeValue(resp.getOutputStream(),
                    Result.error("Mismatch in " + token + " and " + userId));
            return false;
        }
    }

}

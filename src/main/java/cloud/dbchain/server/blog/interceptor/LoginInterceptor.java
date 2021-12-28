package cloud.dbchain.server.blog.interceptor;

import cloud.dbchain.server.blog.BaseResponse;
import cloud.dbchain.server.blog.bean.UserInfo;
import cloud.dbchain.server.blog.contast.CodeKt;
import com.gcigb.dbchain.util.JsonKtxKt;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        if (session.isNew()) {
            writeNotLogin(response);
            return false;
        }
        UserInfo userInfo = (UserInfo) session.getAttribute(session.getId());
        if (userInfo == null || userInfo.getPrivateKey().length <= 0) {
            writeNotLogin(response);
            return false;
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }

    private void writeNotLogin(HttpServletResponse response) throws IOException {
        PrintWriter writer = response.getWriter();
        BaseResponse baseResponse = new BaseResponse(CodeKt.CODE_NOT_LOGIN, "Not Login", null);
        writer.write(JsonKtxKt.toJsonString(baseResponse));
        writer.flush();
    }
}

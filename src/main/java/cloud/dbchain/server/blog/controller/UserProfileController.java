package cloud.dbchain.server.blog.controller;

import cloud.dbchain.server.blog.BaseResponse;
import cloud.dbchain.server.blog.bean.UserInfo;
import cloud.dbchain.server.blog.contast.CodeKt;
import cloud.dbchain.server.blog.service.UserTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

import static cloud.dbchain.server.blog.contast.CodeKt.CODE_FAILURE;

@RestController
@RequestMapping("/userProfile")
public class UserProfileController {

    private final UserTableService userTableService;

    public UserProfileController(@Autowired UserTableService userTableService) {
        this.userTableService = userTableService;
    }

    /**
     * 修改用户信息
     * @param session session
     * @param map name,age,sex,status,photo,motto
     * @return 修改用户信息结果
     */
    @PostMapping("/modify")
    public BaseResponse modify(HttpSession session, @RequestBody Map<String, String> map) {
        // 判断是否登录状态
        if (session.isNew()) {
            return new BaseResponse(CODE_FAILURE, "请先登录", null);
        }
        UserInfo userInfo = (UserInfo) session.getAttribute(session.getId());
        if (userInfo == null || userInfo.getPrivateKey().length <= 0) {
            return new BaseResponse(CODE_FAILURE, "请先登录", null);
        }
        byte[] privateKey = userInfo.getPrivateKey();
        byte[] publicKey = userInfo.getPublicKey33();
        String address = userInfo.getAddress();
        // dbchain_key 不允许随意修改
        map.put("dbchain_key", address);

        // 修改用户信息
        boolean result = userTableService.updateUser(privateKey, publicKey, address, map);
        if (result) {
            return new BaseResponse(CodeKt.CODE_SUCCESS, "修改成功", null);
        } else {
            return new BaseResponse(CodeKt.CODE_FAILURE, "修改失败", null);
        }
    }
}

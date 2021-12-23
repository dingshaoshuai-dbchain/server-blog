package cloud.dbchain.server.blog.controller;

import cloud.dbchain.server.blog.BaseResponse;
import cloud.dbchain.server.blog.bean.UserInfo;
import cloud.dbchain.server.blog.service.KeyEscrowService;
import cloud.dbchain.server.blog.service.UserPasswordService;
import cloud.dbchain.server.blog.service.UserTableService;
import com.gcigb.dbchain.DBChainKt;
import com.gcigb.dbchain.LoopKt;
import com.gcigb.dbchain.TokenKt;
import com.gcigb.dbchain.util.coding.HashUtilKt;
import com.mysql.cj.util.StringUtils;
import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static cloud.dbchain.server.blog.contast.CodeKt.CODE_FAILURE;
import static cloud.dbchain.server.blog.contast.CodeKt.CODE_SUCCESS;

@RestController
@RequestMapping("/user")
public class UserController {

    private final KeyEscrowService keyEscrowService;
    private final UserPasswordService userService;
    private final UserTableService userTableService;

    public UserController(
            @Autowired KeyEscrowService keyEscrowService,
            @Autowired UserPasswordService userPasswordService,
            @Autowired UserTableService userTableService
    ) {
        this.keyEscrowService = keyEscrowService;
        this.userService = userPasswordService;
        this.userTableService = userTableService;
    }

    /**
     * @param map userName,password
     * @return
     */
    @PostMapping("/register")
    public BaseResponse register(@RequestBody Map<String, String> map) {
        String userName = map.get("userName");
        String password = map.get("password");
        if (userName.isEmpty() || password.isEmpty()) {
            return new BaseResponse(CODE_FAILURE, "用户名或密码不可为空", null);
        }
        // 判断用户名是否已经存在
        boolean existAccount = userService.isExistAccount(userName);
        if (existAccount) {
            return new BaseResponse(CODE_FAILURE, "用户名已存在", null);
        }
        // 生成密钥对
        boolean savePrivateKeyResult = keyEscrowService.createAndSavePrivateKeyWithPassword(userName, password);
        if (!savePrivateKeyResult) {
            return new BaseResponse(CODE_FAILURE, "保存密钥失败", null);
        }
        byte[] privateKey = keyEscrowService.loadPrivateKeyByPassword(userName, password);
        byte[] publicKey = DBChainKt.dbChainEncrypt.generatePublicKey33ByPrivateKey(privateKey, null);
        String address = DBChainKt.dbChainEncrypt.generateAddressByPublicKeyByteArray33(publicKey);
        // 获取积分
        Boolean requestResult = LoopKt.loopHandleInCount(() -> TokenKt.requestAppUser(privateKey, publicKey).isSuccess(), aBoolean -> aBoolean, 3);
        if (Boolean.FALSE.equals(requestResult)) {
            return new BaseResponse(CODE_FAILURE, "获取积分失败", null);
        }
        // 查询积分
        Integer token = LoopKt.loopHandleInTime(() -> TokenKt.getToken(address), integer1 -> integer1 > 0, 10);
        if (token == null || token <= 0) {
            if (Boolean.FALSE.equals(requestResult)) {
                return new BaseResponse(CODE_FAILURE, "获取积分失败", null);
            }
        }
        // 往用户表插入数据
        Map<String, String> user = new HashMap<>();
        user.put("name", "用户" + userName);
        user.put("age", "0");
        user.put("dbchain_key", address);
        user.put("sex", "男");
        user.put("status", "");
        user.put("photo", "");
        user.put("motto", "");
        boolean addUser = userTableService.addUser(privateKey, publicKey, address, user);
        if (!addUser) {
            return new BaseResponse(CODE_FAILURE, "插入用户表失败", null);
        }
        // 存储用户名和密码（哈希之后）
        userService.add(userName, password);
        return new BaseResponse(CODE_SUCCESS, "注册成功", address);
    }

    /**
     * @param request
     * @param map     userName,password
     * @return
     */
    @PostMapping("/login")
    public BaseResponse login(HttpServletRequest request, @RequestBody Map<String, String> map) {
        String userName = map.get("userName");
        String password = map.get("password");
        if (userName.isEmpty() || password.isEmpty()) {
            return new BaseResponse(CODE_FAILURE, "用户名或密码不可为空", null);
        }
        // 判断用户名是否已经存在
        boolean existAccount = userService.isExistAccount(userName);
        if (!existAccount) {
            return new BaseResponse(CODE_FAILURE, "用户名不存在", null);
        }
        // 判断密码是否和用户匹配
        byte[] passwordHash = userService.get(userName);
        byte[] userPasswordHash = HashUtilKt.hash256(password.getBytes());
        if (!Arrays.equals(userPasswordHash, passwordHash)) {
            return new BaseResponse(CODE_FAILURE, "用户名或密码错误", null);
        }
        // 获取私钥，如果没有私钥，说明密码错误
        byte[] privateKey = keyEscrowService.loadPrivateKeyByPassword(userName, password);
        if (privateKey == null || privateKey.length <= 0) {
            return new BaseResponse(CODE_FAILURE, "用户名或密码错误", null);
        }
        // 保存登录信息
        HttpSession session = request.getSession();
        session.setMaxInactiveInterval(60 * 60 * 24 * 30);
        byte[] publicKey33 = DBChainKt.dbChainEncrypt.generatePublicKey33ByPrivateKey(privateKey, null);
        String address = DBChainKt.dbChainEncrypt.generateAddressByPublicKeyByteArray33(publicKey33);
        UserInfo userInfo = new UserInfo(userName, privateKey, publicKey33, address);
        session.setAttribute(session.getId(), userInfo);
        return new BaseResponse(CODE_SUCCESS, "登录成功", HexUtils.toHexString(privateKey));
    }

    /**
     * @param request
     * @param recoverWord recoverWord
     * @return
     */
    @PostMapping("/saveRecoverWord")
    public BaseResponse saveRecoverWord(HttpServletRequest request, @RequestBody String recoverWord) {
        // 判断是否登录状态
        HttpSession session = request.getSession();
        if (session.isNew()) {
            return new BaseResponse(CODE_FAILURE, "请先登录", null);
        }
        UserInfo userInfo = (UserInfo) session.getAttribute(session.getId());
        if (userInfo == null || userInfo.getPrivateKey().length <= 0) {
            return new BaseResponse(CODE_FAILURE, "请先登录", null);
        }
        // 保存恢复码
        boolean result = keyEscrowService.savePrivateKeyWithRecoverWord(
                userInfo.getUserName(),
                recoverWord,
                userInfo.getPrivateKey()
        );
        if (!result) {
            return new BaseResponse(CODE_FAILURE, "设置恢复码失败", null);
        }
        return new BaseResponse(CODE_SUCCESS, "设置恢复码成功", null);
    }

    /**
     * @param map userName,recoverWord,newPassword
     * @return
     */
    @PostMapping("/resetPasswordFromRecoverWord")
    public BaseResponse resetPasswordFromRecoverWord(@RequestBody Map<String, String> map) {
        String userName = map.get("userName");
        String recoverWord = map.get("recoverWord");
        String newPassword = map.get("newPassword");
        boolean existAccount = userService.isExistAccount(userName);
        if (!existAccount) {
            return new BaseResponse(CODE_FAILURE, "用户名不存在", null);
        }
        if (StringUtils.isNullOrEmpty(recoverWord) || StringUtils.isNullOrEmpty(newPassword)) {
            return new BaseResponse(CODE_FAILURE, "密码或恢复码不可为空", null);
        }
        boolean result = keyEscrowService.resetPasswordFromRecoverWord(userName, recoverWord, newPassword);
        if (!result) {
            return new BaseResponse(CODE_FAILURE, "恢复码错误", null);
        }
        userService.add(userName, newPassword);
        return new BaseResponse(CODE_SUCCESS, "重置密码成功", null);
    }

    /**
     * @param request
     * @param map     oldPassword,newPassword
     * @return
     */
    @PostMapping("/resetPasswordFromOld")
    public BaseResponse resetPasswordFromOld(HttpServletRequest request, @RequestBody Map<String, String> map) {
        // 判断是否登录状态
        HttpSession session = request.getSession();
        if (session.isNew()) {
            return new BaseResponse(CODE_FAILURE, "请先登录", null);
        }
        UserInfo userInfo = (UserInfo) session.getAttribute(session.getId());
        if (userInfo == null || userInfo.getPrivateKey().length <= 0) {
            return new BaseResponse(CODE_FAILURE, "请先登录", null);
        }
        // 参数判空
        String userName = userInfo.getUserName();
        String oldPassword = map.get("oldPassword");
        String newPassword = map.get("newPassword");
        if (StringUtils.isNullOrEmpty(oldPassword) || StringUtils.isNullOrEmpty(newPassword)) {
            return new BaseResponse(CODE_FAILURE, "密码不可为空", null);
        }
        // 判断密码是否和用户匹配
        byte[] passwordHash = userService.get(userName);
        byte[] userPasswordHash = HashUtilKt.hash256(oldPassword.getBytes());
        if (!Arrays.equals(userPasswordHash, passwordHash)) {
            return new BaseResponse(CODE_FAILURE, "密码错误", null);
        }
        // 获取私钥，如果没有私钥，说明密码错误
        byte[] privateKey = keyEscrowService.loadPrivateKeyByPassword(userName, oldPassword);
        if (privateKey == null || privateKey.length <= 0) {
            return new BaseResponse(CODE_FAILURE, "密码错误", null);
        }
        // 重置密码
        boolean result = keyEscrowService.resetPasswordFromOld(userName, oldPassword, newPassword);
        if (!result) {
            return new BaseResponse(CODE_FAILURE, "密码错误", null);
        }
        userService.add(userName, newPassword);
        return new BaseResponse(CODE_SUCCESS, "重置密码成功", null);
    }
}

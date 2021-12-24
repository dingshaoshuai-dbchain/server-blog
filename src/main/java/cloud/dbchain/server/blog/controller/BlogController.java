package cloud.dbchain.server.blog.controller;

import cloud.dbchain.server.blog.AdministratorKt;
import cloud.dbchain.server.blog.BaseResponse;
import cloud.dbchain.server.blog.bean.UserInfo;
import cloud.dbchain.server.blog.contast.Blogs;
import cloud.dbchain.server.blog.contast.Common;
import cloud.dbchain.server.blog.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/blog")
public class BlogController {
    private final BlogService blogService;

    public BlogController(@Autowired BlogService blogService) {
        this.blogService = blogService;
    }

    @GetMapping("/getAll")
    public BaseResponse getAllBlogs() {
        return blogService.getAll(AdministratorKt.privateKey, AdministratorKt.publicKey);
    }

    /**
     * 发布博客
     *
     * @param map title,body,img
     */
    @PostMapping("/publish")
    public BaseResponse publishBlog(HttpSession session, @RequestBody Map<String, String> map) {
        UserInfo userInfo = (UserInfo) session.getAttribute(session.getId());
        byte[] privateKey = userInfo.getPrivateKey();
        byte[] publicKey = userInfo.getPublicKey33();
        String address = userInfo.getAddress();
        return blogService.publish(privateKey, publicKey, address, map);
    }

    /**
     * 评论博客
     *
     * @param map blog_id,discuss_id,text
     * @return 评论结果
     */
    @PostMapping("/discuss")
    public BaseResponse discuss(HttpSession session, @RequestBody Map<String, String> map) {
        UserInfo userInfo = (UserInfo) session.getAttribute(session.getId());
        byte[] privateKey = userInfo.getPrivateKey();
        byte[] publicKey = userInfo.getPublicKey33();
        String address = userInfo.getAddress();
        return blogService.discuss(privateKey, publicKey, address, map);
    }

    /**
     * 根据条件获取博客
     *
     * @param map title,created_by
     * @return 博客列表
     */
    @PostMapping("/getBlogs")
    public BaseResponse getBlogs(@RequestBody Map<String, String> map) {
        String title = map.get(Blogs.title);
        String createdBy = map.get(Common.created_by);
        return blogService.getBlogs(AdministratorKt.privateKey, AdministratorKt.publicKey, title, createdBy);
    }

    /**
     * 根据 id 查询博客
     *
     * @param id 博客 id
     * @return 博客
     */
    @GetMapping("/getBlog/{id}")
    public BaseResponse getBlog(@PathVariable("id") String id) {
        return blogService.getBlog(AdministratorKt.privateKey, AdministratorKt.publicKey, id);
    }

    /**
     * 查询博客详情
     *
     * @param blogId 博客 id
     * @return 博客
     */
    @GetMapping("/getBlogDetail/{blog_id}")
    public BaseResponse getBlogDetail(@PathVariable("blog_id") String blogId) {
        return blogService.getBlogDetail(AdministratorKt.privateKey, AdministratorKt.publicKey, blogId);
    }
}

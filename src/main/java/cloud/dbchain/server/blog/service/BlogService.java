package cloud.dbchain.server.blog.service;

import cloud.dbchain.server.blog.BaseDBChainResult;
import cloud.dbchain.server.blog.BaseResponse;
import cloud.dbchain.server.blog.bean.DiscussBundle;
import cloud.dbchain.server.blog.bean.response.BlogDetail;
import cloud.dbchain.server.blog.bean.response.DiscussResponse;
import cloud.dbchain.server.blog.contast.Blogs;
import cloud.dbchain.server.blog.contast.CodeKt;
import cloud.dbchain.server.blog.contast.Common;
import cloud.dbchain.server.blog.contast.Discuss;
import cloud.dbchain.server.blog.dao.TableDao;
import cloud.dbchain.server.blog.table.BlogTable;
import cloud.dbchain.server.blog.table.DiscussTable;
import cloud.dbchain.server.blog.table.UserTable;
import com.gcigb.dbchain.QueriedArray;
import com.gcigb.dbchain.bean.result.DBChainQueryResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mysql.cj.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BlogService {

    private final UserTableService userTableService;
    private final TableDao tableDao;

    public BlogService(@Autowired UserTableService userTableService, @Autowired TableDao tableDao) {
        this.userTableService = userTableService;
        this.tableDao = tableDao;
    }

    public BaseResponse getAll(byte[] privateKey, byte[] publicKey) {
        QueriedArray queriedArray = new QueriedArray("table", Blogs.tableName);
        DBChainQueryResult result = tableDao.query(privateKey, publicKey, queriedArray);
        if (!result.isSuccess()) {
            return new BaseResponse(CodeKt.CODE_FAILURE, "获取博客失败", null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<BaseDBChainResult<BlogTable>>() {
        }.getType();
        BaseDBChainResult<BlogTable> o = gson.fromJson(result.getContent(), type);
        return new BaseResponse(CodeKt.CODE_SUCCESS, "成功", o.getResult());
    }

    public BaseResponse getBlogs(byte[] privateKey, byte[] publicKey, String title, String createdBy) {
        QueriedArray queriedArray = new QueriedArray("table", Blogs.tableName);
        if (title != null && title.length() >= 1) {
            queriedArray.findEqual(Blogs.title, title);
        }
        if (createdBy != null && createdBy.length() >= 1) {
            queriedArray.findEqual(Common.created_by, createdBy);
        }
        DBChainQueryResult result = tableDao.query(privateKey, publicKey, queriedArray);
        if (!result.isSuccess()) {
            return new BaseResponse(CodeKt.CODE_FAILURE, "获取博客失败", null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<BaseDBChainResult<BlogTable>>() {
        }.getType();
        BaseDBChainResult<BlogTable> o = gson.fromJson(result.getContent(), type);
        return new BaseResponse(CodeKt.CODE_SUCCESS, "成功", o.getResult());
    }

    public BaseResponse getBlog(byte[] privateKey, byte[] publicKey, String id) {
        QueriedArray queriedArray = new QueriedArray("table", Blogs.tableName)
                .findById(id);
        DBChainQueryResult result = tableDao.query(privateKey, publicKey, queriedArray);
        if (!result.isSuccess()) {
            return new BaseResponse(CodeKt.CODE_FAILURE, "获取博客失败", null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<BaseDBChainResult<BlogTable>>() {
        }.getType();
        BaseDBChainResult<BlogTable> o = gson.fromJson(result.getContent(), type);
        return new BaseResponse(CodeKt.CODE_SUCCESS, "成功", o.getResult());
    }

    public BaseResponse publish(byte[] privateKey, byte[] publicKey, String address, Map<String, String> map) {
        boolean insertRow = tableDao.inertRow(privateKey, publicKey, address, Blogs.tableName, map);
        if (insertRow) {
            return new BaseResponse(CodeKt.CODE_SUCCESS, "发布成功", null);
        } else {
            return new BaseResponse(CodeKt.CODE_FAILURE, "发布失败", null);
        }
    }

    public BaseResponse discuss(byte[] privateKey, byte[] publicKey, String address, Map<String, String> map) {
        boolean insertRow = tableDao.inertRow(privateKey, publicKey, address, Discuss.tableName, map);
        if (insertRow) {
            return new BaseResponse(CodeKt.CODE_SUCCESS, "评论成功", null);
        } else {
            return new BaseResponse(CodeKt.CODE_FAILURE, "评论失败", null);
        }
    }

    public BlogDetail getBlogDetail(byte[] privateKey, byte[] publicKey, String blogId) {
        Gson gson = new Gson();
        // 获取博客内容
        QueriedArray queriedArray = new QueriedArray("table", Blogs.tableName)
                .findById(blogId);
        String content = tableDao.query(privateKey, publicKey, queriedArray).getContent();
        Type type = new TypeToken<BaseDBChainResult<BlogTable>>() {
        }.getType();
        BaseDBChainResult<BlogTable> blogTableResult = gson.fromJson(content, type);
        List<BlogTable> result = blogTableResult.getResult();
        if (result.isEmpty()) {
            return null;
        }
        BlogTable blogTable = result.get(0);

        // 查出关于这条博客的所有评论及回复
        queriedArray = new QueriedArray("table", Discuss.tableName)
                .findEqual(Discuss.blog_id, blogId);
        content = tableDao.query(privateKey, publicKey, queriedArray).getContent();
        type = new TypeToken<BaseDBChainResult<DiscussTable>>() {
        }.getType();
        BaseDBChainResult<DiscussTable> discussTableBaseResult = gson.fromJson(content, type);
        List<DiscussTable> discussList = discussTableBaseResult.getResult();
        // 以每一条评论或者回复的 id 为 key，方便后期获取
        Map<String, DiscussResponse> discussMap = new HashMap<>();
        List<DiscussResponse> responseList = new ArrayList<>();
        for (DiscussTable discussTable : discussList) {
            String id = discussTable.getId();
            String blog_id = discussTable.getBlog_id();
            String discuss_id = discussTable.getDiscuss_id();
            String text = discussTable.getText();
            UserTable user = userTableService.getUser(privateKey, publicKey, discussTable.getCreated_by());
            if (user != null) {
                String authorName = user.getName();
                String authorPhoto = user.getPhoto();
                String address = user.getDbchain_key();
                DiscussResponse discussResponse = new DiscussResponse(id, blog_id, discuss_id, text, authorName, authorPhoto, address, null, null);
                discussMap.put(discussResponse.getId(), discussResponse);
                responseList.add(discussResponse);
            }
        }

        List<DiscussBundle> commentList = new ArrayList<>();
        Map<String, List<DiscussBundle>> repliedMap = new HashMap<>();
        for (DiscussResponse response : responseList) {
            if (StringUtils.isNullOrEmpty(response.getDiscuss_id())) {
                DiscussBundle discussBundle = new DiscussBundle(response, null);
                commentList.add(discussBundle);
            } else {
                DiscussResponse discussResponse = discussMap.get(response.getDiscuss_id());
                UserTable user = userTableService.getUser(privateKey, publicKey, discussResponse.getAuthorAddress());
                response.setRepliedName(user.getName());
                response.setRepliedPhoto(user.getPhoto());
                DiscussBundle discussBundle = new DiscussBundle(response, null);
                List<DiscussBundle> list = repliedMap.get(response.getDiscuss_id());
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.add(discussBundle);
                repliedMap.put(response.getDiscuss_id(), list);
            }
        }
        for (DiscussBundle discussBundle : commentList) {
            getByRecursion(repliedMap, discussBundle);
        }
        return new BlogDetail(blogTable.getTitle(), blogTable.getBody(), commentList);
    }

    private void getByRecursion(Map<String, List<DiscussBundle>> map, DiscussBundle discussBundle) {
        List<DiscussBundle> discussBundles = map.get(discussBundle.getDiscuss().getId());
        if (discussBundles == null) return;
        for (DiscussBundle bundle : discussBundles) {
            List<DiscussBundle> repliedList = discussBundle.getRepliedList();
            if (repliedList == null) {
                discussBundle.setRepliedList(new ArrayList<>());
            }
            discussBundle.getRepliedList().add(bundle);
            getByRecursion(map, bundle);
        }
    }
}

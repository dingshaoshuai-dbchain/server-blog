package cloud.dbchain.server.blog.util;

import cloud.dbchain.server.blog.BaseResponse;
import cloud.dbchain.server.blog.contast.CodeKt;
import com.gcigb.dbchain.bean.result.DBChainQueryResult;

public class ResponseUtil {

    public static BaseResponse generateResponse(DBChainQueryResult result) {
        if (result.isSuccess()) {
            return new BaseResponse(CodeKt.CODE_SUCCESS, "成功", result.getContent());
        } else {
            return new BaseResponse(CodeKt.CODE_FAILURE, "失败", null);
        }
    }
}

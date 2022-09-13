package pro.fessional.wings.slardar.httprest;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import pro.fessional.wings.slardar.controller.TestRestTmplController;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

import java.util.List;

/**
 * @author trydofor
 * @see TestRestTmplController
 * @since 2022-09-11
 */
public interface RetrofitCaller {

    @Data
    class Ins {
        private List<Bad> bad;
    }

    @Data
    class Bad {
        @JSONField(name = "sstr") // 增加注解，使fastjson和jackson兼容
        private String sStr; // bad naming
        private String ssStr;
    }


    @POST("/test/rest-bad-json.htm")
    Call<Ins> jsonIns(@Body Ins ins);


    @GET("/test/rest-template-helper-down.htm")
    Call<ResponseBody> download();

    @Multipart
    @POST("/test/rest-template-helper-file.htm")
    Call<ResponseBody> upload(@Part MultipartBody.Part up);

    // 官方issue，没有提供固定name的动态文件名的强类型方案，
    // MultipartBody.Part 属于动态name，破坏了retrofit2的注解约定
    // https://github.com/square/retrofit/issues/1063
    // https://github.com/square/retrofit/issues/1140
    // https://github.com/square/retrofit/pull/1188
    // RequestFactory 构造header的不推荐的hack技巧
    @Multipart
    @POST("/test/rest-template-helper-file.htm")
    Call<ResponseBody> upload(@Part("up\"; filename=\"test.txt") RequestBody up);
}

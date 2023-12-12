package pro.fessional.wings.slardar.httprest;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import pro.fessional.wings.slardar.app.controller.TestRestTmplController;
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
        @JSONField(name = "sstr") // annotation to make fastjson and jackson compatible
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

    // Official issue, there is no strongly typed scheme for dynamic filenames with fixed names.
    // MultipartBody.Part is a dynamic name, breaking the annotation convention of retrofit2
    // https://github.com/square/retrofit/issues/1063
    // https://github.com/square/retrofit/issues/1140
    // https://github.com/square/retrofit/pull/1188
    // RequestFactory, not recommended to use hack trick for constructing headers
    @Multipart
    @POST("/test/rest-template-helper-file.htm")
    Call<ResponseBody> upload(@Part("up\"; filename=\"test.txt") RequestBody up);
}

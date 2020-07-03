package pro.fessional.wings.example.service.user;

import lombok.Data;
import pro.fessional.mirana.data.R;

import java.time.LocalDate;

/**
 * @author trydofor
 * @since 2020-07-01
 */
public interface UserService {

    @Data
    class UserCreate {
        private String name;
        private Integer gender;
        private LocalDate birth;
        private String avatar;
        private String country;
        private String language;
        private Integer timezone;
    }

    /**
     * 创建用户，并返回id
     *
     * @param user 对象
     * @return id
     */
    R<Long> create(UserCreate user);
}

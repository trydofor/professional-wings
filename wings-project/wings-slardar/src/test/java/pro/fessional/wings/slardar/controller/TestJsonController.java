package pro.fessional.wings.slardar.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.fessional.mirana.data.R;

import java.math.BigDecimal;

/**
 * @author trydofor
 * @since 2021-04-05
 */
@RestController
@Slf4j
public class TestJsonController {

    @Data
    public static class Dec {
        private BigDecimal dec = new BigDecimal("12345.67");
        private String str = "string";
    }

    // 继承并override
    public static class Sub extends Dec {
        @JsonFormat(pattern = "#,###.##", shape = JsonFormat.Shape.STRING)
        @Override
        public BigDecimal getDec() {
            return super.getDec();
        }
    }

    /**
     * DateFmt to DateMmm mapper, auto generate by `wgmp` live template
     */
    @Mapper
    public interface DecToSub {

        DecToSub INSTANCE = Mappers.getMapper(DecToSub.class);

        static Sub into(Dec a) {
            return into(a, new Sub());
        }

        static Sub into(Dec a, Sub b) {
            INSTANCE._into(a, b);
            return b;
        }

        void _into(Dec a, @MappingTarget Sub b);
    }

    // 自己控制，分作不同的view
    @Data
    public static class Vi {
        private BigDecimal dec = new BigDecimal("12345.67");

        //
        public interface Pub {}
        public interface Api {}

        @JsonView(Pub.class)
        public BigDecimal getDec() {
            return dec;
        }

        @JsonView(Api.class)
        @JsonProperty("key")
        @JsonFormat(pattern = "#,###.##", shape = JsonFormat.Shape.STRING)
        public BigDecimal getDecApi() {
            return dec;
        }
    }

    @GetMapping("/test/json-dec.json")
    public R<Dec> jsonDec() {
        // {"success":true,"data":{"dec":"12345.67","str":"string"}}
        return R.okData(new Dec());
    }

    @GetMapping("/test/json-sub.json")
    public R<Dec> jsonSub() {
        // {"success":true,"data":{"dec":"12,345.67","str":"string"}}
        return R.okData(DecToSub.into(new Dec()));
    }

    @GetMapping("/test/json-api.json")
    @JsonView(Vi.Api.class)
    public R<Vi> jsonApi() {
        // {"success":true,"data":{"key":"12,345.67"}}
        return R.okData(new Vi());
    }
}

package pro.fessional.wings.tiny.grow.track;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pro.fessional.mirana.id.Ulid;
import pro.fessional.mirana.time.Sleep;
import pro.fessional.mirana.time.ThreadNow;
import pro.fessional.wings.tiny.app.service.TestTrack1Service;
import pro.fessional.wings.tiny.app.service.TestTrack2Service;
import pro.fessional.wings.tiny.app.service.impl.TestTrackCollectorImpl;
import pro.fessional.wings.tiny.grow.database.autogen.tables.daos.WinGrowTrackDao;
import pro.fessional.wings.tiny.grow.database.autogen.tables.pojos.WinGrowTrack;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author trydofor
 * @since 2024-07-27
 */
@SpringBootTest(properties = {
    "wings.tiny.grow.track.exclude.equal[password]=password",
    "wings.tiny.grow.track.exclude.regex[secret]=(?i).*secret",
})
@Slf4j
@AutoConfigureMockMvc
class TinyTrackServiceTest {

    @Setter(onMethod_ = { @Autowired })
    protected TestTrack1Service testTrack1Service;

    @Setter(onMethod_ = { @Autowired })
    protected TestTrack2Service testTrack2Service;

    @Setter(onMethod_ = { @Autowired })
    protected WinGrowTrackDao winGrowTrackDao;

    @Setter(onMethod_ = { @Autowired })
    private MockMvc mvc;

    private final long now = (ThreadNow.millis() / 100) * 100;

    @Test
    void service() {
        String key11 = Ulid.next();
        String key12 = Ulid.next();
        testTrack1Service.track(now + 11, key11);
        testTrack1Service.trackTx(now + 12, key12);
        waitCodeKey(key11, key12);

        checkPojo(11, key11, false, "pro.fessional.wings.tiny.app.service.TestTrack1Service#track(long,String)", "Local", now + 11, key11);
        checkPojo(12, key12, false, "pro.fessional.wings.tiny.app.service.TestTrack1Service#trackTx(long,String)", "Local", 0, "");

        String key21 = Ulid.next();
        String key22 = Ulid.next();
        testTrack2Service.track(now + 21, key21);
        testTrack2Service.trackTx(now + 22, key22);
        waitCodeKey(key21, key22);

        checkPojo(21, key21, false, "pro.fessional.wings.tiny.app.service.impl.TestTrack2ServiceImpl#track(long,String)", "Local", now + 21, key21);
        checkPojo(22, key22, false, "pro.fessional.wings.tiny.app.service.impl.TestTrack2ServiceImpl#trackTx(long,String)", "Local", 0, "");
    }

    @Test
    void mvc() throws Exception {
        String key31 = Ulid.next();
        final MvcResult mvcResult = mvc.perform(get("/test/track.json")
                                           .contentType(MediaType.APPLICATION_JSON)
                                           .param("id", String.valueOf(now + 31))
                                           .param("str", key31)
                                       )
                                       .andDo(print())
                                       .andExpect(status().isOk())
                                       .andReturn();
        final String body0 = mvcResult.getResponse().getContentAsString();
        waitCodeKey(key31);
        checkPojo(31, key31, true, "pro.fessional.wings.tiny.app.controller.TestTrackController#track(long,String,HttpServletRequest)", "zoneid", 0, "");
    }

    private void waitCodeKey(String... cks) {
        for (String ck : cks) {
            while (TestTrackCollectorImpl.CodeKeys.containsKey(ck)) {
                log.info("wait key={}", ck);
                Sleep.ignoreInterrupt(1_000);
            }
        }
    }

    private void checkPojo(int id, String str, boolean web, String key, String env, long dkey, String ckey) {
        long iid = now + id;
        String ins = (web ? "[%s,\"%s\",{}]" : "[%s,\"%s\"]").formatted(iid, str);
        String out = "{\"id\":%s,\"str\":\"%s\"}".formatted(iid, str);
        WinGrowTrack pojo = winGrowTrackDao.fetchOne(t -> t.TrackIns.eq(ins));
        Assertions.assertNotNull(pojo);
        Assertions.assertEquals(key, pojo.getTrackKey());
        Assertions.assertTrue(pojo.getTrackEnv().contains(env));
        Assertions.assertEquals(dkey, pojo.getDataKey());
        Assertions.assertEquals(ckey, pojo.getCodeKey());
        Assertions.assertEquals(out, pojo.getTrackOut());
    }
}
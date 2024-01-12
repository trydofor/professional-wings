package pro.fessional.wings.faceless.flywave;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import pro.fessional.mirana.math.AnyIntegerUtil;
import pro.fessional.mirana.time.ThreadNow;
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager.RevisionSql;
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager.Status;
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author trydofor
 * @since 2022-03-15
 */
@Slf4j
public class RevisionFitness {

    public static final Long UnInit = -1L;
    private final Set<String> scanPath = new HashSet<>();
    private final TreeMap<Long, Set<Act>> reviAct = new TreeMap<>(); // No Skip
    private final HashMap<Long, Set<String>> reviMsg = new HashMap<>();

    public void addFit(Fit fit, String msg) {
        if (fit == null) return;
        final Act act = fit.getLost();
        if (act == null || act == Act.SKIP) {
            log.info("skip fit {}", msg);
            return;
        }

        final Set<String> path = fit.path;
        if (path != null) {
            this.scanPath.addAll(path);
        }

        final Set<String> revi = fit.getRevi();
        for (String str : revi) {
            Long r = AnyIntegerUtil.obj64(str);
            reviAct.computeIfAbsent(r, ignored -> new HashSet<>()).add(act);
            reviMsg.computeIfAbsent(r, ignored -> new HashSet<>()).add(msg);
        }

        log.info("found fit {}. `wings.faceless.flywave.fit[{}].lost=SKIP` to skip", revi, msg);
    }

    public void addFits(Map<String, Fit> fits) {
        for (Map.Entry<String, Fit> ent : fits.entrySet()) {
            addFit(ent.getValue(), ent.getKey());
        }
    }

    public void checkRevision(SchemaRevisionManager revisionManager, boolean autoInit) {
        revisionManager.askWay(ignored -> true);
        revisionManager.logWay((ignored1, ignored2) -> {});
        final TreeMap<Long, Set<Act>> revi = checkUnapply(revisionManager);
        applyRevision(revisionManager, revi, autoInit);
    }

    private void applyRevision(SchemaRevisionManager manager, TreeMap<Long, Set<Act>> revi, boolean autoInit) {
        TreeMap<Long, Set<String>> exec = new TreeMap<>();
        boolean failed = false;
        if (revi.containsKey(UnInit)) {
            for (Set<Act> at : revi.values()) {
                if (!autoInit && at.contains(Act.EXEC)) {
                    throw new IllegalStateException("""

                            Wings `flywave revision` do NOT exist, and Auto Init is dangerous, you can,
                            1.stop checker: `wings.faceless.flywave.checker=false`
                            2.revision fitness do NOT contain `EXEC`
                            3.init `flywave revision` manually
                            4.auto-init: `wings.faceless.flywave.auto-init=true` At Your Own Risk
                            """);
                }
            }
            revi.remove(UnInit);
        }

        for (Map.Entry<Long, Set<Act>> en : revi.entrySet()) {
            final Long rv = en.getKey();
            final Set<Act> ts = en.getValue();
            final Set<String> ms = reviMsg.get(rv);
            if (ts.contains(Act.WARN)) {
                log.warn("Wings Revision Lost revi={}. Manual={}", rv, manual(ms));
            }
            if (ts.contains(Act.FAIL)) {
                log.error("Wings Revision Lost revi={}. Manual={}", rv, manual(ms));
                failed = true;
            }
            if (ts.contains(Act.EXEC)) {
                exec.put(rv, ms);
            }
        }

        if (failed) {
            throw new IllegalStateException("Wings Revision Lost revi need FAIL");
        }
        if (exec.isEmpty()) {
            return;
        }

        final SortedMap<Long, RevisionSql> scan = FlywaveRevisionScanner.scan(scanPath);

        // check first
        boolean errors = false;
        for (Map.Entry<Long, Set<String>> en : exec.entrySet()) {
            Long rv = en.getKey();
            final Set<String> ms = en.getValue();
            final RevisionSql sql = scan.get(rv);
            if (sql == null) {
                log.error("Wings Revision Lost And Failed to Scan. revi={} by={}", rv, ms);
                errors = true;
            }
        }

        if (errors) {
            throw new IllegalStateException("Wings Revision Lost And Failed");
        }

        // exec sql
        final long cid = -ThreadNow.millis();
        for (Map.Entry<Long, Set<String>> en : exec.entrySet()) {
            Long rv = en.getKey();
            final Set<String> ms = en.getValue();
            final RevisionSql sql = scan.get(rv);
            if (rv == WingsRevision.V00_19_0512_01_Schema.revision()) {
                TreeMap<Long, RevisionSql> init = new TreeMap<>();
                init.put(rv, sql);
                log.info("Wings Revision force to init revi={}, cid={}, by={}", rv, cid, ms);
                manager.checkAndInitSql(init, cid, true);
            }
            else {
                manager.forceUpdateSql(sql, cid);
                log.info("Wings Revision force to apply revi={}, cid={}, by={}", rv, cid, ms);
                manager.forceApplyBreak(rv, cid, true, null);
            }
        }
    }

    private String manual(Set<String> ms) {
        StringBuilder sb = new StringBuilder();
        sb.append("replace XXX with SKIP to skip, EXEC to exec sqls: ");
        for (String m : ms) {
            sb.append("\nwings.faceless.flywave.fit.").append(m).append(".lost=XXX");
        }
        return sb.toString();
    }

    private TreeMap<Long, Set<Act>> checkUnapply(SchemaRevisionManager manager) {
        HashMap<Long, Status> reviStatus = null;
        HashMap<Long, Map<String, Status>> reviDbDiff = new HashMap<>();
        String headDb = "";
        boolean unInit = false;
        for (Map.Entry<String, SortedMap<Long, Status>> en : manager.statusRevisions().entrySet()) {
            final String nextDb = en.getKey();
            log.debug("Wings Revision Check Database={}", nextDb);
            Map<Long, Status> sts = en.getValue();
            if (sts == null) {
                unInit = true;
                sts = Map.of(UnInit, Status.Future);
            }

            if (reviStatus == null) {
                reviStatus = new HashMap<>(sts);
                headDb = nextDb;
            }
            else {
                Map<Long, Status> headStatus = new HashMap<>(reviStatus);
                Map<Long, Status> nextStatus = new HashMap<>();
                for (Map.Entry<Long, Status> st : sts.entrySet()) {
                    final Long rv = st.getKey();
                    final Status rs = st.getValue();
                    if (rs == headStatus.get(rv)) {
                        headStatus.remove(rv);
                    }
                    else {
                        nextStatus.put(rv, rs);
                    }
                }
                for (Map.Entry<Long, Status> e : headStatus.entrySet()) {
                    final Map<String, Status> diff = reviDbDiff.computeIfAbsent(e.getKey(), ignored -> new LinkedHashMap<>());
                    diff.put(headDb, e.getValue());
                }
                for (Map.Entry<Long, Status> e : nextStatus.entrySet()) {
                    final Map<String, Status> diff = reviDbDiff.computeIfAbsent(e.getKey(), ignored -> new LinkedHashMap<>());
                    diff.put(nextDb, e.getValue());
                }
            }
        }

        if (reviStatus == null || reviStatus.isEmpty()) {
            if (unInit) {
                log.warn("Wings Revision UnInit all-revi");
                final TreeMap<Long, Set<Act>> map = new TreeMap<>(reviAct);
                map.put(UnInit, Set.of(Act.WARN));
                return map;
            }
            else {
                log.info("Wings Revision Unapply all-revi");
                return reviAct;
            }
        }

        final StringBuilder diffWarn = new StringBuilder();
        boolean needFail = false;
        for (Map.Entry<Long, Map<String, Status>> en : reviDbDiff.entrySet()) {
            final Long rv = en.getKey();
            final Set<Act> acts = reviAct.get(rv);
            diffWarn.append("\nWARN Diff-Revi=").append(rv);
            for (Map.Entry<String, Status> mp : en.getValue().entrySet()) {
                diffWarn.append(", ").append(mp.getKey()).append('=').append(mp.getValue());
            }
            if (acts != null && !needFail && acts.contains(Act.FAIL)) {
                needFail = true;
            }
        }

        if (!diffWarn.isEmpty()) {
            if (needFail) {
                throw new IllegalStateException("Wings Revision Diff Schemas Found:" + diffWarn);
            }
            else {
                log.warn("Wings Revision Diff Schemas Found:" + diffWarn);
            }
        }

        final TreeMap<Long, Set<Act>> map = new TreeMap<>();
        for (Map.Entry<Long, Set<Act>> revi : reviAct.entrySet()) {
            final Long rv = revi.getKey();
            final Status st = reviStatus.get(rv);
            if (st != Status.Applied) {
                map.put(rv, revi.getValue());
                log.info("Wings Revision Unapply revi={}, status={}", rv, st);
            }
        }
        if (unInit) {
            log.warn("Wings Revision UnInit all-revi");
            map.put(UnInit, Set.of(Act.WARN));
            return map;
        }
        return map;
    }

    // ////////

    public enum Act {
        /**
         * skip checking
         */
        SKIP,
        /**
         * only warn in log
         */
        WARN,
        /**
         * stop with exception
         */
        FAIL,
        /**
         * force to exec. forceUpdateSql and forceApplyBreak
         */
        EXEC,
    }

    @Data
    public static class Fit {
        /**
         * sql scan pattern, comma separated. PathMatchingResourcePatternResolver format
         */
        private Set<String> path = Collections.emptySet();
        /**
         * revision, comma separated
         */
        private Set<String> revi = Collections.emptySet();

        /**
         * Post check, if the specified revi is not applied,
         * only upgrade can be performed, not downgrade to avoid dangerous delete.
         */
        private Act lost = Act.WARN;
    }
}

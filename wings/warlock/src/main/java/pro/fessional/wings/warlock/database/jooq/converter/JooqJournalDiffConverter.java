package pro.fessional.wings.warlock.database.jooq.converter;

import org.jooq.impl.AbstractConverter;
import pro.fessional.wings.faceless.service.journal.JournalDiff;
import pro.fessional.wings.slardar.fastjson.FastJsonHelper;

/**
 * @author trydofor
 * @since 2022-10-25
 */
public class JooqJournalDiffConverter extends AbstractConverter<String, JournalDiff> {

    public JooqJournalDiffConverter() {
        super(String.class, JournalDiff.class);
    }

    @Override
    public JournalDiff from(String str) {
        return FastJsonHelper.object(str, JournalDiff.class);
    }

    @Override
    public String to(JournalDiff obj) {
        return FastJsonHelper.string(obj);
    }
}

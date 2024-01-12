/*
 * This file is generated by jOOQ.
 */
package pro.fessional.wings.tiny.mail.database.autogen;


import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;
import pro.fessional.wings.tiny.mail.database.autogen.tables.WinMailSenderTable;

import javax.annotation.processing.Generated;
import java.util.Arrays;
import java.util.List;


/**
 * The schema <code>wings</code>.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.18.7",
        "schema version:2020102701"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class DefaultSchemaTinyMail extends SchemaImpl {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>DEFAULT_SCHEMA</code>
     */
    public static final DefaultSchemaTinyMail DEFAULT_SCHEMA = new DefaultSchemaTinyMail();

    /**
     * The table <code>win_mail_sender</code>.
     */
    public final WinMailSenderTable WinMailSender = WinMailSenderTable.WinMailSender;

    /**
     * No further instances allowed
     */
    private DefaultSchemaTinyMail() {
        super("", null);
    }


    @Override
    public Catalog getCatalog() {
        return DefaultCatalogTinyMail.DEFAULT_CATALOG;
    }

    @Override
    public final List<Table<?>> getTables() {
        return Arrays.asList(
            WinMailSenderTable.WinMailSender
        );
    }
}

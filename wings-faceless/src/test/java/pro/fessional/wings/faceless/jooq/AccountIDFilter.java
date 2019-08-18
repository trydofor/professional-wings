package pro.fessional.wings.faceless.jooq;

import static java.util.Arrays.asList;
import static org.jooq.Clause.DELETE;
import static org.jooq.Clause.DELETE_DELETE;
import static org.jooq.Clause.DELETE_WHERE;
import static org.jooq.Clause.INSERT;
import static org.jooq.Clause.SELECT;
import static org.jooq.Clause.SELECT_FROM;
import static org.jooq.Clause.SELECT_WHERE;
import static org.jooq.Clause.TABLE_ALIAS;
import static org.jooq.Clause.UPDATE;
import static org.jooq.Clause.UPDATE_UPDATE;
import static org.jooq.Clause.UPDATE_WHERE;


import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import org.jooq.Clause;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Operator;
import org.jooq.QueryPart;
import org.jooq.Table;
import org.jooq.VisitContext;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultVisitListener;

@SuppressWarnings("unchecked")
public class AccountIDFilter extends DefaultVisitListener {

    final Integer[] ids;

    public AccountIDFilter(Integer... ids) {
        this.ids = ids;
    }

    void push(VisitContext context) {
        conditionStack(context).push(new ArrayList<>());
        whereStack(context).push(false);
    }

    void pop(VisitContext context) {
        whereStack(context).pop();
        conditionStack(context).pop();
    }

    Deque<List<Condition>> conditionStack(VisitContext context) {
        Deque<List<Condition>> data = (Deque<List<Condition>>) context.data("conditions");

        if (data == null) {
            data = new ArrayDeque<>();
            context.data("conditions", data);
        }

        return data;
    }

    Deque<Boolean> whereStack(VisitContext context) {
        Deque<Boolean> data = (Deque<Boolean>) context.data("predicates");

        if (data == null) {
            data = new ArrayDeque<>();
            context.data("predicates", data);
        }

        return data;
    }

    List<Condition> conditions(VisitContext context) {
        return conditionStack(context).peek();
    }

    boolean where(VisitContext context) {
        return whereStack(context).peek();
    }

    void where(VisitContext context, boolean value) {
        whereStack(context).pop();
        whereStack(context).push(value);
    }

    <E> void pushConditions(VisitContext context, Table<?> table, Field<E> field, E... values) {

        // Check if we're visiting the given table
        if (context.queryPart() == table) {
            List<Clause> clauses = clauses(context);

            // ... and if we're in the context of the current subselect's
            // FROM clause
            if (clauses.contains(SELECT_FROM) ||
                    clauses.contains(UPDATE_UPDATE) ||
                    clauses.contains(DELETE_DELETE)) {

                // If we're declaring a TABLE_ALIAS... (e.g. "T_BOOK" as "b")
                if (clauses.contains(TABLE_ALIAS)) {
                    QueryPart[] parts = context.queryParts();

                    // ... move up the QueryPart visit path to find the
                    // defining aliased table, and extract the aliased
                    // field from it. (i.e. the "b" reference)
                    for (int i = parts.length - 2; i >= 0; i--) {
                        if (parts[i] instanceof Table) {
                            field = ((Table<?>) parts[i]).field(field);
                            break;
                        }
                    }
                }

                // Push a condition for the field of the (potentially aliased) table
                conditions(context).add(field.in(values));
            }
        }
    }

    /**
     * Retrieve all clauses for the current subselect level, starting with
     * the last {@link Clause#SELECT}.
     */
    List<Clause> clauses(VisitContext context) {
        List<Clause> result = asList(context.clauses());
        int index = result.lastIndexOf(SELECT);

        if (index > 0)
            return result.subList(index, result.size() - 1);
        else
            return result;
    }

    @Override
    public void clauseStart(VisitContext context) {

        // Enter a new SELECT clause / nested select, or DML statement
        if (context.clause() == SELECT ||
                context.clause() == UPDATE ||
                context.clause() == DELETE ||
                context.clause() == INSERT) {
            push(context);
        }
    }

    @Override
    public void clauseEnd(VisitContext context) {

        // Append all collected predicates to the WHERE clause if any
        if (context.clause() == SELECT_WHERE ||
                context.clause() == UPDATE_WHERE ||
                context.clause() == DELETE_WHERE) {
            List<Condition> conditions = conditions(context);

            if (conditions.size() > 0) {
                context.context()
                       .formatSeparator()
                       .keyword(where(context) ? "and" : "where")
                       .sql(' ');

                context.context().visit(DSL.condition(Operator.AND, conditions));
            }
        }

        // Leave a SELECT clause / nested select, or DML statement
        if (context.clause() == SELECT ||
                context.clause() == UPDATE ||
                context.clause() == DELETE ||
                context.clause() == INSERT) {
            pop(context);
        }
    }

    @Override
    public void visitEnd(VisitContext context) {
//        pushConditions(context, ACCOUNTS, ACCOUNTS.ID, ids);
//        pushConditions(context, TRANSACTIONS, TRANSACTIONS.ACCOUNT_ID, ids);

        // Check if we're rendering any condition within the WHERE clause
        // In this case, we can be sure that jOOQ will render a WHERE keyword
        if (context.queryPart() instanceof Condition) {
            List<Clause> clauses = clauses(context);

            if (clauses.contains(SELECT_WHERE) ||
                    clauses.contains(UPDATE_WHERE) ||
                    clauses.contains(DELETE_WHERE)) {
                where(context, true);
            }
        }
    }
}
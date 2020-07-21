package com.alibaba.druid.util;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import java.util.Optional;

/**
 * @ClassNameMysqlWhereUpdater
 * @Description
 * @Author sanmengcc
 * @Date2020/7/21 16:31
 * @Version V1.0
 **/
public class MysqlWhereUpdater {

    private static final String MYSQL_STRING = "mysql";

    /**
     * @Description 原始sql增加条件
     * 1.目前只测试了联表查询、子查询
     * 2.测试不是很完善、可以自行修改
     * 3.仅仅是满足目前的需求
     * 4.针对where没有做去重
     * 5.简单的实现了几个util
     * 6.修改了SQLBinaryOpExpr源码增加了field字段
     * @Date   2020/7/21 15:28
     */
    public static String addWhereSql(String sql, SQLBinaryOpExpr expr) {
        SQLStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement statement = parser.parseStatement();
        SQLSelectStatement sqlSelectStatement = (SQLSelectStatement) statement;
        MySqlSelectQueryBlock queryBlock = (MySqlSelectQueryBlock) ((SQLSelectStatement) statement).getSelect().getQueryBlock();
        SQLTableSource from = queryBlock.getFrom();
        if (from instanceof SQLSubqueryTableSource) {
            addWhere(((SQLSubqueryTableSource) from).getSelect(),expr);
            return sqlSelectStatement.toString();
        }
        if (from instanceof SQLJoinTableSource) {
            addWhereStatement(sqlSelectStatement,expr);
            return sqlSelectStatement.toString();
        }
        return sql;
    }

    /**
     * @Description 添加where条件
     * @Date   2020/7/21 14:01
     */
    public static void addWhereStatement(SQLSelectStatement statement, SQLBinaryOpExpr expr) {
        SQLExpr where = statement.getSelect().getQueryBlock().getWhere();
        if (where instanceof SQLInSubQueryExpr) {
            //处理where中的子查询操作
        }
        addWhere(statement.getSelect(), expr);
    }

    /**
     * @Description 添加where条件
     * @Date   2020/7/21 14:01
     */
    public static void addWhere(SQLSelect select, SQLBinaryOpExpr expr) {
        Optional.ofNullable(select).ifPresent(s -> s.addWhere(expr));
    }

    /**
     * @Description 添加where条件
     * @Date   2020/7/21 14:01
     */
    public static void addWhere(String sql, SQLBinaryOpExpr expr) {
        Optional.ofNullable(toSQLSelect(sql)).ifPresent(s -> s.addWhere(expr));
    }

    /**
     * @Description SQL TO SQLSelect
     * @Date   2020/7/21 14:08
     */
    private static SQLSelect toSQLSelect(String sql) {
        return Optional.ofNullable(sql)
                .map(s -> SQLParserUtils.createSQLStatementParser(sql, MYSQL_STRING))
                .map(SQLStatementParser::parseStatementList)
                .map(array -> ((SQLSelectStatement) array.get(0)).getSelect())
                .orElseThrow(() -> new RuntimeException("Cover SQLSelect Fail."));
    }

    /**
     * @Description 填充where条件
     * @Date   2020/7/21 14:23
     */
    public static SQLBinaryOpExpr fullWhereField(String alias, String field, SQLValuableExpr fieldValue, SQLBinaryOperator operator) {
        SQLBinaryOpExpr whereExpr = new SQLBinaryOpExpr(MYSQL_STRING);
        whereExpr.setOperator(operator);
        whereExpr.setRight(fieldValue);
        whereExpr.setField(field);
        if (StringUtils.isEmpty(alias)) {
            whereExpr.setLeft(new SQLIdentifierExpr(field));
        } else {
            whereExpr.setLeft(new SQLPropertyExpr(alias, field));
        }
        return whereExpr;
    }
}

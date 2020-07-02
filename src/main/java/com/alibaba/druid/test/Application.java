package com.alibaba.druid.test;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUpdateStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.parser.SQLStatementParser;
/**
 * @ClassNameApplication
 * @Description
 * @Author sanmengcc
 * @Date2020/7/2 17:42
 * @Version V1.0
 **/
public class Application {

    public static void main(String[] args) {
        String sql = "UPDATE sy SET id=xxxxxxxx-xxx, name = nelue/2 where 1 =1";
        SQLStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement statement = parser.parseStatement();
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        statement.accept(visitor);
        System.out.println("Columns = "+visitor.getColumns());
        System.out.println("Tables = "+visitor.getTables());
        System.out.println("DbType = "+visitor.getDbType());
        MySqlUpdateStatement myStatement = (MySqlUpdateStatement) statement;
        System.out.println("myStatement.getItems = " + myStatement.getItems());
    }
}

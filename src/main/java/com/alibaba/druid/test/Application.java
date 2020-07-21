package com.alibaba.druid.test;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.util.MysqlWhereUpdater;

import java.util.Optional;

/**
 * @ClassNameApplication
 * @Description
 * @Author sanmengcc
 * @Date2020/7/2 17:42
 * @Version V1.0
 **/
public class Application {


    public static void main(String[] args) {
        String sql = "SELECT count(1) AS count, b.STATUS, b.type FROM ( SELECT DISTINCT a.id, a.type AS type, a.activity_status AS STATUS FROM t_activity a, t_activity_project p WHERE a.id = p.activity_id AND a.creator_id IN ('1') AND a.reviewer_id IN ('1') AND (a.creator_id IN ('1') OR a.reviewer_id IN ('1')) AND ((p.province_code IN ('1') AND p.`city_code` = '0') OR (p.city_code IN ('1') AND p.`area_code` = '0') OR (p.area_code IN ('1') AND p.street_code = '0') OR (p.street_code IN ('1') AND p.project_code = '0') OR p.project_code IN ('1')) AND a.oem = '4401' GROUP BY a.id ) b GROUP BY b.type, b.STATUS";
        String alias = "a";
        String field = "oem";
        String fieldValue = "gz";
        Optional.ofNullable(MysqlWhereUpdater.addWhereSql(sql, MysqlWhereUpdater.fullWhereField(alias, field,
                new SQLCharExpr(fieldValue), SQLBinaryOperator.Equality)))
                .ifPresent(System.out::println);
    }



}

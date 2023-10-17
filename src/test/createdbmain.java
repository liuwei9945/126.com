package test;

import java.util.ArrayList;
import java.util.List;



public class createdbmain {
     public static void main(String[] args) {

         test.DBUtil.setConnection();
         String tablename="hadwn_assis_tab";
         String classname="classname";
         List list=new ArrayList();
         test.hadwn_assis_tab n=new test.hadwn_assis_tab();


         //插入表
         test.DBUtil.create(tablename,classname);
         test.DBUtil.insert(tablename,list);
         test.DBUtil.select(tablename,list);
         test.DBUtil.endConnection();


    }
}

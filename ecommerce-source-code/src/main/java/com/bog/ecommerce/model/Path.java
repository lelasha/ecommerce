package com.bog.ecommerce.model;




public class Path {
    private static final String MAIN_DIR  = "/files/";
    private static final String EXCEL_DIR  = "excel/";
    public static final String IMAGE_DIR  = "images/";

    public static String fullPAth(){
        return System.getProperty("user.dir") + MAIN_DIR;
    }

    public static String excelPAth(){
        return fullPAth() + EXCEL_DIR;
    }


}

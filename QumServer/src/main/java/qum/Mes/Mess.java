package qum.Mes;

import java.io.Serializable;

public class Mess implements Serializable {

    private static final long serialVersionUID = 1L;

    private String value1,value2,value3;

    private long fileSize;
    
    private final int serviceCode;
    
    
    public void setValue1(String value1) {
        this.value1 = value1;
    }
    
    public String getValue1() {
        return value1;
    }

    public String getValue2() {
        return value2;
    }

    public String getValue3() {
        return value3;
    }
    
    public long getFileSize() {
        return fileSize;
    }
    
    public int getServiceCode() {
        return serviceCode;
    }


    public Mess() {
	serviceCode = 0;
    }

    public Mess(String val1, String val2) {
	value1 = val1;
	value2 = val2;
	serviceCode = 0;
    }

    public Mess(String val1, String val2, int Code) {
	value1 = val1;
	value2 = val2;
	serviceCode = Code;
    }

    public Mess(String val1, String val2, String val3, int Code) {
	value1 = val1;
	value2 = val2;
	value3 = val3;
	serviceCode = Code;
    }

    public Mess(String val1, String val2, long size, int Code) {
	value1 = val1;
	value2 = val2;
	fileSize = size;
	serviceCode = Code;
    }

    public Mess(String val1, String val2, long size, String val3,int Code) {
	value1 = val1;
	value2 = val2;
	value3 = val3;
	fileSize = size;
	serviceCode = Code;
    }
}

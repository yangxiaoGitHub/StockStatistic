package test;

public class Son extends Parent {
	   private String son_str = "test son";

	   public String getSonStr() {
	      System.out.println("get son String!");
	      return "get son String";
	   }

	   public void setSonStr(String str) {
	      System.out.println("set son String: " + str);
	   }
	}
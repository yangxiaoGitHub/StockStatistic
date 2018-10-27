package test;

public class Parent extends obj {

	   public String publicField = "1";
	   String defaultField = "2";
	   protected String protectedField = "3";
	   private String privateField = "4";
	   
	   public void publicMethod() {
	      System.out.println("publicMethod...");
	   }

	   void defaultMethod() {
	      System.out.println("defaultMethod...");
	   }

	   protected void protectedMethod() {
	      System.out.println("protectedMethod...");
	   }

	   private void privateMethod() {
	      System.out.println("privateMethod...");
	   }

	@Override
	public void setBase(String value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getBase() {
		// TODO Auto-generated method stub
		return null;
	}
	}

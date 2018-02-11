package test;

public class AOPMain {

	public static void main(String[] args) {

		AOPMain aopMain = new AOPMain();
		try {
			//aopMain.proxyTest();
			aopMain.cglibTest();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setUpBeforeClass() throws Exception {

	}

	public void proxyTest() {
		JDKProxyFactory factory = new JDKProxyFactory();
		PersonService service = (PersonService) factory.createProxyInstance(new PersonServiceBean());
		service.save("proxy test!");
	}

	public void cglibTest() {
		CGlibProxyFactory factory = new CGlibProxyFactory();
		PersonServiceBean service = (PersonServiceBean) factory.createProxyInstance(new PersonServiceBean("user"));
		service.save("test CGlib!");
	}
}

package test;

public class PersonServiceBean implements PersonService {
	private String user = null;

	@Override
	public String getUser() {
		return user;
	}

	public PersonServiceBean() {

	}
	
	public PersonServiceBean(String user) {
	   this.user = user;
	}

	@Override
	public String getPersonName(Integer personId) {
		System.out.println("--->getPersonName()");
		return "getPersonName()";
	}

	@Override
	public void save(String name) {
		System.out.println("--->save()");
	}

	@Override
	public void update(String name, Integer personId) {
		System.out.println("--->update");
	}
}
package test;

public interface PersonService {

	String getUser();
	String getPersonName(Integer personId);
	void save(String name);
	void update(String name, Integer personId);
}
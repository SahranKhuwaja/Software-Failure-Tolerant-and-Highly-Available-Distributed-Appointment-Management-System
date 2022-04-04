package DAMS.Clients.Interfaces;

public interface ClientOperations {
	public void displayHeading (); 
	public String askCity();
	public String askUserId();
	public void displayCities();
	public void roleMessage();
	public String getUserInput();
	public String selectedCity(int option);
	public void customErrorMessage(String msg);
	public String getIP();
	public int getPort();
	public String haveUserId(String userId);
	public String dontHaveUserId();
	public String validateUserId(String userId);
	
}

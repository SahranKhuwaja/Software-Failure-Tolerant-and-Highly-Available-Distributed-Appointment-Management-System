package DAMS.Clients.Operations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import DAMS.Clients.Interfaces.ClientOperations;

public class Operations implements ClientOperations {

	public final List<String> cities = Arrays.asList( "Montreal (MTL)", "Quebec (QUE)", "Sherbrooke (SHE)" );
	public final List<String> citiesPrefixes = Arrays.asList("MTL","QUE", "SHE");
	public final HashMap<String, String> frontEndDetails = new HashMap<String, String>();
	public static String userId;
	public String serverName = null;
	public String role;
	
	public Operations(String role) {
		frontEndDetails.put("ip","172.20.10.2");
		frontEndDetails.put("port","6800");
		this.role = role;
	}

	@Override
	public void displayHeading() {
		String text = "WElCOME TO THE HEALTH APPOINTMENT SYSTEM";
		System.out.println(text);
	}

	@Override
	public String askUserId() {
		this.displayHeading();
		System.out.println("");
		String data;
		String serverName = null;
		this.roleMessage();
		System.out.println("");
		System.out.println("Do you have your " + role + "ID? (Y/N)");
		while (true) {
			data = (this.getUserInput()).toLowerCase();
			if (data.length() < 1) {
				this.customErrorMessage("Incorrect input! Please re-type!");
				continue;
			}
			if (data.equals("y") || data.equals("yes")) {
				serverName = this.haveUserId(data);
				break;
			} else if (data.equals("n") || data.equals("no")) {
				serverName = this.dontHaveUserId();
				break;
			} else {
				this.customErrorMessage("Incorrect input! Please re-type!");
				continue;
			}
		}
		return serverName;
	}

	@Override
	public String askCity() {
		this.displayHeading();
		System.out.println("");
		this.roleMessage();
		System.out.println("");
		System.out.println("Please select the city to generate " + role + " ID :");
		this.displayCities();

		int option = 0;

		while (true) {
			try {
				option = Integer.parseInt(this.getUserInput());
				if (option != 1 && option != 2 && option != 3) {
					this.customErrorMessage("Please enter correct value!");
					continue;
				}
				break;
			} catch (NumberFormatException e) {
				this.customErrorMessage("Please enter only numeric value!");
			}

		}
		return this.selectedCity(option);
	}

	@Override
	public void displayCities() {
		for (int i = 0; i < this.cities.size(); i++) {
			System.out.println(i + 1 + ") " + this.cities.get(i));
		}
	}

	@Override
	public void roleMessage() {

		System.out.println("Hello! You are identifed as " + role);

	}

	@Override
	public String getUserInput() {
		String data = "";
		try {
			BufferedReader bR = new BufferedReader(new InputStreamReader(System.in));
			data = bR.readLine();
		} catch (IOException e) {
			System.out.println("");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return data;
	}

	@Override
	public String selectedCity(int option) {
		String selected = this.cities.get(option - 1);
		System.out.println("You Selected: " + selected);
		serverName = selected;
		System.out.println("======================================================================");
		System.out.println("Please wait! Connecting to the " + serverName + " Server...");
		return selected;
	}

	@Override
	public void customErrorMessage(String msg) {
		System.out.println(msg);
	}

	@Override
	public String getIP() {
		return this.frontEndDetails.get("ip");
	}
	@Override
	public int getPort() {
		return Integer.parseInt(this.frontEndDetails.get("port"));
	}

	@Override
	public String haveUserId(String userId) {
		System.out.println("Please enter your " + role + "ID!");
		String data = null;
		while (data == null) {
			String id = this.getUserInput();
			data = this.validateUserId(id);
		}
		return data;
	}

	@Override
	public String dontHaveUserId() {
		
		return this.askCity();
	}

	@Override
	public String validateUserId(String userId) {
		if (userId.length() != 8) {
			this.customErrorMessage("Please enter valid 8 digits " + role + "ID!");
			return null;
		}
		String cityCode = userId.substring(0, 3).toUpperCase();
		if(citiesPrefixes.contains(cityCode)){
			for (String name : cities) {
				if (name.contains(cityCode)) {
					serverName = name;
					break;
				}
			}
			System.out.println(" ");
			System.out.println("Identified Server : " + serverName);
			if (!userId.substring(3, 4).toLowerCase().equals(role.substring(0, 1).toLowerCase())) {
				this.customErrorMessage(
						"Your " + role + "ID is incorrect! " + "Your " + role + "ID don't have proper role symbol!");
				this.customErrorMessage("Please re-enter your " + role + "ID");
				return null;
			}
			try {
				Integer.parseInt(userId.substring(4, 8));
			}catch(NumberFormatException e) {
				this.customErrorMessage("UserID should contain 4 numeric "
						+ "numbers after the role! Please re-enter!");
				return null;
			}
			System.out.println("======================================================================");
			System.out.println("Please wait! Connecting to the " + serverName + " Server...");
		} else {
			this.customErrorMessage("Please enter valid 8 digits " + role + "ID!");
			return null;
		}
		Operations.userId = userId.toUpperCase();
		return serverName;
	}

}

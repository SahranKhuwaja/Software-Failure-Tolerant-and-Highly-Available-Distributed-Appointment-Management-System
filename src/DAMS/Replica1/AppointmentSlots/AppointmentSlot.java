package DAMS.Replica1.AppointmentSlots;

public class AppointmentSlot {
	String description;
	int capacity;
	public AppointmentSlot(String description, int capacity) {
		super();
		this.description = description;
		this.capacity = capacity;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getCapacity() {
		return capacity;
	}
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	@Override
	public String toString() {
		return String.valueOf(capacity);
	}
	
}

public class Students {
	private String ID;
	private String first_name;
	private String last_name;
	private String quality_points;
	private String gpa_hours;
	private String gpa;
	
	//Students constructor
	public Students(String id, String fname, String lname, String points, String hours, String gpa){
		ID = id;
		first_name = fname;
		last_name = lname;
		quality_points = points;
		gpa_hours = hours;
		this.gpa = gpa;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getFirst_name() {
		return first_name;
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public String getQuality_points() {
		return quality_points;
	}

	public void setQuality_points(String quality_points) {
		this.quality_points = quality_points;
	}

	public String getGpa_hours() {
		return gpa_hours;
	}

	public void setGpa_hours(String gpa_hours) {
		this.gpa_hours = gpa_hours;
	}

	public String getGpa() {
		return gpa;
	}

	public void setGpa(String gpa) {
		this.gpa = gpa;
	}
}

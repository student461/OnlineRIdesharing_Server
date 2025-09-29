package model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

	@Document("drivers")
	public class drivers {

			@Id
			private String id;
			private String clerkId;
			
			private String name;
			private String email;
			private String phone;
			private String role;
			private String homeLocation;
			private String vehicleNumber;
			 private  int totalRating;
			    private int ratingCount;
			    private  int rating;
			    
			public drivers(String id, String clerkId, String name, String email, String phone, String role,
					String homeLocation, String vehicleNumber, int totalRating, int ratingCount, int rating) {
				super();
				this.id = id;
				this.clerkId = clerkId;
				this.name = name;
				this.email = email;
				this.phone = phone;
				this.role = role;
				this.homeLocation = homeLocation;
				this.vehicleNumber = vehicleNumber;
				this.totalRating = totalRating;
				this.ratingCount = ratingCount;
				this.rating = rating;
			}
			public String getId() {
				return id;
			}
			public void setId(String id) {
				this.id = id;
			}
			public String getClerkId() {
				return clerkId;
			}
			public void setClerkId(String clerkId) {
				this.clerkId = clerkId;
			}
			public String getName() {
				return name;
			}
			public void setName(String name) {
				this.name = name;
			}
			public String getEmail() {
				return email;
			}
			public void setEmail(String email) {
				this.email = email;
			}
			public String getPhone() {
				return phone;
			}
			public void setPhone(String phone) {
				this.phone = phone;
			}
			public String getRole() {
				return role;
			}
			public void setRole(String role) {
				this.role = role;
			}
			public String getHomeLocation() {
				return homeLocation;
			}
			public void setHomeLocation(String homeLocation) {
				this.homeLocation = homeLocation;
			}
			public String getVehicleNumber() {
				return vehicleNumber;
			}
			public void setVehicleNumber(String vehicleNumber) {
				this.vehicleNumber = vehicleNumber;
			}
			public int getTotalRating() {
				return totalRating;
			}
			public void setTotalRating(int totalRating) {
				this.totalRating = totalRating;
			}
			public int getRatingCount() {
				return ratingCount;
			}
			public void setRatingCount(int ratingCount) {
				this.ratingCount = ratingCount;
			}
			public int getRating() {
				return rating;
			}
			public void setRating(int rating) {
				this.rating = rating;
			}
			
			 	}


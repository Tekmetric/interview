package com.interview.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class ShopRequest {

	@NotBlank
	@Size(min = 1, max = 255, message = "Name should not exceed 255 characters or be empty")
	private String name;

	@Size(min = 1, max = 255, message = "Address2 should not exceed 255 characters or be empty")
	private String address1;

	@Size(max = 255, message = "Address2 should not exceed 255 characters")
	private String address2;

	@Size(min = 1, max = 100, message = "City should not exceed 100 characters or be empty")
	private String city;

	@Size(min = 1, max = 20, message = "State should not exceed 20 characters or be empty")
	private String state;

	@Size(min = 1, max = 5, message = "Zip should not exceed 5 characters  or be empty")
	@Pattern(regexp = "\\d+", message = "Zip should be numeric")
	private String zip;

	@Size(min = 1, max = 10, message = "Phone number should not exceed 10 characters or be empty")
	@Pattern(regexp = "\\d+", message = "Phone number should be numeric")
	private String phoneNumber;

	@Email
	@Size(min = 1, max = 255, message = "Email should not exceed 255 characters or be empty")
	private String email;

	@Size(min = 1, max = 255, message = "Email should not exceed 255 characters or be empty")
	private String website;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

}

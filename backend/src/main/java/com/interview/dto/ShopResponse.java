package com.interview.dto;

import com.interview.data.Shop;

public class ShopResponse {

	private Long id;
	private String name;
	private String address1;
	private String address2;
	private String city;
	private String state;
	private String zip;
	private String phoneNumber;
	private String email;
	private String website;

	public ShopResponse(Shop shop) {
		this.id = shop.getId();
		this.name = shop.getName();
		this.address1 = shop.getAddress1();
		this.address2 = shop.getAddress2();
		this.city = shop.getCity();
		this.state = shop.getState();
		this.zip = shop.getZip();
		this.phoneNumber = shop.getPhoneNumber();
		this.email = shop.getEmail();
		this.website = shop.getWebsite();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

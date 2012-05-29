package com.totango.eloqua.data;

import java.util.List;

public class ActiveList {
	private String id;
	private String name;
	private List<String> accounts;
	
	public ActiveList(){}
	
	public ActiveList(String id, String name, List<String> accounts) {
		super();
		this.id = id;
		this.name = name;
		this.accounts = accounts;
	}

	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<String> accounts) {
		this.accounts = accounts;
	}
	
	
	
}

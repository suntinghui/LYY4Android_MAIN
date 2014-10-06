package com.people.lyy.jababean;

import java.io.Serializable;

public class Balance implements Serializable {
	public String balance;
	private String can_cost;
	
	public Balance(String balance,String can_cost){
		this.balance = balance;
		this.can_cost = can_cost;
	}

	public String getBalance() {
		return balance;
	}

	public void setBalance(String balance) {
		this.balance = balance;
	}

	public String getCan_cost() {
		return can_cost;
	}

	public void setCan_cost(String can_cost) {
		this.can_cost = can_cost;
	}



}

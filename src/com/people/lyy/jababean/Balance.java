package com.people.lyy.jababean;

import java.io.Serializable;

public class Balance implements Serializable {
	public String balance;
	private String can_cost;
	private String tag_code;
	private String tag_end;
	private String tip;

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

	public String getTag_code() {
		return tag_code;
	}

	public void setTag_code(String tag_code) {
		this.tag_code = tag_code;
	}

	public String getTag_end() {
		return tag_end;
	}

	public void setTag_end(String tag_end) {
		this.tag_end = tag_end;
	}

	public String getTip() {
		return tip;
	}

	public void setTip(String tip) {
		this.tip = tip;
	}

}

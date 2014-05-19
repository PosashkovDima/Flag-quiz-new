package com.example.flagquiz3;

public class Champion implements Comparable<Champion> {
	private String name;
	private double result;
	private String date;

	public Champion(String name, double result, String date) {
		this.name = name;
		this.result = result;
		this.date = date;
	}

	public Champion() {

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getResult() {
		return result;
	}

	public void setResult(double result) {
		this.result = result;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	@Override
	public int compareTo(Champion another) {
		return (int) (this.result - another.result);
	}
}

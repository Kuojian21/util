package com.tool.kj.model;

import java.util.List;

import com.beust.jcommander.internal.Lists;

public class Model {
	private String name;
	private List<Cell> cells = Lists.newArrayList();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Cell> getCells() {
		return cells;
	}

	public void addCell(Cell cell) {
		this.cells.add(cell);
	}

	public void setCells(List<Cell> cells) {
		this.cells = cells;
	}
	

}

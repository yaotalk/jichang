package com.minivision.cameraplat.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class FaceTmp {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	private String taskId;
	private String picId;
	//1为保存的记录，0为不保存的记录，如果都是0，随机删除
	private int isStore; 

	public FaceTmp() {

	}

	public FaceTmp(String picId, String taskId) {
		this.taskId = taskId;
		this.picId = picId;
		isStore = 0;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getPicId() {
		return picId;
	}

	public void setPicId(String picId) {
		this.picId = picId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIsStore() {
		return isStore;
	}

	public void setIsStore(int isStore) {
		this.isStore = isStore;
	}

}

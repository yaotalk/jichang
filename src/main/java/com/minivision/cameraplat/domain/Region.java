package com.minivision.cameraplat.domain;


import javax.persistence.*;

@Entity
public class Region extends IdEntity{
  private String name; // 区域名称
  @ManyToOne(optional = true)
  @JoinColumn(name = "parent_id", nullable = true)
  private Region parentNode; // 父节点

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Region getParentNode() {
    return parentNode;
  }

  public Region setParentNode(Region parentNode) {
    this.parentNode = parentNode;
    return this;
  }
  
  public String getFullName(){
    if(parentNode!=null){
      return parentNode.getFullName()+"/"+name;
    }else{
      return name;
    }
  }
  @Override public String toString() {
    return "区域{ id='" + id + '\''+ ", 名称='" + name + '\'' + ", 父节点ID=" + (parentNode==null?null:parentNode.getId()) + '}';
  }
}

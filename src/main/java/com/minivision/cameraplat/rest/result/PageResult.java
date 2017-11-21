package com.minivision.cameraplat.rest.result;

import java.util.List;

import org.springframework.data.domain.Page;

public class PageResult<T> {
  private long total;
  private List<T> rows;
  
  public PageResult(long total, List<T> rows) {
    this.total = total;
    this.rows = rows;
  }
  
  public PageResult(Page<T> page) {
    this.total = page.getTotalElements();
    this.rows = page.getContent();
  }
  
  public long getTotal() {
    return total;
  }
  public void setTotal(long total) {
    this.total = total;
  }

  public List<T> getRows() {
    return rows;
  }

  public void setRows(List<T> rows) {
    this.rows = rows;
  }

  @Override
  public String toString() {
    return "PageResult [total=" + total + ", rows=" + rows + "]";
  }
  
}

package com.minivision.cameraplat.util;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class ChunkRequest implements Pageable {
  private int limit = 0;
  private int offset = 0;
  private Sort sort;

  public ChunkRequest(int offset, int limit) {
    if (offset < 0)
      throw new IllegalArgumentException("Offset must not be less than zero!");

    if (limit < 1)
      throw new IllegalArgumentException("Limit must not be less than one!");

    this.offset = offset;
    this.limit = limit;
  }
  public ChunkRequest(int offset, int limit,Sort sort) {
    if (offset < 0)
      throw new IllegalArgumentException("Offset must not be less than zero!");

    if (limit < 1)
      throw new IllegalArgumentException("Limit must not be less than one!");

    this.offset = offset;
    this.limit = limit;
    this.sort = sort;
  }
  @Override
  public int getPageNumber() {
    return 0;
  }

  @Override
  public int getPageSize() {
    return limit;
  }

  @Override
  public int getOffset() {
    return offset;
  }

  @Override
  public Sort getSort() {
    return sort;
  }

  @Override
  public Pageable next() {
    return null;
  }

  @Override
  public Pageable previousOrFirst() {
    return this;
  }

  @Override
  public Pageable first() {
    return this;
  }

  @Override
  public boolean hasPrevious() {
    return false;
  }

}

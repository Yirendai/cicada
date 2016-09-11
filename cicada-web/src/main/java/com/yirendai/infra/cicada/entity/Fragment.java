package com.yirendai.infra.cicada.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Fragment {
  private int start;
  private int end;
}

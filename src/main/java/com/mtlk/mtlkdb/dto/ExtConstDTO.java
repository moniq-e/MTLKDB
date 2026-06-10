package com.mtlk.mtlkdb.dto;

import com.mtlk.mtlkdb.struct.ConstraintMap;

public record ExtConstDTO(ConstraintMap[] constraints, int diff) {}
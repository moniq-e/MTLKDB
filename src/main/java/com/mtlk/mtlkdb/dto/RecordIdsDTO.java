package com.mtlk.mtlkdb.dto;

import java.util.List;

import com.mtlk.mtlkdb.struct.RecordId;

public record RecordIdsDTO(List<RecordId> recordIds, int lastKey) {}

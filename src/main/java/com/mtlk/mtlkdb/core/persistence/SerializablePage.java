package com.mtlk.mtlkdb.core.persistence;

import com.mtlk.mtlkdb.struct.encoder.PersistByteArray;

public interface SerializablePage {

    public PersistByteArray serialize();
}

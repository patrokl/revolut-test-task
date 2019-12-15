package com.revolut.dao;

import com.revolut.model.Transfer;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class TransferDao {

    private final Map<Long, Transfer> transfersStorage = new ConcurrentHashMap<>();

    public void save(Long id, Transfer transfer) {
        transfersStorage.put(id, transfer);
    }

    public Collection<Transfer> findAll() {
        return transfersStorage.values();
    }
}

package main.repository.interfaces;

import main.model.Index;

import java.util.List;

public interface IndexRepositoryCustom {
    void insertIndexList(String siteName, List<Index> indices);
}

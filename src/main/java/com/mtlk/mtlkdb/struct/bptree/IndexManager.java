package com.mtlk.mtlkdb.struct.bptree;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.mtlk.mtlkdb.core.persistence.DiskManager;
import com.mtlk.mtlkdb.struct.RecordId;

public class IndexManager {
    private DiskManager indexDM;
    private int rootPageId;

    public IndexManager(String indexFileName, int rootPageId) throws IOException {
        this.indexDM = new DiskManager(indexFileName);
        this.rootPageId = rootPageId;
    }

    public RecordId search(int key) throws Exception {
        int currentPageId = rootPageId;
        
        // Carrega a página raiz do disco
        byte[] pageData = indexDM.readPage(currentPageId);
        
        // Enquanto for um Nó Interno (tipo 0), continua descendo a árvore
        while (pageData[0] == 0) { 
            currentPageId = findChildPageIdInInternalNode(pageData, key);
            pageData = indexDM.readPage(currentPageId); // I/O de busca
        }
        
        // Se saiu do laço, achou a página Folha (tipo 1)
        var leaf = IndexLeafPage.deserialize(currentPageId, pageData);
        
        // Busca binária ou linear dentro da lista de chaves da página carregada
        for (int i = 0; i < leaf.keys.size(); i++) {
            if (leaf.keys.get(i) == key) {
                return leaf.rids.get(i); // Retorna as coordenadas [page, slot] do arquivo .dat
            }
        }
        
        return null; // Chave não encontrada
    }

    private int findChildPageIdInInternalNode(byte[] internalPageData, int key) {
        ByteBuffer buffer = ByteBuffer.wrap(internalPageData);
        buffer.get(); // Pula o tipo
        int keyCount = buffer.getInt();
        
        // Nós internos guardam: [Ponteiro 0, Chave 1, Ponteiro 1, Chave 2, Ponteiro 2...]
        // Implementar a lógica de comparação para decidir qual Pointer ID retornar
        // Exemplo simplificado: retornar o ponteiro com base no intervalo da chave
        return buffer.getInt(5); // Retorna o ID da próxima página a ser lida
    }
}

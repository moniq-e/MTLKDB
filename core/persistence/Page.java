package core.persistence;

public class Page {
    private byte[] data;
    
    public Page(byte[] data) {
        this.data = data;
    }

    // Resolve o acesso à linha específica usando o ID (o seu bloco "IDX")
    public byte[] getRecord(int slotId) {
        // O cabeçalho da página contém os ponteiros. 
        // Pula a contagem (2 bytes) + (slotId * 2 bytes por offset)
        int offsetAddress = 2 + (slotId * 2);
        int recordStart = data.getShort(offsetAddress);
        
        // Move o ponteiro do buffer para a posição exata da linha dentro do array de bytes
        data.position(recordStart);
        
        // Lê o tamanho do registro (ex: 1 byte) e extrai os dados brutos da linha
        int length = data.get();
        byte[] recordBytes = new byte[length];
        data.get(recordBytes);
        
        // Este array de bytes bruto será depois desserializado pela classe baseada no "TABLE INFO"
        return recordBytes;
    }
}
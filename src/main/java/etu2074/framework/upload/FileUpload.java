package etu2074.framework.upload;


public class FileUpload {
    private String nom;
    private byte[] bytes;
    private String path;
    public FileUpload(String nom,byte[]bytes,String path){
        System.out.println(nom);
        setNom(nom);
        setBytes(bytes);
        setPath(path);
    }

    public FileUpload(){

    }

    public String getNom() {
        return nom;
    }

    public void setNom(String name) {
        this.nom = name;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}


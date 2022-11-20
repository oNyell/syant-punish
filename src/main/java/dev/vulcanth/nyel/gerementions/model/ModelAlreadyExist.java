package dev.vulcanth.nyel.gerementions.model;

public class ModelAlreadyExist extends Exception {

    public ModelAlreadyExist(){
        super("Este modelo já existe.");
    }

}

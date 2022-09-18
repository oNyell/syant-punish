package com.ayane.nyel.other.model;

public class ModelAlreadyExist extends Exception {

    public ModelAlreadyExist(){
        super("Este modelo já existe.");
    }

}

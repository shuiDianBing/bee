package com.skynet.network;

public class Parameter<Type> implements Runnable {
    private Request requestService;
    private ResponseListener responseListener;

    public Parameter(Request requestService, ResponseListener responseListener) {
        this.requestService = requestService;
        this.responseListener = responseListener;
    }

    @Override
    public void run() {
        requestService.execute();
    }
}

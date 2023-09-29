package com.github.asodja.task.api;

import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;

class MyTaskAction implements TaskAction<MyTaskAction.MyTaskActionParams, MyTaskActionParamsGenerated> {

    interface MyTaskActionParams extends TaskParameters {
        @Input
        Property<String> getInput();
        @Input
        Property<String> getInput2();
    }

    @Override
    public void execute(MyTaskActionParamsGenerated params) {
        System.out.println("MyTaskAction.execute: " + params.getInput());
        System.out.println("MyTaskAction.execute: " + params.getInput2());
    }
}

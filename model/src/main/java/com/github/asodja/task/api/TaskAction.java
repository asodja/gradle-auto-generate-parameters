package com.github.asodja.task.api;

public interface TaskAction<T extends TaskParameters, P> {

    void execute(P params);
}

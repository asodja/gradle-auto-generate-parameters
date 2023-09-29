package com.github.asodja.task.api;

import com.github.asodja.task.api.MyKtTaskAction.MyKtTaskActionParams
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input

class MyKtTaskAction: TaskAction<MyKtTaskActionParams, MyKtTaskActionParamsGeneratedViaKsp> {

    interface MyKtTaskActionParams : TaskParameters {
        @get:Input
        val input: Property<String>
        @get:Input
        val input2: Property<String>
    }

    override fun execute(params: MyKtTaskActionParamsGeneratedViaKsp) {
        println("MyKtTaskAction.execute: ${params.input}")
        println("MyKtTaskAction.execute: ${params.input2}")
    }
}

package com.granular.model;

public class Task {
    private Long targetQuantity;
    private Product targetProduct;
    private Status taskStatus;

    public Task(Long targetQuantity, Product targetProduct, Status taskStatus) {
        this.targetQuantity = targetQuantity;
        this.targetProduct = targetProduct;
    }

    public Long getTargetQuantity() {
        return targetQuantity;
    }

    public Product getTargetProduct() {
        return targetProduct;
    }

    public Status getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Status taskStatus) {
        this.taskStatus = taskStatus;
    }
}
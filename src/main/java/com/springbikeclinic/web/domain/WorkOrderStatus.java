package com.springbikeclinic.web.domain;

public enum WorkOrderStatus {
    DRAFT, SUBMITTED, AWAIT_DROP_OFF, PENDING, IN_PROCESS, HOLD_PARTS, HOLD_CUSTOMER, WORK_COMPLETE, PICKED_UP, CANCELLED;
}

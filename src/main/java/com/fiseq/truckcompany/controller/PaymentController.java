package com.fiseq.truckcompany.controller;

import com.fiseq.truckcompany.service.PaymentServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController {

    private final PaymentServiceImpl paymentServiceImpl;

    @Autowired
    public PaymentController(final PaymentServiceImpl paymentServiceImpl) {
        this.paymentServiceImpl = paymentServiceImpl;
    }


}

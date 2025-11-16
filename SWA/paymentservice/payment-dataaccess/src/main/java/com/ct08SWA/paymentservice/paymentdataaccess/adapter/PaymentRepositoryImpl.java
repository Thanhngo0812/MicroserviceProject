package com.ct08SWA.paymentservice.paymentdataaccess.adapter;

import com.ct08SWA.paymentservice.paymentapplicationservice.ports.outputports.PaymentRepository;
import com.ct08SWA.paymentservice.paymentdataaccess.entity.PaymentEntity;
import com.ct08SWA.paymentservice.paymentdataaccess.mapper.PaymentDataAccessMapper;
import com.ct08SWA.paymentservice.paymentdataaccess.repository.PaymentJpaRepository;
import com.ct08SWA.paymentservice.paymentdomaincore.entity.Payment;
import com.ct08SWA.paymentservice.paymentdomaincore.valueobject.OrderId;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Implementation cho PaymentRepository (Output Port).
 * Sử dụng JpaRepository và Mapper để tương tác DB.
 */
@Component // Đánh dấu là Spring Bean
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;
    private final PaymentDataAccessMapper paymentDataAccessMapper;

    public PaymentRepositoryImpl(PaymentJpaRepository paymentJpaRepository,
                                 PaymentDataAccessMapper paymentDataAccessMapper) {
        this.paymentJpaRepository = paymentJpaRepository;
        this.paymentDataAccessMapper = paymentDataAccessMapper;
    }

    @Override
    public Payment save(Payment payment) {
        PaymentEntity paymentEntity = paymentDataAccessMapper.paymentToPaymentEntity(payment);
        PaymentEntity savedEntity = paymentJpaRepository.save(paymentEntity);
        return paymentDataAccessMapper.paymentEntityToPayment(savedEntity);
    }

    @Override
    public Optional<Payment> findByOrderId(OrderId orderId) {
        return paymentJpaRepository.findByOrderId(orderId.getValue())
                .map(paymentDataAccessMapper::paymentEntityToPayment);
    }
}


package com.ct08SWA.paymentservice.paymentapplicationservice.ports.outputports;

import com.ct08SWA.paymentservice.paymentdomaincore.entity.CreditEntry;
import com.ct08SWA.paymentservice.paymentdomaincore.valueobject.CustomerId;

import java.util.Optional;

/**
 * Output Port: Interface định nghĩa các thao tác cần thiết với CreditEntry entity.
 */
public interface CreditEntryRepository {

    /**
     * Lưu một đối tượng CreditEntry.
     * @param creditEntry CreditEntry entity.
     * @return CreditEntry entity đã được lưu.
     */
    CreditEntry save(CreditEntry creditEntry);

    /**
     * Tìm kiếm CreditEntry dựa trên CustomerId.
     * @param customerId ID của Customer.
     * @return Optional chứa CreditEntry nếu tìm thấy.
     */
    Optional<CreditEntry> findByCustomerId(CustomerId customerId);
}

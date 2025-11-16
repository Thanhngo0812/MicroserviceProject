package com.ct08SWA.paymentservice.paymentapplicationservice.ports.outputports;
import com.ct08SWA.paymentservice.paymentdomaincore.entity.CreditHistory;
import com.ct08SWA.paymentservice.paymentdomaincore.valueobject.CustomerId;

import java.util.List;

/**
 * Output Port: Interface định nghĩa các thao tác cần thiết với CreditHistory entity.
 */
public interface CreditHistoryRepository {

    /**
     * Lưu một đối tượng CreditHistory.
     * @param creditHistory CreditHistory entity.
     * @return CreditHistory entity đã được lưu.
     */
    CreditHistory save(CreditHistory creditHistory);

    /**
     * Tìm kiếm tất cả CreditHistory dựa trên CustomerId.
     * @param customerId ID của Customer.
     * @return Danh sách CreditHistory (có thể rỗng).
     */
    List<CreditHistory> findByCustomerId(CustomerId customerId);
}

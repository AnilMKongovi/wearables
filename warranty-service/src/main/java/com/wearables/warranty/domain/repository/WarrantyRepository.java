/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.wearables.warranty.domain.repository;

import com.wearables.warranty.domain.entity.WarrantyRegistration;
import com.wearables.warranty.domain.entity.WarrantyStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WarrantyRepository extends JpaRepository<WarrantyRegistration, UUID> {

    List<WarrantyRegistration> findByUserId(UUID userId);

    Optional<WarrantyRegistration> findBySerialNumber(String serialNumber);

    boolean existsBySerialNumber(String serialNumber);

    /**
     * Used by the scheduler-service to find warranties expiring on or before
     * a given date so expiry notifications can be dispatched proactively.
     */
    List<WarrantyRegistration> findByStatusAndWarrantyEndDateLessThanEqual(
            WarrantyStatus status, LocalDate date);
}

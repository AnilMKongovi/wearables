/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.wearables.claim.domain.repository;

import com.wearables.claim.domain.entity.WarrantyClaim;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClaimRepository extends JpaRepository<WarrantyClaim, UUID> {

    List<WarrantyClaim> findByWarrantyId(UUID warrantyId);

    Optional<WarrantyClaim> findByIdAndWarrantyId(UUID id, UUID warrantyId);
}

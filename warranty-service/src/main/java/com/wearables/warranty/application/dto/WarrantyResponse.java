/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.wearables.warranty.application.dto;

import com.wearables.warranty.domain.entity.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record WarrantyResponse(
        UUID id,
        UUID userId,
        UUID deviceRegistryId,
        String serialNumber,
        String productId,
        String productSku,
        String productName,
        LocalDate purchaseDate,
        LocalDate warrantyStartDate,
        LocalDate warrantyEndDate,
        int warrantyDurationMonths,
        WarrantyType warrantyType,
        WarrantyStatus status,
        VerificationStatus verificationStatus,
        SellerType sellerType,
        String sellerName,
        String orderNumber,
        String invoiceUrl,
        BigDecimal purchasePrice,
        String currency,
        String purchaseCountry,
        Instant registeredAt,
        Instant updatedAt
) {
    public static WarrantyResponse from(WarrantyRegistration w) {
        return new WarrantyResponse(
                w.getId(), w.getUserId(), w.getDeviceRegistryId(),
                w.getSerialNumber(), w.getProductId(), w.getProductSku(), w.getProductName(),
                w.getPurchaseDate(), w.getWarrantyStartDate(), w.getWarrantyEndDate(),
                w.getWarrantyDurationMonths(), w.getWarrantyType(),
                w.getStatus(), w.getVerificationStatus(),
                w.getSellerType(), w.getSellerName(), w.getOrderNumber(),
                w.getInvoiceUrl(), w.getPurchasePrice(), w.getCurrency(),
                w.getPurchaseCountry(), w.getRegisteredAt(), w.getUpdatedAt()
        );
    }
}

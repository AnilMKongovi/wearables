/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.wearables.warranty.application.dto;

import com.wearables.warranty.domain.entity.SellerType;
import com.wearables.warranty.domain.entity.WarrantyType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record RegisterWarrantyRequest(

        /** Unique hardware serial number printed on the device / packaging. */
        @NotBlank String serialNumber,

        @NotBlank String productId,
        @NotBlank String productSku,
        @NotBlank String productName,

        /** Date of purchase as shown on the invoice (must not be in the future). */
        @NotNull @PastOrPresent LocalDate purchaseDate,

        /** Warranty length in months from the start date (e.g. 12, 24). */
        @NotNull @Min(1) @Max(120) Integer warrantyDurationMonths,

        @NotNull WarrantyType warrantyType,

        @NotNull SellerType sellerType,

        /**
         * Human-readable seller name. Required when sellerType = OTHER;
         * optional for known seller types as additional context.
         */
        String sellerName,

        /** Invoice / order reference number from the seller. */
        @NotBlank String orderNumber,

        /** URL to an uploaded image of the receipt or invoice. */
        String invoiceUrl,

        BigDecimal purchasePrice,

        /** ISO 4217 currency code (e.g. INR, USD, EUR). */
        @Size(min = 3, max = 3) String currency,

        /**
         * Country where the product was purchased.
         * ISO 3166-1 alpha-2 (e.g. IN, US, DE).
         */
        @NotBlank @Size(min = 2, max = 2) String purchaseCountry,

        /**
         * Optional: UUID of the device record in device-registry-service.
         * Can be set later via a separate update if not known at registration time.
         */
        UUID deviceRegistryId
) {}

/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.wearables.warranty.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "warranty_registrations", indexes = {
        @Index(name = "idx_warranty_user_id",      columnList = "user_id"),
        @Index(name = "idx_warranty_serial_number", columnList = "serial_number", unique = true)
})
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class WarrantyRegistration {

    @Id
    private UUID id;

    /** Owner — matches userId in user-service. */
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    /**
     * Optional link to the physical device registered in device-registry-service.
     * May be null at registration time and filled in when the user activates the device.
     */
    @Column(name = "device_registry_id")
    private UUID deviceRegistryId;

    /** Unique hardware serial number printed on the device. */
    @Column(name = "serial_number", nullable = false, unique = true)
    private String serialNumber;

    @Column(name = "product_id", nullable = false)
    private String productId;

    @Column(name = "product_sku", nullable = false)
    private String productSku;

    /** Denormalized display name — stable even if the product catalog changes. */
    @Column(name = "product_name", nullable = false)
    private String productName;

    /** Date of purchase as shown on the invoice. */
    @Column(name = "purchase_date", nullable = false)
    private LocalDate purchaseDate;

    /** Warranty clock start (usually equals purchaseDate; may differ for refurb units). */
    @Column(name = "warranty_start_date", nullable = false)
    private LocalDate warrantyStartDate;

    /** Warranty expiry date — derived from warrantyStartDate + warrantyDurationMonths. */
    @Column(name = "warranty_end_date", nullable = false)
    private LocalDate warrantyEndDate;

    /** Stored for display; calculated as months between start and end dates. */
    @Column(name = "warranty_duration_months", nullable = false)
    private int warrantyDurationMonths;

    @Enumerated(EnumType.STRING)
    @Column(name = "warranty_type", nullable = false, length = 32)
    private WarrantyType warrantyType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private WarrantyStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false, length = 32)
    private VerificationStatus verificationStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "seller_type", nullable = false, length = 32)
    private SellerType sellerType;

    /** Free-text seller name — required when sellerType = OTHER, optional otherwise. */
    @Column(name = "seller_name")
    private String sellerName;

    /** Invoice / order reference number from the seller. */
    @Column(name = "order_number", nullable = false)
    private String orderNumber;

    /** URL of the uploaded receipt or invoice image (S3 / CDN). */
    @Column(name = "invoice_url")
    private String invoiceUrl;

    /** Purchase price — used for replacement-value calculations in claims. */
    @Column(name = "purchase_price", precision = 12, scale = 2)
    private BigDecimal purchasePrice;

    /** ISO 4217 currency code (e.g. INR, USD, EUR). */
    @Column(name = "currency", length = 3)
    private String currency;

    /**
     * Country where the product was purchased — ISO 3166-1 alpha-2 (e.g. IN, US, DE).
     * Determines warranty jurisdiction and statutory minimums.
     */
    @Column(name = "purchase_country", nullable = false, length = 2)
    private String purchaseCountry;

    @Column(name = "registered_at", nullable = false, updatable = false)
    private Instant registeredAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}

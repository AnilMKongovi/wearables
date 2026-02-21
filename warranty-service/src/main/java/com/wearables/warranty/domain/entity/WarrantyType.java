/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.wearables.warranty.domain.entity;

public enum WarrantyType {
    /** Factory-included warranty covering manufacturing defects. */
    STANDARD,

    /** Paid or promotional extension beyond the standard term. */
    EXTENDED,

    /** Covers accidental physical or liquid damage in addition to defects. */
    ACCIDENTAL_DAMAGE,

    /** Covers manufacturing defects only — no accidental damage. */
    MANUFACTURER_DEFECT_ONLY
}

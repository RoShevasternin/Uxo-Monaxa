package com.uxo.monax.util.billingManager

import com.android.billingclient.api.BillingClient

enum class InAppProduct(val productId: String, val type: String, val isConsumable: Boolean) {
    //COINS_1000    ("coins_1000"    , BillingClient.ProductType.INAPP, true  ),
    NO_ADS_FOREVER("no_ads_forever", BillingClient.ProductType.INAPP, false ),
    SUB_1_MONTH   ("sub_1_month"   , BillingClient.ProductType.SUBS , false ),
}

package com.uxo.monax.util.billingManager

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*
import com.uxo.monax.util.log


class BillingManager(
    private val context: Context,
    private val onPurchaseSuccess: (InAppProduct) -> Unit
) : PurchasesUpdatedListener {

    private val pendingPurchasesParams: PendingPurchasesParams = PendingPurchasesParams.newBuilder()
        .enableOneTimeProducts()
        .build()

    private val billingClient: BillingClient = BillingClient.newBuilder(context)
        .enablePendingPurchases(pendingPurchasesParams)
        .setListener(this)
        .build()

    // Збереження деталей продуктів
    private val productDetailsMap = mutableMapOf<InAppProduct, ProductDetails>()

    init {
        startConnection()
    }

    private fun startConnection() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    log("Billing: BillingClient підключений")
                    queryProductDetails()
                    queryPurchases() // ✅ Відновлення покупок
                }
            }

            override fun onBillingServiceDisconnected() {
                log("Billing: BillingClient відключено | restart")
                startConnection()
            }
        })
    }

    /** Запит деталей продуктів **/
    private fun queryProductDetails() {
        log("Запит продуктів...")

        queryProductList(BillingClient.ProductType.INAPP)
        queryProductList(BillingClient.ProductType.SUBS)
    }

    /** Запит деталей продуктів для певного типу (INAPP або SUBS) */
    private fun queryProductList(productType: String) {
        val listTypedProducts = InAppProduct.entries.filter { it.type == productType }

        val products = listTypedProducts
            .map {
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(it.productId)
                    .setProductType(it.type)
                    .build()
            }
        
        if (products.isNotEmpty()) {
            val params = QueryProductDetailsParams.newBuilder()
                .setProductList(products)
                .build()

            billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    productDetailsList.forEach { details ->
                        val product = listTypedProducts.find {
                            it.productId == details.productId && it.type == details.productType
                        }
                        product?.let { productDetailsMap[it] = details }
                    }
                    log("Billing: Продукти завантажені ($productType): ${productDetailsMap.keys}")
                } else {
                    log("Billing: Помилка завантаження ($productType): ${billingResult.debugMessage}")
                }
            }
        }
    }

    /** Купівля продукту */
    fun buyProduct(activity: Activity, product: InAppProduct) {
        val productDetails = productDetailsMap[product] ?: run {
            log("Billing: ProductDetails не знайдено для ${product.productId}")
            return
        }

        val productDetailsParamsBuilder = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(productDetails)

        // Додаємо offerToken, якщо це підписка (SUBS)
        if (product.type == BillingClient.ProductType.SUBS) {
            val offerToken = productDetails.subscriptionOfferDetails
                ?.firstOrNull()?.offerToken // Отримуємо перший доступний offerToken

            if (offerToken == null) {
                log("Billing: Не вдалося отримати offerToken для підписки ${product.productId}")
                return
            }

            productDetailsParamsBuilder.setOfferToken(offerToken)
        }

        val params = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(productDetailsParamsBuilder.build()))
            .build()

        billingClient.launchBillingFlow(activity, params)
    }


    /** Обробка завершеної покупки */
    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            handlePurchases(purchases)
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            log("Billing: Користувач скасував покупку")
        } else {
            log("Billing: Помилка покупки: ${billingResult.debugMessage}")
        }
    }

    /** Обробка придбаних продуктів */
    private fun handlePurchases(purchases: List<Purchase>) {
        purchases.forEach { purchase ->
            val product = InAppProduct.entries.find { it.productId in purchase.products } ?: return@forEach

            when (purchase.purchaseState) {
                Purchase.PurchaseState.PURCHASED -> {
                    log("Billing: Покупка ${purchase.products} завершена!")

                    if (product.isConsumable) {
                        consumePurchase(purchase) // ✅ Витрачаємо покупку (onPurchaseSuccess викликається там)
                    } else if (purchase.isAcknowledged.not()) {
                        acknowledgePurchase(purchase) // ✅ Підтверджуємо покупку (onPurchaseSuccess викликається там)
                    } else {
                        // ✅ Якщо покупка вже підтверджена, просто викликаємо onPurchaseSuccess
                        onPurchaseSuccess(product)
                    }
                }

                Purchase.PurchaseState.PENDING -> {
                    log("Billing: Покупка ${purchase.products} в стані PENDING. Очікуємо підтвердження.")
                    // ❗ Користувач ще не завершив покупку. Очікуємо підтвердження Google Play.
                }

                Purchase.PurchaseState.UNSPECIFIED_STATE -> {
                    log("Billing: Покупка ${purchase.products} має невизначений стан. Пропускаємо.")
                }
            }
        }
    }

    /** Підтвердження покупки */
    private fun acknowledgePurchase(purchase: Purchase) {
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        billingClient.acknowledgePurchase(params) { billingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                log("Billing: Покупка ${purchase.products} підтверджена")
                val product = InAppProduct.entries.find { it.productId in purchase.products }
                product?.let { onPurchaseSuccess(it) } // ✅ Викликаємо onPurchaseSuccess після підтвердження
            } else {
                log("Billing: Не вдалося підтвердити покупку ${purchase.products}")
            }
        }
    }

    /** Консумація витратних покупок */
    private fun consumePurchase(purchase: Purchase) {
        val params = ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        billingClient.consumeAsync(params) { billingResult, _ ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                log("Billing: Покупка ${purchase.products} витрачена")
                val product = InAppProduct.entries.find { it.productId in purchase.products }
                product?.let { onPurchaseSuccess(it) } // ✅ Викликаємо onPurchaseSuccess після витрачання
            } else {
                log("Billing: Не вдалося витратити покупку ${purchase.products}")
            }
        }
    }

    /** Відновлення покупок */
    fun queryPurchases() {
        // Запит для одноразових покупок (INAPP)
        val inAppParams = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()

        billingClient.queryPurchasesAsync(inAppParams) { billingResult, purchases ->
            val purchasedProductNames = purchases.mapNotNull { purchase ->
                InAppProduct.entries.find { it.productId in purchase.products }
            }
            log("Billing: Відновлення покупок INAPP = $purchasedProductNames")

            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                handlePurchases(purchases)
            } else {
                log("Billing: Не вдалося отримати INAPP покупки: ${billingResult.debugMessage}")
            }
        }

        // Запит для підписок (SUBS)
        val subsParams = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()

        billingClient.queryPurchasesAsync(subsParams) { billingResult, purchases ->
            val purchasedProductNames = purchases.mapNotNull { purchase ->
                InAppProduct.entries.find { it.productId in purchase.products }
            }
            log("Billing: Відновлення покупок SUBS = $purchasedProductNames")

            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                handlePurchases(purchases)
            } else {
                log("Billing: Не вдалося отримати SUBS покупки: ${billingResult.debugMessage}")
            }
        }
    }

    /** Закриття BillingClient */
    fun close() {
        billingClient.endConnection()
    }
}
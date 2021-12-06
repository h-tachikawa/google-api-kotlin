import api.ads.adgroupad.AdGroupAdClient
import api.ads.adgroupad.GoogleAdsError
import api.datafeed.DatafeedClient
import api.datafeed.status.DatafeedStatusClient
import arrow.core.*

fun main(args: Array<String>) {
    adsApiOps(args)
}

fun adsApiOps(args: Array<String>) {
    val customerIdStr = args[0]
    val adGroupIdStr = args[1]
    val adGroupAdIdStr = args[2]

    val maybeCustomerId = try {
        Some(customerIdStr.toLong())
    } catch (e: NumberFormatException) {
        none<Long>()
    }

    val maybeAdGroupId = try {
        Some(adGroupIdStr.toLong())
    } catch (e: NumberFormatException) {
        none<Long>()
    }

    val maybeAdGroupAdId = try {
        Some(adGroupAdIdStr.toLong())
    } catch (e: NumberFormatException) {
        none<Long>()
    }

    val (customerId, adGroupId, adGroupAdId) = listOf(maybeCustomerId, maybeAdGroupId, maybeAdGroupAdId).map {
        when (it) {
            is None -> throw ArithmeticException("failed to convert some of the argument value into Long.")
            is Some -> it.value
        }
    }

    AdGroupAdClient.get(customerId, adGroupId, adGroupAdId)
        .tapLeft {
            when (it) {
                is GoogleAdsError.AdNotExistsError -> TODO()
                is GoogleAdsError.ApiError -> TODO()
                is GoogleAdsError.UnknownError -> TODO()
            }
        }
        .tap { println(it) }
}

fun contentApiOps(args: Array<String>) {
    val merchantIdStr = args[0]
    val datafeedIdStr = args[1]

    val merchantId = try {
        merchantIdStr.toBigInteger()
    } catch (e: NumberFormatException) {
        null
    }

    val datafeedId = try {
        datafeedIdStr.toBigInteger()
    } catch (e: NumberFormatException) {
        null
    }

    if (merchantId == null || datafeedId == null) {
        println("$merchantIdStr does not format to Int.")
        return
    }

    val datafeedList = DatafeedClient.list(merchantId)

    datafeedList.forEach {
        println(it.id)
        println(it.name)
        println(it.fileName)
    }

    val datafeedStatus = DatafeedStatusClient.get(
        merchantId = merchantId,
        datafeedId = datafeedId,
    )

    if (datafeedStatus != null) {
        println(datafeedStatus)
    }
}

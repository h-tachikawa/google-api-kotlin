import api.ad.groupad.AdGroupAdClient
import api.datafeed.DatafeedClient
import api.datafeed.status.DatafeedStatusClient

fun main(args: Array<String>) {
    adsApiOps(args)
}

fun adsApiOps(args: Array<String>) {
    val customerIdStr = args[0]
    val adGroupIdStr = args[1]
    val adGroupAdIdStr = args[2]

    val customerId = try {
        customerIdStr.toLong()
    } catch (e: NumberFormatException) {
        null
    }

    val adGroupId = try {
        adGroupIdStr.toLong()
    } catch (e: NumberFormatException) {
        null
    }

    val adGroupAdId = try {
        adGroupAdIdStr.toLong()
    } catch (e: NumberFormatException) {
        null
    }

    if (customerId == null || adGroupId == null || adGroupAdId == null) {
        println("some required arguments is empty.")
        return
    }

    val res = AdGroupAdClient.get(customerId, adGroupId, adGroupAdId) ?: return

    val adGroupAdList = res.iterateAll().map {
        it.adGroupAd
    }

    adGroupAdList.forEach {
        println(it.toString())
    }
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

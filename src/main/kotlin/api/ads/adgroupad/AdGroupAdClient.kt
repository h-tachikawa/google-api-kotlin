package api.ads.adgroupad

import com.google.ads.googleads.lib.GoogleAdsClient
import com.google.ads.googleads.v9.services.SearchGoogleAdsRequest
import com.google.ads.googleads.v9.utils.ResourceNames
import arrow.core.Either
import com.google.ads.googleads.v9.resources.AdGroupAd

// Union Type を Kotlin の sealed class で表現している
sealed class GoogleAdsError {
    data class ApiError(val reason: Exception): GoogleAdsError()
    data class AdNotExistsError(val msg: String): GoogleAdsError()
    data class UnknownError(val reason: Exception): GoogleAdsError()
}

class AdGroupAdClient {
    companion object {
        private val client = GoogleAdsClient.newBuilder()
            .fromEnvironment()
            .fromPropertiesFile()
            .build().latestVersion.createGoogleAdsServiceClient()

        fun get(
            customerId: Long, adGroupId: Long, adGroupAdId: Long
        ): Either<GoogleAdsError, AdGroupAd> {
            val query = """
                SELECT
                  ad_group_ad.status,
                  ad_group_ad.policy_summary.approval_status
                FROM
                  ad_group_ad
                WHERE
                  ad_group_ad.resource_name = '${ResourceNames.adGroupAd(customerId, adGroupId, adGroupAdId)}'
            """

            val request = SearchGoogleAdsRequest.newBuilder()
                .setCustomerId(customerId.toString())
                .setQuery(query)
                .build()

            try {
                val adGroupAd = client.search(request)
                    .iterateAll()
                    .map { it.adGroupAd }
                    .first() ?: return Either.Left(GoogleAdsError.AdNotExistsError("AdGroupAd not exists."))
                return Either.Right(adGroupAd)
            } catch (e: com.google.api.gax.rpc.ApiException) {
                return Either.Left(GoogleAdsError.ApiError(e))
            } catch (e: Exception) {
                return Either.Left(GoogleAdsError.UnknownError(e))
            }
        }
    }
}

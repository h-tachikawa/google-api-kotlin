package api.ad.groupad

import com.google.ads.googleads.lib.GoogleAdsClient
import com.google.ads.googleads.v9.services.GoogleAdsServiceClient
import com.google.ads.googleads.v9.services.SearchGoogleAdsRequest
import com.google.ads.googleads.v9.utils.ResourceNames
import arrow.core.Either

class AdGroupAdClient {
    companion object {
        private val client = GoogleAdsClient.newBuilder()
            .fromEnvironment()
            .fromPropertiesFile()
            .build().latestVersion.createGoogleAdsServiceClient()

        fun get(
            customerId: Long, adGroupId: Long, adGroupAdId: Long
        ): Either<RuntimeException, GoogleAdsServiceClient.SearchPagedResponse> {
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

            val result = client.search(request) ?: return Either.Left(NoSuchElementException("not exists"))
            return Either.Right(result)
        }
    }
}

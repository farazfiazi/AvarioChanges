package io.avario.fansolution.android.common.data.integration.impl

import io.avario.fansolution.android.common.data.integration.Panel
import io.avario.fansolution.android.common.data.integration.ZoneAttributes
import io.avario.fansolution.android.common.data.integration.impl.entities.DiscoveryInfoResponse
import io.avario.fansolution.android.common.data.integration.impl.entities.DomainResponse
import io.avario.fansolution.android.common.data.integration.impl.entities.EntityResponse
import io.avario.fansolution.android.common.data.integration.impl.entities.GetConfigResponse
import io.avario.fansolution.android.common.data.integration.impl.entities.IntegrationRequest
import io.avario.fansolution.android.common.data.integration.impl.entities.RegisterDeviceRequest
import io.avario.fansolution.android.common.data.integration.impl.entities.RegisterDeviceResponse
import okhttp3.HttpUrl
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Url

interface IntegrationService {

    @GET("/api/discovery_info")
    suspend fun discoveryInfo(
        @Header("Authorization") auth: String
    ): DiscoveryInfoResponse

    @POST("/api/mobile_app/registrations")
    suspend fun registerDevice(
        @Header("Authorization") auth: String,
        @Body request: RegisterDeviceRequest
    ): RegisterDeviceResponse

    @GET("/api/services")
    suspend fun getServices(
        @Header("Authorization") auth: String
    ): Array<DomainResponse>

    @GET("/api/states")
    suspend fun getStates(
        @Header("Authorization") auth: String
    ): Array<EntityResponse<Any>>

    @POST
    suspend fun callWebhook(
        @Url url: HttpUrl,
        @Body request: IntegrationRequest
    ): Response<ResponseBody>

    @POST
    suspend fun getTemplate(
        @Url url: HttpUrl,
        @Body request: IntegrationRequest
    ): Map<String, String>

    @POST
    suspend fun getZones(
        @Url url: HttpUrl,
        @Body request: IntegrationRequest
    ): Array<EntityResponse<ZoneAttributes>>

    @POST
    suspend fun getConfig(
        @Url url: HttpUrl,
        @Body request: IntegrationRequest
    ): GetConfigResponse

    @POST
    suspend fun getPanels(
        @Url url: HttpUrl,
        @Body request: IntegrationRequest
    ): Array<Panel>

    @POST
    suspend fun updateSensors(
        @Url url: HttpUrl,
        @Body request: IntegrationRequest
    ): Map<String, Map<String, Any>>
}

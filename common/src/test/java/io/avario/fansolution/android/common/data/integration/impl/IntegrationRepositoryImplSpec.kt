package io.avario.fansolution.android.common.data.integration.impl

import io.avario.fansolution.android.common.data.LocalStorage
import io.avario.fansolution.android.common.data.authentication.AuthenticationRepository
import io.avario.fansolution.android.common.data.integration.DeviceRegistration
import io.avario.fansolution.android.common.data.integration.Entity
import io.avario.fansolution.android.common.data.integration.IntegrationException
import io.avario.fansolution.android.common.data.integration.UpdateLocation
import io.avario.fansolution.android.common.data.integration.ZoneAttributes
import io.avario.fansolution.android.common.data.integration.impl.entities.EntityResponse
import io.avario.fansolution.android.common.data.integration.impl.entities.IntegrationRequest
import io.avario.fansolution.android.common.data.integration.impl.entities.RegisterDeviceRequest
import io.avario.fansolution.android.common.data.integration.impl.entities.ServiceCallRequest
import io.avario.fansolution.android.common.data.integration.impl.entities.UpdateLocationRequest
import io.avario.fansolution.android.common.data.url.UrlRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyAll
import io.mockk.every
import io.mockk.mockk
import java.net.URL
import java.util.Calendar
import java.util.HashMap
import kotlin.properties.Delegates
import kotlinx.coroutines.runBlocking
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import retrofit2.Response

object IntegrationRepositoryImplSpec : Spek({

    describe("a repository") {

        val localStorage by memoized { mockk<LocalStorage>(relaxUnitFun = true) }
        val integrationService by memoized { mockk<IntegrationService>(relaxUnitFun = true) }
        val authenticationRepository by memoized { mockk<AuthenticationRepository>(relaxUnitFun = true) }
        val urlRepository by memoized { mockk<UrlRepository>(relaxUnitFun = true) }

        val repository by memoized {
            IntegrationRepositoryImpl(
                integrationService,
                authenticationRepository,
                urlRepository,
                localStorage,
                "manufacturer",
                "model",
                "osVersion",
                "deviceId"
            )
        }

        describe("registerDevicePre 0.104.0") {
            beforeEachTest {
                val deviceRegistration = DeviceRegistration(
                    "appVersion",
                    "deviceName",
                    "pushToken"
                )
                val registerDeviceRequest =
                    RegisterDeviceRequest(
                        "io.homeassistant.companion.android",
                        "Avario",
                        deviceRegistration.appVersion,
                        deviceRegistration.deviceName,
                        "manufacturer",
                        "model",
                        "Android",
                        "osVersion",
                        false,
                        hashMapOf(
                            "push_url" to "https://mobile-apps.home-assistant.io/api/sendPush/android/v1",
                            "push_token" to (deviceRegistration.pushToken ?: "push_token")
                        ),
                        "deviceId"
                    )

                coEvery { localStorage.getString("app_version") } returns "app_version"
                coEvery { localStorage.getString("device_name") } returns "device_name"
                coEvery { localStorage.getString("push_token") } returns "push_token"

                coEvery { integrationService.discoveryInfo("ABC123") } returns mockk {
                    every { version } returns "0.104.0"
                }

                coEvery {
                    integrationService.registerDevice("ABC123", registerDeviceRequest)
                } returns mockk {
                    every { cloudhookUrl } returns "https://home-assistant.io/1/"
                    every { remoteUiUrl } returns "https://home-assistant.io/2/"
                    every { secret } returns "ABCDE"
                    every { webhookId } returns "FGHIJ"
                }

                coEvery { authenticationRepository.buildBearerToken() } returns "ABC123"

                runBlocking {
                    repository.registerDevice(deviceRegistration)
                }
            }

            it("should save response data") {
                coVerify {
                    urlRepository.saveRegistrationUrls(
                        "https://home-assistant.io/1/",
                        "https://home-assistant.io/2/",
                        "FGHIJ")
                    localStorage.putString("secret", "ABCDE")
                }
            }

            it("should save registration data") {
                coVerify {
                    localStorage.putString("app_version", "appVersion")
                    localStorage.putString("device_name", "deviceName")
                    localStorage.putString("push_token", "pushToken")
                }
            }
        }

        describe("registerDevice") {
            beforeEachTest {
                val deviceRegistration = DeviceRegistration(
                    "appVersion",
                    "deviceName",
                    "pushToken"
                )
                val registerDeviceRequest =
                    RegisterDeviceRequest(
                        "io.homeassistant.companion.android",
                        "Avario",
                        deviceRegistration.appVersion,
                        deviceRegistration.deviceName,
                        "manufacturer",
                        "model",
                        "Android",
                        "osVersion",
                        false,
                        hashMapOf(
                            "push_url" to "https://mobile-apps.home-assistant.io/api/sendPush/android/v1",
                            "push_token" to (deviceRegistration.pushToken ?: "push_token")
                        ),
                        null
                    )

                coEvery { localStorage.getString("app_version") } returns "app_version"
                coEvery { localStorage.getString("device_name") } returns "device_name"
                coEvery { localStorage.getString("push_token") } returns "push_token"

                coEvery {
                    integrationService.registerDevice("ABC123", registerDeviceRequest)
                } returns mockk {
                    every { cloudhookUrl } returns "https://home-assistant.io/1/"
                    every { remoteUiUrl } returns "https://home-assistant.io/2/"
                    every { secret } returns "ABCDE"
                    every { webhookId } returns "FGHIJ"
                }

                coEvery { authenticationRepository.buildBearerToken() } returns "ABC123"

                runBlocking {
                    repository.registerDevice(deviceRegistration)
                }
            }

            it("should save response data") {
                coVerify {
                    urlRepository.saveRegistrationUrls(
                        "https://home-assistant.io/1/",
                        "https://home-assistant.io/2/",
                        "FGHIJ")
                    localStorage.putString("secret", "ABCDE")
                }
            }

            it("should save registration data") {
                coVerify {
                    localStorage.putString("app_version", "appVersion")
                    localStorage.putString("device_name", "deviceName")
                    localStorage.putString("push_token", "pushToken")
                }
            }
        }

        describe("updateRegistration") {
            val deviceRegistration = DeviceRegistration(
                "appVersion",
                "deviceName",
                "pushToken"
            )
            val registerDeviceRequest =
                RegisterDeviceRequest(
                    null,
                    null,
                    deviceRegistration.appVersion,
                    deviceRegistration.deviceName,
                    "manufacturer",
                    "model",
                    null,
                    "osVersion",
                    null,
                    hashMapOf(
                        "push_url" to "https://mobile-apps.home-assistant.io/api/sendPush/android/v1",
                        "push_token" to (deviceRegistration.pushToken ?: "push_token")
                    ),
                    null
                )
            beforeEachTest {
                coEvery {
                    integrationService.callWebhook(any(),
                        IntegrationRequest(
                            "update_registration",
                            registerDeviceRequest
                        )
                    )
                } returns Response.success(null)

                coEvery { urlRepository.getApiUrls() } returns arrayOf(
                    URL("http://best.com/hook/id"),
                    URL("http://better.com"),
                    URL("http://example.com")
                )

                coEvery { localStorage.getString("app_version") } returns "app_version"
                coEvery { localStorage.getString("device_name") } returns "device_name"
                coEvery { localStorage.getString("push_token") } returns "push_token"

                runBlocking {
                    repository.updateRegistration(deviceRegistration)
                }
            }

            it("should call the service") {
                coVerify {
                    integrationService.callWebhook(
                        "http://best.com/hook/id".toHttpUrl(),
                        IntegrationRequest(
                            "update_registration",
                            registerDeviceRequest
                        )
                    )
                }
            }

            it("should persist the registration") {
                coVerify {
                    localStorage.putString("app_version", "appVersion")
                    localStorage.putString("device_name", "deviceName")
                    localStorage.putString("push_token", "pushToken")
                }
            }
        }

        describe("is registered") {
            beforeEachTest {
                coEvery { urlRepository.getApiUrls() } returns arrayOf(
                    URL("http://best.com/hook/id"),
                    URL("http://better.com"),
                    URL("http://example.com")
                )
            }
            describe("isRegistered") {
                var isRegistered by Delegates.notNull<Boolean>()
                beforeEachTest {
                    runBlocking { isRegistered = repository.isRegistered() }
                }
                it("should return true when webhook has a value") {
                    assertThat(isRegistered).isTrue()
                }
            }
        }

        describe("is not registered") {
            beforeEachTest {
                coEvery { urlRepository.getApiUrls() } returns arrayOf()
            }
            describe("isRegistered") {
                var isRegistered by Delegates.notNull<Boolean>()
                beforeEachTest {
                    runBlocking { isRegistered = repository.isRegistered() }
                }
                it("should return false when webhook has no value") {
                    assertThat(isRegistered).isFalse()
                }
            }
        }

        describe("location updated") {

            describe("updateLocation cloud url") {
                val location = UpdateLocation(
                    "locationName",
                    arrayOf(45.0, -45.0),
                    0,
                    1,
                    2,
                    3,
                    4
                )
                val integrationRequest =
                    IntegrationRequest(
                        "update_location",
                        UpdateLocationRequest(
                            location.locationName,
                            location.gps,
                            location.gpsAccuracy,
                            location.speed,
                            location.altitude,
                            location.course,
                            location.verticalAccuracy
                        )
                    )
                beforeEachTest {
                    coEvery { urlRepository.getApiUrls() } returns arrayOf(
                        URL("http://best.com/hook/id"),
                        URL("http://better.com"),
                        URL("http://example.com")
                    )
                    coEvery {
                        integrationService.callWebhook(
                            any(), // "http://example.com/api/webhook/FGHIJ",
                            any() // integrationRequest
                        )
                    } returns Response.success(null)
                    runBlocking { repository.updateLocation(location) }
                }

                it("should call the service.") {
                    coVerify {
                        integrationService.callWebhook(
                            "http://best.com/hook/id".toHttpUrl(),
                            integrationRequest
                        )
                    }
                }
            }

            describe("updateLocation remote ui url") {
                val location = UpdateLocation(
                    "locationName",
                    arrayOf(45.0, -45.0),
                    0,
                    1,
                    2,
                    3,
                    4
                )
                val integrationRequest =
                    IntegrationRequest(
                        "update_location",
                        UpdateLocationRequest(
                            location.locationName,
                            location.gps,
                            location.gpsAccuracy,
                            location.speed,
                            location.altitude,
                            location.course,
                            location.verticalAccuracy
                        )
                    )
                beforeEachTest {
                    coEvery { urlRepository.getApiUrls() } returns arrayOf(
                        URL("http://better.com/api/webhook/FGHIJ"),
                        URL("http://example.com")
                    )
                    coEvery {
                        integrationService.callWebhook(
                            any(), // "http://example.com/api/webhook/FGHIJ",
                            any() // integrationRequest
                        )
                    } returns Response.success(null)
                    runBlocking { repository.updateLocation(location) }
                }

                it("should call the service.") {
                    coVerify {
                        integrationService.callWebhook(
                            "http://better.com/api/webhook/FGHIJ".toHttpUrl(),
                            integrationRequest
                        )
                    }
                }
            }

            describe("updateLocation auth url") {
                val location = UpdateLocation(
                    "locationName",
                    arrayOf(45.0, -45.0),
                    0,
                    1,
                    2,
                    3,
                    4
                )
                val integrationRequest =
                    IntegrationRequest(
                        "update_location",
                        UpdateLocationRequest(
                            location.locationName,
                            location.gps,
                            location.gpsAccuracy,
                            location.speed,
                            location.altitude,
                            location.course,
                            location.verticalAccuracy
                        )
                    )
                beforeEachTest {
                    coEvery { urlRepository.getApiUrls() } returns arrayOf(
                        URL("http://example.com/api/webhook/FGHIJ")
                    )
                    coEvery {
                        integrationService.callWebhook(
                            any(), // "http://example.com/api/webhook/FGHIJ",
                            any() // integrationRequest
                        )
                    } returns Response.success(null)
                    runBlocking { repository.updateLocation(location) }
                }

                it("should call the service.") {
                    coVerify {
                        integrationService.callWebhook(
                            "http://example.com/api/webhook/FGHIJ".toHttpUrl(),
                            integrationRequest
                        )
                    }
                }
            }

            describe("updateLocation fail then succeeds") {
                val location = UpdateLocation(
                    "locationName",
                    arrayOf(45.0, -45.0),
                    0,
                    1,
                    2,
                    3,
                    4
                )
                val integrationRequest =
                    IntegrationRequest(
                        "update_location",
                        UpdateLocationRequest(
                            location.locationName,
                            location.gps,
                            location.gpsAccuracy,
                            location.speed,
                            location.altitude,
                            location.course,
                            location.verticalAccuracy
                        )
                    )

                beforeEachTest {
                    coEvery { urlRepository.getApiUrls() } returns arrayOf(
                        URL("http://best.com/hook/id"),
                        URL("http://better.com/api/webhook/FGHIJ"),
                        URL("http://example.com")
                    )
                    coEvery {
                        integrationService.callWebhook(
                            "http://best.com/hook/id".toHttpUrl(),
                            any() // integrationRequest
                        )
                    } returns mockk {
                        every { isSuccessful } returns false
                    }
                    coEvery {
                        integrationService.callWebhook(
                            "http://better.com/api/webhook/FGHIJ".toHttpUrl(),
                            any() // integrationRequest
                        )
                    } returns Response.success(null)

                    runBlocking { repository.updateLocation(location) }
                }

                it("should call service 2 times") {
                    coVerifyAll {
                        integrationService.callWebhook(
                            "http://best.com/hook/id".toHttpUrl(),
                            integrationRequest
                        )
                        integrationService.callWebhook(
                            "http://better.com/api/webhook/FGHIJ".toHttpUrl(),
                            integrationRequest
                        )
                    }
                }
            }

            describe("updateLocation failure") {
                val location = UpdateLocation(
                    "locationName",
                    arrayOf(45.0, -45.0),
                    0,
                    1,
                    2,
                    3,
                    4
                )

                lateinit var thrown: Throwable

                beforeEachTest {
                    coEvery { urlRepository.getApiUrls() } returns arrayOf(
                        URL("http://best.com/hook/id"),
                        URL("http://better.com"),
                        URL("http://example.com")
                    )
                    coEvery {
                        integrationService.callWebhook(
                            "http://best.com/hook/id".toHttpUrl(),
                            any() // integrationRequest
                        )
                    } returns mockk {
                        every { isSuccessful } returns false
                    }
                    coEvery {
                        integrationService.callWebhook(
                            "http://better.com/api/webhook/FGHIJ".toHttpUrl(),
                            any() // integrationRequest
                        )
                    } returns mockk {
                        every { isSuccessful } returns false
                    }
                    coEvery {
                        integrationService.callWebhook(
                            "http://example.com/api/webhook/FGHIJ".toHttpUrl(),
                            any() // integrationRequest
                        )
                    } returns mockk {
                        every { isSuccessful } returns false
                    }

                    thrown = catchThrowable { runBlocking { repository.updateLocation(location) } }
                }

                it("should throw an exception") {
                    assertThat(thrown).isInstanceOf(IntegrationException::class.java)
                }
            }
        }

        describe("call a service") {

            describe("callService cloud url") {
                val domain = "light"
                val service = "toggle"
                val serviceDataMap = hashMapOf<String, Any>("entity_id" to "light.dummy_light")

                val serviceCallRequest =
                    ServiceCallRequest(
                        domain,
                        service,
                        serviceDataMap
                    )

                val integrationRequest =
                    IntegrationRequest(
                        "call_service",
                        serviceCallRequest
                    )

                beforeEachTest {
                    coEvery { urlRepository.getApiUrls() } returns arrayOf(
                        URL("http://best.com/hook/id"),
                        URL("http://better.com"),
                        URL("http://example.com")
                    )
                    coEvery {
                        integrationService.callWebhook(
                            any(), // "http://example.com/api/webhook/FGHIJ",
                            any() // integrationRequest
                        )
                    } returns Response.success(null)
                    runBlocking { repository.callService(domain, service, serviceDataMap) }
                }

                it("should call the service.") {
                    coVerify {
                        integrationService.callWebhook(
                            "http://best.com/hook/id".toHttpUrl(),
                            integrationRequest
                        )
                    }
                }
            }

            describe("callService remote ui url") {
                val domain = "light"
                val service = "toggle"
                val serviceDataMap = hashMapOf<String, Any>("entity_id" to "light.dummy_light")

                val serviceCallRequest =
                    ServiceCallRequest(
                        domain,
                        service,
                        serviceDataMap
                    )

                val integrationRequest =
                    IntegrationRequest(
                        "call_service",
                        serviceCallRequest
                    )

                beforeEachTest {
                    coEvery { urlRepository.getApiUrls() } returns arrayOf(
                        URL("http://better.com/api/webhook/FGHIJ"),
                        URL("http://example.com")
                    )
                    coEvery {
                        integrationService.callWebhook(
                            any(), // "http://example.com/api/webhook/FGHIJ",
                            any() // integrationRequest
                        )
                    } returns Response.success(null)
                    runBlocking { repository.callService(domain, service, serviceDataMap) }
                }

                it("should call the service.") {
                    coVerify {
                        integrationService.callWebhook(
                            "http://better.com/api/webhook/FGHIJ".toHttpUrl(),
                            integrationRequest
                        )
                    }
                }
            }

            describe("callService auth url") {
                val domain = "light"
                val service = "toggle"
                val serviceDataMap = hashMapOf<String, Any>("entity_id" to "light.dummy_light")

                val serviceCallRequest =
                    ServiceCallRequest(
                        domain,
                        service,
                        serviceDataMap
                    )

                val integrationRequest =
                    IntegrationRequest(
                        "call_service",
                        serviceCallRequest
                    )

                beforeEachTest {
                    coEvery { urlRepository.getApiUrls() } returns arrayOf(
                        URL("http://example.com/api/webhook/FGHIJ")
                    )
                    coEvery {
                        integrationService.callWebhook(
                            any(), // "http://example.com/api/webhook/FGHIJ",
                            any() // integrationRequest
                        )
                    } returns Response.success(null)
                    runBlocking { repository.callService(domain, service, serviceDataMap) }
                }

                it("should call the service.") {
                    coVerify {
                        integrationService.callWebhook(
                            "http://example.com/api/webhook/FGHIJ".toHttpUrl(),
                            integrationRequest
                        )
                    }
                }
            }

            describe("callService fail then succeeds") {
                val domain = "light"
                val service = "toggle"
                val serviceDataMap = hashMapOf<String, Any>("entity_id" to "light.dummy_light")

                val serviceCallRequest =
                    ServiceCallRequest(
                        domain,
                        service,
                        serviceDataMap
                    )

                val integrationRequest =
                    IntegrationRequest(
                        "call_service",
                        serviceCallRequest
                    )

                beforeEachTest {
                    coEvery { urlRepository.getApiUrls() } returns arrayOf(
                        URL("http://best.com/hook/id"),
                        URL("http://better.com/api/webhook/FGHIJ"),
                        URL("http://example.com")
                    )
                    coEvery {
                        integrationService.callWebhook(
                            "http://best.com/hook/id".toHttpUrl(),
                            any() // integrationRequest
                        )
                    } returns mockk {
                        every { isSuccessful } returns false
                    }
                    coEvery {
                        integrationService.callWebhook(
                            "http://better.com/api/webhook/FGHIJ".toHttpUrl(),
                            any() // integrationRequest
                        )
                    } returns Response.success(null)
                    runBlocking { repository.callService(domain, service, serviceDataMap) }
                }

                it("should call service 2 times") {
                    coVerifyAll {
                        integrationService.callWebhook(
                            "http://best.com/hook/id".toHttpUrl(),
                            integrationRequest
                        )
                        integrationService.callWebhook(
                            "http://better.com/api/webhook/FGHIJ".toHttpUrl(),
                            integrationRequest
                        )
                    }
                }
            }

            describe("callService failure") {
                val domain = "light"
                val service = "toggle"
                val serviceDataMap = hashMapOf<String, Any>("entity_id" to "light.dummy_light")

                lateinit var thrown: Throwable

                beforeEachTest {
                    coEvery { urlRepository.getApiUrls() } returns arrayOf(
                        URL("http://best.com/hook/id"),
                        URL("http://better.com"),
                        URL("http://example.com")
                    )
                    coEvery {
                        integrationService.callWebhook(
                            "http://best.com/hook/id".toHttpUrl(),
                            any() // integrationRequest
                        )
                    } returns mockk {
                        every { isSuccessful } returns false
                    }
                    coEvery {
                        integrationService.callWebhook(
                            "http://better.com/api/webhook/FGHIJ".toHttpUrl(),
                            any() // integrationRequest
                        )
                    } returns mockk {
                        every { isSuccessful } returns false
                    }
                    coEvery {
                        integrationService.callWebhook(
                            "http://example.com/api/webhook/FGHIJ".toHttpUrl(),
                            any() // integrationRequest
                        )
                    } returns mockk {
                        every { isSuccessful } returns false
                    }

                    thrown = catchThrowable {
                        runBlocking {
                            repository.callService(
                                domain,
                                service,
                                serviceDataMap
                            )
                        }
                    }
                }

                it("should throw an exception") {
                    assertThat(thrown).isInstanceOf(IntegrationException::class.java)
                }
            }
        }

        describe("get zones") {
            beforeEachTest {
                coEvery { urlRepository.getApiUrls() } returns arrayOf(
                    URL("http://best.com/"),
                    URL("http://better.com"),
                    URL("http://example.com")
                )
                coEvery { localStorage.getString("webhook_id") } returns "FGHIJ"
            }
            describe("getZones") {
                val entities =
                    EntityResponse(
                        "entityId",
                        "state",
                        ZoneAttributes(
                            false,
                            0.0,
                            1.1,
                            2.2F,
                            "fName",
                            "icon"
                        ),
                        Calendar.getInstance(),
                        Calendar.getInstance(),
                        HashMap()
                    )
                var zones: Array<Entity<ZoneAttributes>>? = null
                beforeEachTest {
                    coEvery { integrationService.getZones(any(), any()) } returns arrayOf(entities)
                    runBlocking { zones = repository.getZones() }
                }
                it("should return true when webhook has a value") {
                    assertThat(zones).isNotNull
                    assertThat(zones!!.size).isEqualTo(1)
                    assertThat(zones!![0]).isNotNull
                    assertThat(zones!![0].entityId).isEqualTo(entities.entityId)
                }
            }
        }
    }
})
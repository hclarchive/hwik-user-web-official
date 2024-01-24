package com.hwik.hcl.user.util

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.hwik.hcl.core.util.AesUtil
import com.hwik.hcl.user.util.vo.TwitterAccessTokenVo
import com.hwik.hcl.user.util.vo.TwitterResVo
import com.hwik.hcl.user.util.vo.TwitterUserFollowingVo
import com.hwik.hcl.user.util.vo.TwitterUserMeVo
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.net.URLEncoder
import java.util.*


@Service
class TwitterUtil (
    private val aesUtil: AesUtil
): SnsUtilBase(aesUtil) {
    companion object {
        private val logger = org.slf4j.LoggerFactory.getLogger(this::class.java)
    }

    @Value("\${app.sns.twitter.client-id}")
    lateinit var clientId: String

    @Value("\${app.sns.twitter.client-secret}")
    lateinit var clientSecret: String

    fun getLoginUrl(callbackUrl: String, data: String): String {
        val redirectUri = URLEncoder.encode(callbackUrl, "UTF-8")
        val state = URLEncoder.encode(data, "UTF-8")
        val authUrl: String =
            "https://twitter.com/i/oauth2/authorize?response_type=code&client_id=${clientId}&redirect_uri=${redirectUri}&scope=tweet.read%20users.read%20follows.write%20list.read&state=${state}&code_challenge=challenge&code_challenge_method=plain"

        return authUrl
    }

    fun getAccessToken(callbackUrl: String, code: String): TwitterAccessTokenVo? {
        try {
            val client = OkHttpClient().newBuilder().build()
            val mediaType: MediaType? = "application/x-www-form-urlencoded".toMediaTypeOrNull() ?: return null

            val body: RequestBody? = RequestBody.create(
                mediaType,
                "grant_type=authorization_code&redirect_uri=${callbackUrl}&code=${code}&code_verifier=challenge"
            )

            val authorization = "Basic ${Base64.getEncoder().encodeToString("${clientId}:${clientSecret}".toByteArray(Charsets.US_ASCII))}"
            val request: Request = Request.Builder()
                .url("https://api.twitter.com/2/oauth2/token")
                .method("POST", body)
                .addHeader("Authorization", authorization)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build() ?: return null
            val response: Response = client.newCall(request).execute()

            if (response.code == 200) {
                response.body?.let {
                    val json = it.string()

                    val mapper = ObjectMapper()
                    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    val res = mapper.readValue<TwitterAccessTokenVo>(json, TwitterAccessTokenVo::class.java)

                    return res
                }
            }
        } catch (e: Exception) {
            logger.warn(e.toString())
        }

        return null;
    }

    fun getUserId(accessToken: String): TwitterUserMeVo? {
        try {
            val client = OkHttpClient().newBuilder()
                .build()
            val mediaType = "text/plain".toMediaTypeOrNull()
            val request: Request = Request.Builder()
                .url("https://api.twitter.com/2/users/me")
                .method("GET", null)
                .addHeader("Authorization", "Bearer ${accessToken}")
                .build()
            val response = client.newCall(request).execute()

            if (response.code == 200) {
                response.body?.let {
                    val json = it.string()

                    val mapper = ObjectMapper()
                    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                    val res = mapper.readValue(json, object : TypeReference<TwitterResVo<TwitterUserMeVo?>?>() {})
                    return res?.data
                }
            }
        } catch (e: Exception) {
            logger.warn(e.toString())
        }

        return null;

    }

    fun follow(accessToken: String, snsId: String, followId: String): Boolean? {
        try {
            val client = OkHttpClient().newBuilder()
                .build()
            val mediaType = "application/json".toMediaTypeOrNull()
            val body: RequestBody = RequestBody.create(mediaType, "{\r\n    \"target_user_id\": \"${followId}\"\r\n}")
            val request: Request = Request.Builder()
                .url("https://api.twitter.com/2/users/${snsId}/following")
                .method("POST", body)
                .addHeader("Authorization", "Bearer ${accessToken}")
                .addHeader("Content-Type", "application/json")
                .build()
            val response = client.newCall(request).execute()

            if (response.code == 200) {
                response.body?.let {
                    val json = it.string()

                    val mapper = ObjectMapper()
                    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                    val res = mapper.readValue(json, object : TypeReference<TwitterResVo<TwitterUserFollowingVo?>?>() {})
                    return res?.data?.following
                }
            }
        } catch (e: Exception) {
            logger.warn(e.toString())
        }

        return null;

    }

}
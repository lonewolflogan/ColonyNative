package com.colony.nativeapp.network

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import org.json.JSONObject
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class ColonyCookieJar(context: Context) : CookieJar {

    private val prefs = context.applicationContext
        .getSharedPreferences("colony_secure_session", Context.MODE_PRIVATE)

    private val alias = "colony_session_encryption_key"
    private val cookieName = "colony_session_v2"

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val cookie = cookies.firstOrNull { it.name() == cookieName } ?: return

        if (cookie.expiresAt() <= System.currentTimeMillis()) {
            clear()
            return
        }

        val data = JSONObject().apply {
            put("name", cookie.name())
            put("value", cookie.value())
            put("domain", cookie.domain())
            put("hostOnly", cookie.hostOnly())
            put("path", cookie.path())
            put("expiresAt", cookie.expiresAt())
            put("secure", cookie.secure())
            put("httpOnly", cookie.httpOnly())
        }

        try {
            val encrypted = encrypt(data.toString().toByteArray(Charsets.UTF_8))
            prefs.edit().putString("session_cookie", encrypted).commit()
        } catch (_: Exception) {
            clear()
        }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val encrypted = prefs.getString("session_cookie", null) ?: return emptyList()

        return try {
            val json = JSONObject(String(decrypt(encrypted), Charsets.UTF_8))

            val builder = Cookie.Builder()
                .name(json.getString("name"))
                .value(json.getString("value"))
                .path(json.getString("path"))
                .expiresAt(json.getLong("expiresAt"))

            if (json.getBoolean("hostOnly")) {
                builder.hostOnlyDomain(json.getString("domain"))
            } else {
                builder.domain(json.getString("domain"))
            }

            if (json.getBoolean("secure")) builder.secure()
            if (json.getBoolean("httpOnly")) builder.httpOnly()

            val cookie = builder.build()

            if (cookie.expiresAt() <= System.currentTimeMillis()) {
                clear()
                emptyList()
            } else if (cookie.matches(url)) {
                listOf(cookie)
            } else {
                emptyList()
            }
        } catch (_: Exception) {
            clear()
            emptyList()
        }
    }

    fun clear() {
        prefs.edit().remove("session_cookie").commit()
    }

    private fun getSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
            load(null)
        }

        val existing = keyStore.getKey(alias, null)
        if (existing is SecretKey) return existing

        val generator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            "AndroidKeyStore"
        )

        generator.init(
            KeyGenParameterSpec.Builder(
                alias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setRandomizedEncryptionRequired(true)
                .build()
        )

        return generator.generateKey()
    }

    private fun encrypt(input: ByteArray): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())

        val encrypted = cipher.doFinal(input)
        val combined = cipher.iv + encrypted

        return Base64.encodeToString(combined, Base64.NO_WRAP)
    }

    private fun decrypt(input: String): ByteArray {
        val combined = Base64.decode(input, Base64.NO_WRAP)
        require(combined.size > 12)

        val iv = combined.copyOfRange(0, 12)
        val encrypted = combined.copyOfRange(12, combined.size)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(
            Cipher.DECRYPT_MODE,
            getSecretKey(),
            GCMParameterSpec(128, iv)
        )

        return cipher.doFinal(encrypted)
    }
}

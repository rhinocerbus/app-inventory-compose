package com.piledrive.rip_compose.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.nio.charset.Charset
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Objects
import java.util.UUID

object UUIDv5 {
	private val UTF8: Charset = Charset.forName("UTF-8")
	val NAMESPACE_DNS: UUID = UUID.fromString("6ba7b810-9dad-11d1-80b4-00c04fd430c8")
	val NAMESPACE_URL: UUID = UUID.fromString("6ba7b811-9dad-11d1-80b4-00c04fd430c8")
	val NAMESPACE_OID: UUID = UUID.fromString("6ba7b812-9dad-11d1-80b4-00c04fd430c8")
	val NAMESPACE_X500: UUID = UUID.fromString("6ba7b814-9dad-11d1-80b4-00c04fd430c8")

	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
	fun nameUUIDFromNamespaceAndString(namespace: UUID?, name: String): UUID {
		return nameUUIDFromNamespaceAndBytes(
			namespace, Objects.requireNonNull(name, "name == null").toByteArray(
				UTF8
			)
		)
	}

	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
	fun nameUUIDFromNamespaceAndBytes(namespace: UUID?, name: ByteArray): UUID {
		val md: MessageDigest
		try {
			md = MessageDigest.getInstance("SHA-1")
		} catch (nsae: NoSuchAlgorithmException) {
			throw InternalError("SHA-1 not supported")
		}
		namespace ?: run {
			throw InternalError("namespace is null")
		}
		md.update(toBytes(Objects.requireNonNull(namespace, "")))
		md.update(Objects.requireNonNull(name, "name is null"))
		val sha1Bytes = md.digest()
		sha1Bytes[6] = (sha1Bytes[6].toInt() and 0x0f).toByte() /* clear version        */
		sha1Bytes[6] = (sha1Bytes[6].toInt() or 0x50).toByte() /* set to version 5     */
		sha1Bytes[8] = (sha1Bytes[8].toInt() and 0x3f).toByte() /* clear variant        */
		sha1Bytes[8] = (sha1Bytes[8].toInt() or 0x80).toByte() /* set to IETF variant  */
		return fromBytes(sha1Bytes)
	}

	private fun fromBytes(data: ByteArray): UUID {
		// Based on the private UUID(bytes[]) constructor
		var msb: Long = 0
		var lsb: Long = 0
		assert(data.size >= 16)
		for (i in 0..7) msb = (msb shl 8) or (data[i].toInt() and 0xff).toLong()
		for (i in 8..15) lsb = (lsb shl 8) or (data[i].toInt() and 0xff).toLong()
		return UUID(msb, lsb)
	}

	private fun toBytes(uuid: UUID): ByteArray {
		// inverted logic of fromBytes()
		val out = ByteArray(16)
		val msb = uuid.mostSignificantBits
		val lsb = uuid.leastSignificantBits
		for (i in 0..7) out[i] = ((msb shr ((7 - i) * 8)) and 0xffL).toByte()
		for (i in 8..15) out[i] = ((lsb shr ((15 - i) * 8)) and 0xffL).toByte()
		return out
	}
}
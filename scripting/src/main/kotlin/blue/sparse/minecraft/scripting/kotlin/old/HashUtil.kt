@file:Suppress("UnstableApiUsage")

package blue.sparse.minecraft.scripting.kotlin.old

import com.google.common.hash.Hashing
import java.math.BigInteger

fun hashSHA1(source: String): String {
	val hashCode = Hashing.sha1().hashString(source, Charsets.UTF_8)
	return BigInteger(hashCode.asBytes()).abs().toString(36).toUpperCase()
//	return Base64.getEncoder().encodeToString(hashCode.asBytes())
}
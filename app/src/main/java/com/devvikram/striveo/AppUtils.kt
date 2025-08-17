package com.devvikram.striveo

import java.security.MessageDigest
import java.security.SecureRandom

class AppUtils {

    companion object{

        fun isValidEmail(email: String): Boolean {
            val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
            return emailRegex.matches(email)
        }


        fun generateStrongPassword(length: Int = 12): String {
            val upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
            val lower = "abcdefghijklmnopqrstuvwxyz"
            val digits = "0123456789"
            val special = "!@#\$%^&*()-_=+[]{};:,.<>?/|"

            val allChars = upper + lower + digits + special
            val random = SecureRandom()

            // Ensure at least one character from each category
            val password = StringBuilder().apply {
                append(upper[random.nextInt(upper.length)])
                append(lower[random.nextInt(lower.length)])
                append(digits[random.nextInt(digits.length)])
                append(special[random.nextInt(special.length)])
                repeat(length - 4) {
                    append(allChars[random.nextInt(allChars.length)])
                }
            }.toList().shuffled(random).joinToString("")

            return password
        }

        fun hashPassword(password: String): String {
            val bytes = password.toByteArray()
            val digest = MessageDigest.getInstance("SHA-256").digest(bytes)
            return digest.joinToString("") { "%02x".format(it) }
        }



    }

}
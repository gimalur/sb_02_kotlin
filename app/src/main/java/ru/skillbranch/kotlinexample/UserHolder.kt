package ru.skillbranch.kotlinexample

import androidx.annotation.VisibleForTesting

object UserHolder {
    private val map = mutableMapOf<String, User>()

    fun registerUser(
            fullName: String,
            email: String,
            password: String
    ): User {
        val user = User.makeUser(fullName, email = email, password = password)
        if (user.login in map ) {
            throw IllegalArgumentException("A user with this email already exists")
        }
        map[user.login] = user
        return user
    }

    fun registerUserByPhone(
            fullName: String,
            rawPhone: String
    ): User {
        validatePhone(rawPhone)
        val user = User.makeUser(fullName, phone = rawPhone)
        if (rawPhone in map ) {
            throw IllegalArgumentException("A user with this phone already exists")
        }
        map[rawPhone] = user
        return user
    }

    private fun validatePhone(rawPhone: String) {
        if ("""[A-Za-z]+""".toRegex().matches(rawPhone) ||
                !rawPhone.startsWith("+") ||
                rawPhone.filter { it.isDigit() }.length != 11)
        throw IllegalArgumentException("Enter a valid phone number starting with a + and containing 11 digits")
    }

    fun loginUser(login: String, password: String): String? =
            map[login.trim()]?.let {
                if (it.checkPassword(password)) it.userInfo
                else null
            }

    fun requestAccessCode(login: String) {
        map[login]?.requestAccessCode()
    }

    fun importUsers(list: List<String>): List<User> = list.mapNotNull { importUser(it) }

    private fun importUser(csv: String): User {
        val (fullName, email, encrypted, rawPhone) = csv.split(";")
        val (salt, passwordHash) = encrypted.split(":")
        return registerImportedUser(
            fullName,
            email.nullIfBlank(),
            rawPhone.nullIfBlank(),
            salt,
            passwordHash
        )
    }

    private fun registerImportedUser(
        fullName: String,
        email: String? = null,
        rawPhone: String? = null,
        salt: String,
        passwordHash: String
    ): User {
        val user = User.makeUser(
            fullName,
            email,
            rawPhone,
            salt,
            passwordHash
        )
        if (rawPhone != null) {
            validatePhone(rawPhone)
            if (rawPhone in map ) {
                throw IllegalArgumentException("A user with this phone already exists")
            }
            map[rawPhone] = user
        } else {
            if (user.login in map ) {
                throw IllegalArgumentException("A user with this email already exists")
            }
            map[user.login] = user
        }
        return user
    }

    private fun String.nullIfBlank() = if (isNullOrBlank()) null else this

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun clearHolder() {
        map.clear()
    }
}


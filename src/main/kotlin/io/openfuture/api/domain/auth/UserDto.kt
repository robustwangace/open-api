package io.openfuture.api.domain.auth

import io.openfuture.api.entity.auth.User

/**
 * @author Kadach Alexey
 */
data class UserDto(
        val id: Long,
        val credits: Int,
        val openKeys: List<OpenKeyDto>
) {

    constructor(user: User) : this(user.id, user.credits, user.openKeys.filter { it.enabled }.map { OpenKeyDto(it) })

}
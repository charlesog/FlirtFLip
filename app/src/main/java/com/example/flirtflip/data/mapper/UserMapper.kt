package com.example.flirtflip.data.mapper

import com.example.flirtflip.data.entity.UserEntity
import com.example.flirtflip.data.entity.UserProfileEntity
import com.example.flirtflip.domain.model.Gender
import com.example.flirtflip.domain.model.Hobby
import com.example.flirtflip.domain.model.Location
import com.example.flirtflip.domain.model.User
import com.example.flirtflip.domain.model.UserProfile

fun User.toEntity(): UserEntity {
    return UserEntity(
        id = this.id,
        email = this.email,
        password = this.password,
        profile = this.profile.toEntity()
    )
}

fun UserEntity.toDomain(): User {
    return User(
        id = this.id,
        email = this.email,
        password = this.password,
        profile = this.profile.toDomain()
    )
}

fun UserProfile.toEntity(): UserProfileEntity {
    return UserProfileEntity(
        name = this.name,
        firstSurname = this.firstSurname,
        secondSurname = this.secondSurname,
        age = this.age,
        gender = this.gender.name,
        bio = this.bio,
        hobbies = this.hobbies.map { it.name },
        mainPhotoUrl = this.mainPhotoUrl,
        galleryPhotoUrls = this.galleryPhotoUrls,
        job = this.job,
        latitude = this.location?.latitude,
        longitude = this.location?.longitude
    )
}

fun UserProfileEntity.toDomain(): UserProfile {
    return UserProfile(
        name = this.name,
        firstSurname = this.firstSurname,
        secondSurname = this.secondSurname,
        age = this.age,
        gender = try { Gender.valueOf(this.gender) } catch (e: Exception) { Gender.UNDEFINED },
        bio = this.bio,
        hobbies = this.hobbies.mapNotNull {
            try { Hobby.valueOf(it) } catch (e: Exception) { null }
        },
        mainPhotoUrl = this.mainPhotoUrl,
        galleryPhotoUrls = this.galleryPhotoUrls,
        job = this.job,
        location = if (latitude != null && longitude != null) {
            Location(latitude, longitude)
        } else null
    )
}
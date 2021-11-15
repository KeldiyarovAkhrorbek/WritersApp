package com.projects.writers.models

import java.io.Serializable

class Writer : Serializable {
    var id: String? = null
    var name: String? = null
    var birthYear: String? = null
    var deathYear: String? = null
    var type: String? = null
    var info: String? = null
    var photoUrl: String? = null
    var saved: Boolean = false

    constructor()
    constructor(
        id: String?,
        name: String?,
        birthYear: String?,
        deathYear: String?,
        type: String?,
        info: String?,
        photoUrl: String?,
        saved: Boolean
    ) {
        this.id = id
        this.name = name
        this.birthYear = birthYear
        this.deathYear = deathYear
        this.type = type
        this.info = info
        this.photoUrl = photoUrl
        this.saved = saved
    }

    override fun toString(): String {
        return "Writer(id=$id, name=$name, birthYear=$birthYear, deathYear=$deathYear, type=$type, info=$info, photoUrl=$photoUrl, saved=$saved)"
    }


}